package com.faeterj.tcc.service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateConsultaDTO;
import com.faeterj.tcc.dto.CreateEstabelecimentoDTO;
import com.faeterj.tcc.dto.CreateProfissionalDTO;
import com.faeterj.tcc.dto.ListaConsultasPacienteDTO;
import com.faeterj.tcc.dto.ReturnConsultaPacienteDTO;
import com.faeterj.tcc.model.Consulta;
import com.faeterj.tcc.model.EstabelecimentoSaude;
import com.faeterj.tcc.model.Paciente;
import com.faeterj.tcc.model.ProfissionalSaude;
import com.faeterj.tcc.model.Role;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.ConsultaRepository;

@Service
public class ConsultaService 
{
    private final PacienteService pacienteService;
    private final ProfissionalSaudeService profissionalSaudeService;
    private final EstabelecimentoSaudeService estabelecimentoSaudeService;
    private final ConsultaRepository consultaRepository;

    public ConsultaService(PacienteService pacienteService, ProfissionalSaudeService profissionalSaudeService,
            EstabelecimentoSaudeService estabelecimentoSaudeService, ConsultaRepository consultaRepository) {
        this.pacienteService = pacienteService;
        this.profissionalSaudeService = profissionalSaudeService;
        this.estabelecimentoSaudeService = estabelecimentoSaudeService;
        this.consultaRepository = consultaRepository;
    }

    public void criarConsulta(CreateConsultaDTO dto, User user) 
    {
        Paciente paciente = pacienteService.acharPacientePorId(dto.pacienteId());

        if (!paciente.getUser().getUserID().equals(user.getUserID())) 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "O usuário não tem permissão para criar consultas para este paciente.");
        }

        CreateProfissionalDTO profissionalSaudeDTO = new CreateProfissionalDTO(dto.nomeProfissional(), dto.especialidadeProfissional(), dto.numeroClasseConselho(), dto.estadoProfissional());
        ProfissionalSaude profissionalSaude = profissionalSaudeService.criarProfissional(profissionalSaudeDTO);

        CreateEstabelecimentoDTO estabelecimentoDTO = new CreateEstabelecimentoDTO(dto.nomeEstabelecimento(), dto.cepEstabelecimento(), dto.enderecoEstabelecimento());
        EstabelecimentoSaude estabelecimentoSaude = estabelecimentoSaudeService.criarEstabelecimento(estabelecimentoDTO);
       
        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setProfissionalSaude(profissionalSaude);
        consulta.setEstabelecimentoSaude(estabelecimentoSaude);
        consulta.setMotivoConsulta(dto.motivoConsulta());
        consulta.setObservacaoConsulta(dto.observacaoConsulta());
        consulta.setConsultaData(dto.consultaData());

        consultaRepository.save(consulta);
    }

    public void deletarConsulta(Long idConsulta, User user) 
    {
        Consulta consulta = consultaRepository.findById(idConsulta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || consulta.getPaciente().getUser().getUserID().equals(user.getUserID())) {
            consultaRepository.deleteById(idConsulta);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a deletar este paciente");
        }
    }

    public ListaConsultasPacienteDTO listarConsultasPaciente(Long idPaciente, int page, int pageSize, User user) 
    {
        Paciente paciente = pacienteService.acharPacientePorId(idPaciente);

        if (paciente.getUser().getUserID().equals(user.getUserID())) 
        {
            var listaConsultasPacientePage = consultaRepository.findByPacientePacienteId(idPaciente,
                PageRequest.of(page, pageSize, Sort.Direction.ASC, "consultaData"))
                .map(listaItem -> new ReturnConsultaPacienteDTO(
                        listaItem.getConsultaId(),
                        listaItem.getPaciente(),
                        listaItem.getProfissionalSaude(),
                        listaItem.getEstabelecimentoSaude(),
                        listaItem.getMotivoConsulta(),
                        listaItem.getObservacaoConsulta(),
                        listaItem.getConsultaData()));

            return new ListaConsultasPacienteDTO(
                        listaConsultasPacientePage.getContent(),
                        page,
                        pageSize,
                        listaConsultasPacientePage.getTotalPages(),
                        listaConsultasPacientePage.getTotalElements());
        } 
        else 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a visualizar as consultas deste paciente");
        }
    }

    /*public void listarConsultasUser(Long idPaciente, User user) 
    {
        Consulta consulta = consultaRepository.findById(idConsulta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || consulta.getPaciente().getUser().getUserID().equals(user.getUserID())) {
            consultaRepository.deleteById(idConsulta);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a deletar este paciente");
        }
    }*/

}
