package com.faeterj.tcc.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateProfissionalDTO;
import com.faeterj.tcc.dto.EditaProfissionalDTO;
import com.faeterj.tcc.dto.ListaProfissionaisDTO;
import com.faeterj.tcc.dto.RequestResponseDTO;
import com.faeterj.tcc.dto.ReturnProfissionalDTO;
import com.faeterj.tcc.dto.statusRequisitadoDTO;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.service.ProfissionalSaudeService;
import com.faeterj.tcc.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;

@Validated
@RestController
@Tag(name = "Profissional de Saúde", description = "Endpoints para gerenciamento de Profissionais de Saúde e suas informações.")
public class ProfissionalSaudeController {

    private final UserService userService;
    private final ProfissionalSaudeService profissionalSaudeService;

    public ProfissionalSaudeController(UserService userService, ProfissionalSaudeService profissionalSaudeService) {
        this.userService = userService;
        this.profissionalSaudeService = profissionalSaudeService;
    }

    @Operation(
        summary = "Visualize a lista com todos os profissionais de saúde.",
        description = "Veja todos os profissionais de saúde registradas no sistema.\n Endpoint Restrito somente a Usuários Administradores Logados.",
        responses = {            
            @ApiResponse(
                responseCode = "200",
                description = "Lista de profissionais retornada com sucesso",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ListaProfissionaisDTO.class),
                    examples = @ExampleObject(value = """
                        {
                            "listaProfissionais": [
                                {
                                    "profissionalId": 0,
                                    "nomeProfissional": "Nome Sobrenome",
                                    "especialidadeProfissional": "Especialidade",
                                    "numeroClasseConselho": "000000",
                                    "estadoProfissional": "UF",
                                    "statusProfissional": {
                                        "status": "REGULAR",
                                        "ultimaModificacao": "2024-01-01T00:00:00.000Z"
                                    },
                                    "profissionalDataCriacao": "2024-01-01T00:00:00.000Z"
                                }
                            ],
                            "page": 0,
                            "pageSize": 10,
                            "totalPages": 1,
                            "totalElements": 1
                        }
                    """)
                )
            ),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para visualizar essas informações.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para visualizar essa lista.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "O usuário informado (credenciais ou token) na requisição não foram encontrados no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @GetMapping("/profissionais")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @SecurityRequirement(name = "Auth JWT")
    public ResponseEntity<?> listaProfissionais(@RequestParam(value = "page", defaultValue = "0") int page, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            JwtAuthenticationToken token) 
    {
        try 
        {
            userService.acharUserPorId(UUID.fromString(token.getName()));
            var listaProfissionais = profissionalSaudeService.listarProfissionais(page, pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(listaProfissionais);
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Adicione um Médico ao Banco de Dados do sistema.",
        description = "Dadas as credenciais requisitadas, Adicione o perfil de um médico a aplicação.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Profissional de Saúde adicionado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Profissional criado com sucesso.\", \"status\": 201}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "409", description = "As credenciais informadas estão em conflito com dados já existentes no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Profissional já existente.\", \"status\": 409}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PostMapping("/profissional")
    //@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<RequestResponseDTO> criarProfissional(@RequestBody CreateProfissionalDTO dto, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            profissionalSaudeService.criarProfissional(dto, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponseDTO("Profissional criado com sucesso.", 201));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Modifique as informações sobre um Médico no sistema.",
        description = "Dado o ID de um profissional, altere os dados do profissional no sistema.\n Endpoint Restrito somente a Usuários Administradores Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Profissional de Saúde modificado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Profissional editado com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "400", description = "O Status de Profissional enviado na requisição é inválido.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O Status de Profissional enviado é inválido.\", \"status\": 400}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para realizar essa modificação.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Usuário não autorizado a editar este Profissional.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "O Profissional desejado na requisição não foi encontrado no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Profissional não encontrado.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PutMapping("/profissional/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @SecurityRequirement(name = "Auth JWT")
    public ResponseEntity<RequestResponseDTO> editaProfissional(@PathVariable("id") Long idProfissional, @RequestBody EditaProfissionalDTO dto, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            profissionalSaudeService.editarProfissional(idProfissional, user, dto);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Profissional Editado com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Remova todas as informações sobre um Médico no sistema.",
        description = "Dado o ID de um profissional, delete todos os seus dados incluidos no sistema.\n Endpoint Restrito somente a Usuários Administradores Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Profissional de Saúde removido com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Profissional excluido com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para realizar essa exclusão.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Usuário não autorizado a excluir este Profissional.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "O Profissional desejado na requisição não foi encontrado no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Profissional não encontrado.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @DeleteMapping("/profissional/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @SecurityRequirement(name = "Auth JWT")
    public ResponseEntity<RequestResponseDTO> deletaProfissional(@PathVariable("id") Long idProfissional, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            profissionalSaudeService.deletarProfissional(idProfissional, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Profissional excluido com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Visualize as informações sobre um médico no sistema.",
        description = "Dado o numero de registro e o estado do profissional, consulte todos os seus dados incluidos no sistema.\n Endpoint público a todos visistantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Profissional de Saúde encontrado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReturnProfissionalDTO.class), 
                examples = @ExampleObject(value = """
                {
                    "profissionalId": 0,
                    "nomeProfissional": "Nome Exemplo",
                    "especialidadeProfissional": "Especialidade",
                    "numeroClasseConselho": "000000",
                    "estadoProfissional": "UF",
                    "statusProfissional": {
                        "status": "REGULAR",
                        "ultimaModificacao": "2024-01-01T00:00:00.000Z"
                    },
                    "profissionalDataCriacao": "2024-01-01T00:00:00.000Z"
                }
            """)
            )),
            @ApiResponse(responseCode = "400", description = "Os parâmetros obrigatórios da requisição não foram enviados corretamente.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Os parâmetros obrigatórios da requisição não foram enviados corretamente.\", \"status\": 400}")
            )),
            @ApiResponse(responseCode = "404", description = "O Profissional desejado na requisição não foi encontrado no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Profissional não encontrado.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @GetMapping("/profissional")
    public ResponseEntity<?> buscaProfissionalPorNumeroConselho(@RequestParam @NotBlank(message = "O código de registro não pode estar vazio.") String codigoRegistro, 
                                                                @NotBlank(message = "O campo UF não pode estar vazio.") @RequestParam String UF) 
    {
        try 
        {
            var profissional = profissionalSaudeService.buscarProfissionalDTOPorConselhoEstado(codigoRegistro, UF);
            return ResponseEntity.status(HttpStatus.OK).body(profissional);
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Visualize as informações sobre um médico no sistema.",
        description = "Dado o ID de um profissional de saúde, consulte todos os seus dados incluidos no sistema.\n Endpoint público a todos visistantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Profissional de Saúde encontrado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReturnProfissionalDTO.class), 
                examples = @ExampleObject(value = """
                {
                    "profissionalId": 0,
                    "nomeProfissional": "Nome Exemplo",
                    "especialidadeProfissional": "Especialidade",
                    "numeroClasseConselho": "000000",
                    "estadoProfissional": "UF",
                    "statusProfissional": {
                        "status": "REGULAR",
                        "ultimaModificacao": "2024-01-01T00:00:00.000Z"
                    },
                    "profissionalDataCriacao": "2024-01-01T00:00:00.000Z"
                }
            """)
            )),
            @ApiResponse(responseCode = "404", description = "O Profissional desejado na requisição não foi encontrado no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Profissional não encontrado.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @GetMapping("/profissional/{id}")
    public ResponseEntity<?> buscaProfissional(@PathVariable("id") Long idProfissional) 
    {
        try 
        {
            var profissional = profissionalSaudeService.buscarProfissional(idProfissional);
            return ResponseEntity.status(HttpStatus.OK).body(profissional);
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Faça a alteração apenas do atributo 'statusProfissional' de um médico.",
        description = "Dado o ID de um profissional, Troque seu status no sistema.\n Endpoint Restrito somente a Usuários Administradores Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Status do Profissional alterado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Status do Profissional alterado com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "400", description = "O Status de Profissional enviado é inválido.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O Status de Profissional enviado é inválido.\", \"status\": 400}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "Usuário não autorizado a editar este Profissional.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Usuário não autorizado a editar este Profissional.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "O Profissional desejado na requisição não foi encontrado no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Profissional não encontrado.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PatchMapping("/statusProfissional/{idProfissional}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @SecurityRequirement(name = "Auth JWT")
    public ResponseEntity<RequestResponseDTO> verificaProfissional(@PathVariable("idProfissional") Long idProfissional, @RequestBody statusRequisitadoDTO statusDTO, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            profissionalSaudeService.verificaProfissional(idProfissional, statusDTO.statusProfissional(), user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Status do Profissional alterado com sucesso.", 200));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

}
