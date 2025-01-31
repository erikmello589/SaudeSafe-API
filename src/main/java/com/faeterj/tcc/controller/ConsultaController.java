package com.faeterj.tcc.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateAtestadoDTO;
import com.faeterj.tcc.dto.CreateConsultaDTO;
import com.faeterj.tcc.dto.CreatePedidoExameDTO;
import com.faeterj.tcc.dto.CreateReceitaDTO;
import com.faeterj.tcc.dto.ListaConsultasDTO;
import com.faeterj.tcc.dto.RequestResponseDTO;
import com.faeterj.tcc.dto.ReturnConsultaCompletaDTO;
import com.faeterj.tcc.model.Atestado;
import com.faeterj.tcc.model.PedidoExame;
import com.faeterj.tcc.model.Receita;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.service.AtestadoService;
import com.faeterj.tcc.service.ConsultaService;
import com.faeterj.tcc.service.PedidoExameService;
import com.faeterj.tcc.service.ReceitaService;
import com.faeterj.tcc.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Consultas", description = "Endpoints para gerenciamento de Consultas e seus anexos.")
@SecurityRequirement(name = "Auth JWT")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final UserService userService;
    private final AtestadoService atestadoService;
    private final ReceitaService receitaService;
    private final PedidoExameService pedidoExameService;

    public ConsultaController(ConsultaService consultaService, UserService userService, AtestadoService atestadoService,
            ReceitaService receitaService, PedidoExameService pedidoExameService) {
        this.consultaService = consultaService;
        this.userService = userService;
        this.atestadoService = atestadoService;
        this.receitaService = receitaService;
        this.pedidoExameService = pedidoExameService;
    }

    @Operation(
        summary = "Armazene sua consulta.",
        description = "Dado o ID de um Paciente, crie uma consulta.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Consulta criada com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta criada com sucesso.\", \"status\": 201}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para criar consultas para este paciente.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para criar consultas para este paciente.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "O paciente informado na requisição não foi encontrado.", content = @Content(
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
    @PostMapping("/consulta/paciente/{idPaciente}")
    public ResponseEntity<RequestResponseDTO> criarConsulta(@PathVariable("idPaciente") Long idPaciente, @RequestBody CreateConsultaDTO dto, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            consultaService.criarConsulta(idPaciente, dto, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponseDTO("Consulta criada com sucesso.", 201));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Remova a sua consulta.",
        description = "Dado o ID de uma consulta, faça a remoção dela no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Consulta removida com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta excluida com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para remover consultas deste paciente.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Usuário não autorizado a deletar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @DeleteMapping("/consulta/{id}")
    public ResponseEntity<RequestResponseDTO> deleteConsulta(@PathVariable("id") Long idConsulta, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            consultaService.deletarConsulta(idConsulta, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Consulta excluida com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Altere informações na sua consulta.",
        description = "Dado o ID de uma consulta, faça a alteração dela no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Consulta alterada com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta editada com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PutMapping("/consulta/{id}")
    public ResponseEntity<RequestResponseDTO> editarConsulta(@PathVariable("id") Long idConsulta, @RequestBody CreateConsultaDTO dto, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            consultaService.editarConsulta(idConsulta, user, dto);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Consulta Editada com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Veja os dados completos de uma consulta.",
        description = "Dado o ID de uma consulta, veja todas suas informações no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Consulta exibida com sucesso.", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ReturnConsultaCompletaDTO.class),
            examples = @ExampleObject(value = "{"
                    + "\"consultaId\": 0,"
                    + "\"paciente\": {"
                    + "    \"pacienteId\": 0,"
                    + "    \"nomePaciente\": \"Nome do Paciente\","
                    + "    \"sobrenomePaciente\": \"Sobrenome do Paciente\","
                    + "    \"dataCriacao\": \"2024-01-01T00:00:00.000Z\""
                    + "},"
                    + "\"profissionalConsulta\": {"
                    + "    \"profissionalId\": 0,"
                    + "    \"nomeProfissional\": \"Nome do Profissional\","
                    + "    \"especialidadeProfissional\": \"Especialidade\","
                    + "    \"numeroClasseConselho\": \"0000000\","
                    + "    \"estadoProfissional\": \"XX\","
                    + "    \"statusProfissional\": {"
                    + "        \"status\": \"STATUS\","
                    + "        \"ultimaModificacao\": \"2024-01-01T00:00:00.000Z\""
                    + "    },"
                    + "    \"profissionalDataCriacao\": \"2024-01-01T00:00:00.000Z\""
                    + "},"
                    + "\"estabelecimentoSaude\": {"
                    + "    \"estabelecimentoId\": 0,"
                    + "    \"nomeEstabelecimento\": \"Nome do Estabelecimento\","
                    + "    \"cepEstabelecimento\": \"00000000\","
                    + "    \"enderecoEstabelecimento\": \"Endereço do Estabelecimento\","
                    + "    \"estabelecimentoDataCriacao\": \"2024-01-01T00:00:00.000Z\""
                    + "},"
                    + "\"motivoConsulta\": \"Motivo da Consulta\","
                    + "\"observacaoConsulta\": \"Observação da Consulta\","
                    + "\"consultaData\": \"2024-01-01T00:00:00.000Z\","
                    + "\"atestado\": {"
                    + "    \"atestadoId\": 0,"
                    + "    \"temAnexo\": false,"
                    + "    \"tipoAnexo\": \"application/pdf\","
                    + "    \"periodoAfastamento\": \"X Dias\","
                    + "    \"observacaoAtestado\": \"Observação do Atestado\","
                    + "    \"atestadoDataCriacao\": \"2024-01-01T00:00:00.000Z\""
                    + "},"
                    + "\"receita\": {"
                    + "    \"receitaId\": 0,"
                    + "    \"medicamentos\": ["
                    + "        {"
                    + "            \"medicamentoId\": 0,"
                    + "            \"nomeMedicamento\": \"Nome do Medicamento\","
                    + "            \"doseMedicamento\": \"Dosagem\","
                    + "            \"qtdDoseMedicamento\": \"Quantidade\","
                    + "            \"intervaloMedicamento\": \"Intervalo\","
                    + "            \"periodoMedicamento\": \"Período\","
                    + "            \"observacaoMedicamento\": \"Observação do Medicamento\""
                    + "        }"
                    + "    ],"
                    + "    \"observacaoReceita\": \"Observação da Receita\","
                    + "    \"temAnexo\": false,"
                    + "    \"tipoAnexo\": \"image/jpeg\","
                    + "    \"receitaDataCriacao\": \"2024-01-01T00:00:00.000Z\""
                    + "},"
                    + "\"pedidoExame\": {"
                    + "    \"pedidoExameId\": 0,"
                    + "    \"temAnexo\": false,"
                    + "    \"tipoAnexo\": \"image/jpeg\","
                    + "    \"exames\": ["
                    + "        {"
                    + "            \"exameId\": 0,"
                    + "            \"tipoExame\": \"Tipo do Exame\","
                    + "            \"nomeExame\": \"Nome do Exame\","
                    + "            \"observacaoExame\": \"Observação do Exame\","
                    + "            \"situacaoExame\": \"Situação do Exame\""
                    + "        }"
                    + "    ],"
                    + "    \"pedidoDataCriacao\": \"2024-01-01T00:00:00.000Z\","
                    + "    \"pedidoObservacao\": \"Observação do Pedido de Exame\""
                    + "}"
                    + "}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para visualizar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para visualizar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<?> buscaConsultaCompleta(@PathVariable("consultaId") Long consultaId,
                                                    JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            ReturnConsultaCompletaDTO consultaEncontrada = consultaService.acharConsultaPorId(consultaId, user);
            return ResponseEntity.status(HttpStatus.OK).body(consultaEncontrada);
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Visualize todas as consultas de um PACIENTE.",
        description = "Ao informar o ID de um paciente, poderá ver todas as consultas registradas em seu nome.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de consultas exibida com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ListaConsultasDTO.class),
                examples = @ExampleObject(value = "{"
                        + "\"listaConsultas\": ["
                        + "    {"
                        + "        \"consultaId\": 0,"
                        + "        \"paciente\": {"
                        + "            \"pacienteId\": 0,"
                        + "            \"nomePaciente\": \"Nome do Paciente\","
                        + "            \"sobrenomePaciente\": \"Sobrenome do Paciente\","
                        + "            \"dataCriacao\": \"2024-01-01T00:00:00.000Z\""
                        + "        },"
                        + "        \"profissionalConsulta\": {"
                        + "            \"profissionalId\": 0,"
                        + "            \"nomeProfissional\": \"Nome do Profissional\","
                        + "            \"especialidadeProfissional\": \"Especialidade\","
                        + "            \"numeroClasseConselho\": \"0000000\","
                        + "            \"estadoProfissional\": \"XX\","
                        + "            \"statusProfissional\": {"
                        + "                \"status\": \"STATUS\","
                        + "                \"ultimaModificacao\": \"2024-01-01T00:00:00.000Z\""
                        + "            },"
                        + "            \"profissionalDataCriacao\": \"2024-01-01T00:00:00.000Z\""
                        + "        },"
                        + "        \"estabelecimentoSaude\": {"
                        + "            \"estabelecimentoId\": 0,"
                        + "            \"nomeEstabelecimento\": \"Nome do Estabelecimento\","
                        + "            \"cepEstabelecimento\": \"00000000\","
                        + "            \"enderecoEstabelecimento\": \"Endereço do Estabelecimento\","
                        + "            \"estabelecimentoDataCriacao\": \"2024-01-01T00:00:00.000Z\""
                        + "        },"
                        + "        \"motivoConsulta\": \"Motivo da Consulta\","
                        + "        \"observacaoConsulta\": \"Observação da Consulta\","
                        + "        \"consultaData\": \"2024-01-01T00:00:00.000Z\""
                        + "    }"
                        + "],"
                        + "\"page\": 0,"
                        + "\"pageSize\": 10,"
                        + "\"totalPages\": 1,"
                        + "\"totalElements\": 0"
                        + "}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para visualizar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para visualizar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @GetMapping("/consultas/paciente/{pacienteId}")
    public ResponseEntity<?> listaConsultasPaciente(@PathVariable("pacienteId") Long idPaciente, 
                                                    @RequestParam(value = "page", defaultValue = "0") int page, 
                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, 
                                                    JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            var listaConsultasPaciente = consultaService.listarConsultasPaciente(idPaciente, page, pageSize, user);
            return ResponseEntity.status(HttpStatus.OK).body(listaConsultasPaciente);
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Visualize todas as consultas de um USUÁRIO.",
        description = "Um usuário ao estar autenticado, poderá ver todas as consultas registradas em sua conta.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de consultas exibida com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ListaConsultasDTO.class),
                examples = @ExampleObject(value = "{"
                        + "\"listaConsultas\": ["
                        + "    {"
                        + "        \"consultaId\": 0,"
                        + "        \"paciente\": {"
                        + "            \"pacienteId\": 0,"
                        + "            \"nomePaciente\": \"Nome do Paciente\","
                        + "            \"sobrenomePaciente\": \"Sobrenome do Paciente\","
                        + "            \"dataCriacao\": \"2024-01-01T00:00:00.000Z\""
                        + "        },"
                        + "        \"profissionalConsulta\": {"
                        + "            \"profissionalId\": 0,"
                        + "            \"nomeProfissional\": \"Nome do Profissional\","
                        + "            \"especialidadeProfissional\": \"Especialidade\","
                        + "            \"numeroClasseConselho\": \"0000000\","
                        + "            \"estadoProfissional\": \"XX\","
                        + "            \"statusProfissional\": {"
                        + "                \"status\": \"STATUS\","
                        + "                \"ultimaModificacao\": \"2024-01-01T00:00:00.000Z\""
                        + "            },"
                        + "            \"profissionalDataCriacao\": \"2024-01-01T00:00:00.000Z\""
                        + "        },"
                        + "        \"estabelecimentoSaude\": {"
                        + "            \"estabelecimentoId\": 0,"
                        + "            \"nomeEstabelecimento\": \"Nome do Estabelecimento\","
                        + "            \"cepEstabelecimento\": \"00000000\","
                        + "            \"enderecoEstabelecimento\": \"Endereço do Estabelecimento\","
                        + "            \"estabelecimentoDataCriacao\": \"2024-01-01T00:00:00.000Z\""
                        + "        },"
                        + "        \"motivoConsulta\": \"Motivo da Consulta\","
                        + "        \"observacaoConsulta\": \"Observação da Consulta\","
                        + "        \"consultaData\": \"2024-01-01T00:00:00.000Z\""
                        + "    }"
                        + "],"
                        + "\"page\": 0,"
                        + "\"pageSize\": 10,"
                        + "\"totalPages\": 1,"
                        + "\"totalElements\": 0"
                        + "}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para visualizar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para visualizar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @GetMapping("/consultas")
    public ResponseEntity<?> listaConsultasUser(@RequestParam(value = "page", defaultValue = "0") int page, 
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            var listaConsultasUser = consultaService.listarConsultasUser(user, page, pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(listaConsultasUser);
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Adicione um atestado médico a sua consulta.",
        description = "Dado o ID de uma consulta, armazene o atestado a ela no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Atestado adicionado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Atestado adicionado com sucesso.\", \"status\": 201}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PostMapping("/atestado/consulta/{idConsulta}")
    public ResponseEntity<RequestResponseDTO> criarAtestado(
            @PathVariable("idConsulta") Long idConsulta, 
            @RequestPart CreateAtestadoDTO dto,
            @RequestParam(value = "file", required = false) MultipartFile file,
            JwtAuthenticationToken token) throws IOException {
        try {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            atestadoService.criarAtestado(idConsulta, dto, file, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponseDTO("Atestado criado com sucesso.", 201));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Modifique o atestado médico de uma consulta.",
        description = "Dado o ID de uma consulta, altere informações do atestado no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Atestado alterado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Atestado editado com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PutMapping("/atestado/consulta/{idConsulta}")
    public ResponseEntity<RequestResponseDTO> editarAtestado(
            @PathVariable("idConsulta") Long idConsulta, 
            @RequestPart CreateAtestadoDTO dto,
            @RequestParam(value = "file", required = false) MultipartFile file,
            JwtAuthenticationToken token) throws IOException {
        try {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));            
            atestadoService.editarAtestado(idConsulta, dto, file, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Atestado editado com sucesso.", 200));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Remova o atestado médico de uma consulta.",
        description = "Dado o ID de uma consulta, faça a remoção do atestado no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Atestado removido com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Atestado excluido com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @DeleteMapping("/atestado/consulta/{idConsulta}")
    public ResponseEntity<RequestResponseDTO> excluirAtestado(@PathVariable("idConsulta") Long idConsulta, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            atestadoService.excluirAtestado(idConsulta, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Atestado excluido com sucesso.", 200));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Visualize o anexo do atestado médico de uma consulta.",
        description = "Dado o ID de uma consulta, visualize o arquivo anexado do atestado médico associado a ela.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Anexo do atestado será exibido com sucesso (PDF, JPG ou PNG)."),
            @ApiResponse(responseCode = "204", description = "O Atestado médico não possui arquivo anexado."),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @GetMapping("/atestado/anexo/consulta/{idConsulta}")
    public ResponseEntity<?> buscarAnexoAtestado(@PathVariable("idConsulta") Long idConsulta, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            Atestado atestado = atestadoService.buscarAtestado(idConsulta, user);
            return ResponseEntity.status(HttpStatus.OK).header("Content-Type", atestado.getTipoAnexo()).body(atestado.getPdfAnexado());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Remova o anexo do atestado médico de uma consulta.",
        description = "Dado o ID de uma consulta, faça a remoção do anexo do atestado no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Anexo removido com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Anexo removido com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @DeleteMapping("/atestado/anexo/consulta/{idConsulta}")
    public ResponseEntity<RequestResponseDTO> excluirAnexoAtestado(@PathVariable("idConsulta") Long idConsulta, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            atestadoService.excluirAnexoAtestado(idConsulta, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Anexo do Atestado excluido com sucesso.", 200));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Adicione uma Receita médica a uma consulta.",
        description = "Dado o ID de uma consulta, Armazene uma receita e seus medicamentos a ela no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Receita Adicionada com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Receita criada com sucesso.\", \"status\": 201}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PostMapping("/receita/consulta/{idConsulta}")
    public ResponseEntity<RequestResponseDTO> criarReceita(
            @PathVariable("idConsulta") Long idConsulta, 
            @RequestPart CreateReceitaDTO dto,
            @RequestParam(value = "file", required = false) MultipartFile file,
            JwtAuthenticationToken token) throws IOException {
        try {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            receitaService.criarReceita(idConsulta, dto, file, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponseDTO("Receita criada com sucesso.", 201));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Modifique a Receita médica de uma consulta.",
        description = "Dado o ID de uma consulta, altere as informações da receita e seus medicamentos associados a ela no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Receita Alterada com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Receita editada com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PutMapping("/receita/consulta/{idConsulta}")
    public ResponseEntity<RequestResponseDTO> editarReceita(
            @PathVariable("idConsulta") Long idConsulta, 
            @RequestPart CreateReceitaDTO dto,
            @RequestParam(value = "file", required = false) MultipartFile file,
            JwtAuthenticationToken token) throws IOException {
        try {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));            
            receitaService.editarReceita(idConsulta, dto, file, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Receita editada com sucesso.", 200));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Remova a Receita médica de uma consulta.",
        description = "Dado o ID de uma consulta, faça a remoção da receita associada a ela no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Receita removida com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Receita excluida com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @DeleteMapping("/receita/consulta/{idConsulta}")
    public ResponseEntity<RequestResponseDTO> excluirReceita(@PathVariable("idConsulta") Long idConsulta, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            receitaService.excluirReceita(idConsulta, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Receita excluida com sucesso.", 200));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Visualize o anexo da Receita Médica de uma consulta.",
        description = "Dado o ID de uma consulta, visualize o arquivo anexado da Receita Médica associada a ela.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Anexo da Receita será exibida com sucesso (PDF, JPG ou PNG)."),
            @ApiResponse(responseCode = "204", description = "A Receita Médica desejada não possui arquivo anexado."),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @GetMapping("/receita/anexo/consulta/{idConsulta}")
    public ResponseEntity<?> buscarAnexoReceita(@PathVariable("idConsulta") Long idConsulta, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            Receita receita = receitaService.buscarReceita(idConsulta, user);
            return ResponseEntity.status(HttpStatus.OK).header("Content-Type", receita.getTipoAnexo()).body(receita.getPdfAnexado());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Remova o anexo da Receita médica de uma consulta.",
        description = "Dado o ID de uma consulta, faça a remoção do anexo da receita associada a ela no sistema.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Anexo removido com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"anexo excluido com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @DeleteMapping("/receita/anexo/consulta/{idConsulta}")
    public ResponseEntity<RequestResponseDTO> excluirAnexoReceita(@PathVariable("idConsulta") Long idConsulta, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            receitaService.excluirAnexoReceita(idConsulta, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Anexo da receita removido com sucesso.", 200));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }
    
    @Operation(
        summary = "Adicione um Pedido de Exames a uma consulta.",
        description = "Dado o ID de uma consulta, Armazene o pedido de exames a ela.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Pedido de Exames adicionado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Pedido de Exame criado com sucesso.\", \"status\": 201}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PostMapping("/pedidoExame/consulta/{idConsulta}")
    public ResponseEntity<RequestResponseDTO> criarPedidoExame(
            @PathVariable("idConsulta") Long idConsulta, 
            @RequestPart CreatePedidoExameDTO dto,
            @RequestParam(value = "file", required = false) MultipartFile file,
            JwtAuthenticationToken token) throws IOException {
        try {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            pedidoExameService.criarPedidoExame(idConsulta, dto, file, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponseDTO("Pedido de Exame criado com sucesso.", 201));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Modifique o Pedido de Exames de uma consulta.",
        description = "Dado o ID de uma consulta, Altere o pedido de exame associado a ela.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Pedido de Exames modificado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Pedido de Exame editado com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PutMapping("/pedidoExame/consulta/{idConsulta}")
    public ResponseEntity<RequestResponseDTO> editarReceita(
            @PathVariable("idConsulta") Long idConsulta, 
            @RequestPart CreatePedidoExameDTO dto,
            @RequestParam(value = "file", required = false) MultipartFile file,
            JwtAuthenticationToken token) throws IOException {
        try {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));            
            pedidoExameService.editarPedidoExame(idConsulta, dto, file, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Pedido de Exame editado com sucesso.", 200));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Remova o Pedido de Exames de uma consulta.",
        description = "Dado o ID de uma consulta, Fça a remoção do pedido de exame associado a ela.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Pedido de Exames removido com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Pedido de Exame excluido com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @DeleteMapping("/pedidoExame/consulta/{idConsulta}")
    public ResponseEntity<RequestResponseDTO> excluirPedidoExame(@PathVariable("idConsulta") Long idConsulta, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            pedidoExameService.excluirPedidoExame(idConsulta, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Pedido excluido com sucesso.", 200));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Visualize o anexo do Pedido de Exame de uma consulta.",
        description = "Dado o ID de uma consulta, visualize o arquivo anexado do Pedido de exame associado a ela.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Anexo do Pedido de Exame será exibido com sucesso (PDF, JPG ou PNG)."),
            @ApiResponse(responseCode = "204", description = "O Pedido de Exames desejado não possui arquivo anexado."),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @GetMapping("/pedidoExame/anexo/consulta/{idConsulta}")
    public ResponseEntity<?> buscarAnexoPedidoExame(@PathVariable("idConsulta") Long idConsulta, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            PedidoExame pedidoExame = pedidoExameService.buscarPedidoExame(idConsulta, user);
            return ResponseEntity.status(HttpStatus.OK).header("Content-Type", pedidoExame.getTipoAnexo()).body(pedidoExame.getPdfAnexado());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Remova o Anexo do Pedido de Exames de uma consulta.",
        description = "Dado o ID de uma consulta, Faça a remoção do arquivo anexado no pedido de exame associado a ela.\n Endpoint Restrito somente a Usuários Logados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Anexo do Pedido de Exame removido com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Anexo do Pedido de Exame excluido com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "401", description = "O usuário não está autenticado."),
            @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para modificar essa consulta.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"O usuário não tem permissão para modificar essa consulta.\", \"status\": 403}")
            )),
            @ApiResponse(responseCode = "404", description = "A consulta informada na requisição não foi encontrada.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @DeleteMapping("/pedidoExame/anexo/consulta/{idConsulta}")
    public ResponseEntity<RequestResponseDTO> excluirAnexoPedidoExame(@PathVariable("idConsulta") Long idConsulta, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            pedidoExameService.excluirAnexoPedidoExame(idConsulta, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Anexo do Pedido removido com sucesso.", 200));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }
}
