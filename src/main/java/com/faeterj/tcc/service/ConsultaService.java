package com.faeterj.tcc.service;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateConsultaDTO;
import com.faeterj.tcc.dto.CreateEstabelecimentoDTO;
import com.faeterj.tcc.dto.CreateProfissionalDTO;
import com.faeterj.tcc.model.Consulta;
import com.faeterj.tcc.model.EstabelecimentoSaude;
import com.faeterj.tcc.model.Paciente;
import com.faeterj.tcc.model.ProfissionalSaude;
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

}
