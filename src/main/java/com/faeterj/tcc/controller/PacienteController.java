package com.faeterj.tcc.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreatePacienteDTO;
import com.faeterj.tcc.dto.ListaPacientesDTO;
import com.faeterj.tcc.dto.RequestResponseDTO;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.service.PacienteService;
import com.faeterj.tcc.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@Tag(name = "Paciente", description = "Endpoints para gerenciamento de Pacientes e suas informações.")
@SecurityRequirement(name = "Auth JWT")
public class PacienteController {

    private final PacienteService pacienteService;
    private final UserService userService;

    public PacienteController(PacienteService pacienteService, UserService userService) {
        this.pacienteService = pacienteService;
        this.userService = userService;
    }

    @Operation(
        summary = "Crie um novo perfil de paciente em sua conta.",
        description = "Crie um perfil de paciente em sua conta no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Paciente criado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Paciente criado com sucesso.\", \"status\": 201}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.", content = @Content(
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
    @PostMapping("/paciente")
    public ResponseEntity<RequestResponseDTO> criarPaciente(@RequestBody CreatePacienteDTO dto, JwtAuthenticationToken token) {
        try 
        {  
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            pacienteService.criarPaciente(dto, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponseDTO("Paciente criado com sucesso.", 201));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }
    
    @Operation(
        summary = "Remova um perfil de paciente da sua conta.",
        description = "Dado o ID de um paciente, faça a remoção dele e de seus registros no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Paciente Deletado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Paciente Deletado com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "Usuário não autorizado a deletar este paciente.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Usuário não autorizado a deletar este paciente.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Paciente não encontrado.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @DeleteMapping("/paciente/{id}")
    public ResponseEntity<RequestResponseDTO> deletaPaciente (@PathVariable("id") Long idPaciente, JwtAuthenticationToken token)
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            pacienteService.deletarPaciente(idPaciente, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Paciente Deletado com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Modifique um perfil de paciente da sua conta.",
        description = "Dado o ID de um paciente, faça a alteração dele e de seus registros no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Paciente editado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Paciente editado com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "Usuário não autorizado a editar este paciente.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Usuário não autorizado a editar este paciente.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Paciente não encontrado.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PutMapping("/paciente/{id}")
    public ResponseEntity<RequestResponseDTO> editaPaciente(@PathVariable("id") Long idPaciente, JwtAuthenticationToken token, @RequestBody CreatePacienteDTO dto)
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            pacienteService.editarPaciente(idPaciente, user, dto);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Paciente Editado com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

        @Operation(
        summary = "Visualize a lista com todos os pacientes da conta.",
        description = "Esse endpoint retorna uma lista paginada de pacientes associados ao usuário autenticado.",
        responses = {            
            @ApiResponse(
                responseCode = "200",
                description = "Lista de profissionais retornada com sucesso.",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ListaPacientesDTO.class),
                    examples = @ExampleObject(value = """
                        {
                            "listaPacientes": [
                                {
                                    "pacienteId": 0,
                                    "nomePaciente": "Nome",
                                    "sobrenomePaciente": "Sobrenome",
                                    "dataCriacao": "2024-01-01T00:00:00.000Z"
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
            @ApiResponse(responseCode = "404", description = "O usuário informado na requisição não foi encontrado no sistema.", content = @Content(
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
    @GetMapping("/pacientes")
    public ResponseEntity<?> listaPacientesUser(@RequestParam(value = "page", defaultValue = "0") int page, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            var listaPacientes = pacienteService.listarPacientes(page, pageSize, user);
            return ResponseEntity.status(HttpStatus.OK).body(listaPacientes);
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }
}
