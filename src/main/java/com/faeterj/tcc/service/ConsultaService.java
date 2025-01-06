package com.faeterj.tcc.service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateConsultaDTO;
import com.faeterj.tcc.dto.CreateEstabelecimentoDTO;
import com.faeterj.tcc.dto.CreateProfissionalDTO;
import com.faeterj.tcc.dto.ListaConsultasDTO;
import com.faeterj.tcc.dto.ReturnConsultaDTO;
import com.faeterj.tcc.model.Consulta;
import com.faeterj.tcc.model.EstabelecimentoSaude;
import com.faeterj.tcc.model.Paciente;
import com.faeterj.tcc.model.ProfissionalConsulta;
import com.faeterj.tcc.model.Role;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.ConsultaRepository;

@Service
public class ConsultaService 
{
    private final PacienteService pacienteService;
    private final ProfissionalConsultaService profissionalConsultaService;
    private final EstabelecimentoSaudeService estabelecimentoSaudeService;
    private final ConsultaRepository consultaRepository;

    public ConsultaService(PacienteService pacienteService, ProfissionalConsultaService profissionalConsultaService,
            EstabelecimentoSaudeService estabelecimentoSaudeService, ConsultaRepository consultaRepository) {
        this.pacienteService = pacienteService;
        this.profissionalConsultaService = profissionalConsultaService;
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
        ProfissionalConsulta profissionalConsulta = profissionalConsultaService.criarProfissionalConsulta(profissionalSaudeDTO, user);

        CreateEstabelecimentoDTO estabelecimentoDTO = new CreateEstabelecimentoDTO(dto.nomeEstabelecimento(), dto.cepEstabelecimento(), dto.enderecoEstabelecimento());
        EstabelecimentoSaude estabelecimentoSaude = estabelecimentoSaudeService.criarEstabelecimento(estabelecimentoDTO);
       
        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setProfissionalConsulta(profissionalConsulta);
        consulta.setEstabelecimentoSaude(estabelecimentoSaude);
        consulta.setMotivoConsulta(dto.motivoConsulta());
        consulta.setObservacaoConsulta(dto.observacaoConsulta());
        consulta.setConsultaData(dto.consultaData());

        consulta = consultaRepository.save(consulta);

        profissionalConsulta = profissionalConsultaService.inserirConsultaAoProfissional(profissionalConsulta.getProfissionalSaudeId(), consulta.getConsultaId());

        consulta.setProfissionalConsulta(profissionalConsulta);

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

    public void editarConsulta(Long idConsulta, User user, CreateConsultaDTO dto) 
    {
        Consulta consulta = consultaRepository.findById(idConsulta)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));

        if (!consulta.getPaciente().getUser().getUserID().equals(user.getUserID())) 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a editar as consultas deste paciente"); 
        }

        CreateProfissionalDTO profissionalSaudeDTO = new CreateProfissionalDTO(dto.nomeProfissional(), dto.especialidadeProfissional(), dto.numeroClasseConselho(), dto.estadoProfissional());
        ProfissionalConsulta profissionalConsulta = profissionalConsultaService.editarProfissionalConsulta(consulta.getProfissionalConsulta().getProfissionalSaudeId(), profissionalSaudeDTO, user);

        CreateEstabelecimentoDTO estabelecimentoDTO = new CreateEstabelecimentoDTO(dto.nomeEstabelecimento(), dto.cepEstabelecimento(), dto.enderecoEstabelecimento());
        EstabelecimentoSaude estabelecimentoSaude = estabelecimentoSaudeService.editarEstabelecimento(consulta.getEstabelecimentoSaude().getEstabelecimentoId(), estabelecimentoDTO);
        
        consulta.setProfissionalConsulta(profissionalConsulta);
        consulta.setEstabelecimentoSaude(estabelecimentoSaude);
        consulta.setMotivoConsulta(dto.motivoConsulta());
        consulta.setObservacaoConsulta(dto.observacaoConsulta());
        consulta.setConsultaData(dto.consultaData());

        consultaRepository.save(consulta);
    }

    public ListaConsultasDTO listarConsultasPaciente(Long idPaciente, int page, int pageSize, User user) 
    {
        Paciente paciente = pacienteService.acharPacientePorId(idPaciente);

        if (!paciente.getUser().getUserID().equals(user.getUserID())) 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a visualizar as consultas deste paciente");
        } 
        
        var listaConsultasPacientePage = consultaRepository.findByPacientePacienteId(idPaciente,
                PageRequest.of(page, pageSize, Sort.Direction.ASC, "consultaData"))
                .map(listaItem -> new ReturnConsultaDTO(
                        listaItem.getConsultaId(),
                        listaItem.getPaciente(),
                        profissionalConsultaService.profissionalConsultaToDTO(listaItem.getProfissionalConsulta()) ,
                        listaItem.getEstabelecimentoSaude(),
                        listaItem.getMotivoConsulta(),
                        listaItem.getObservacaoConsulta(),
                        listaItem.getConsultaData()));

        return new ListaConsultasDTO(
                    listaConsultasPacientePage.getContent(),
                    page,
                    pageSize,
                    listaConsultasPacientePage.getTotalPages(),
                    listaConsultasPacientePage.getTotalElements());
    }

    public ListaConsultasDTO listarConsultasUser(User user, int page, int pageSize) 
    {
        var listaConsultasUserPage = consultaRepository.findByPacienteUser(user, PageRequest.of(page, pageSize, Sort.Direction.ASC, "consultaData"))
                .map(listaItem -> new ReturnConsultaDTO(
                        listaItem.getConsultaId(),
                        listaItem.getPaciente(),
                        profissionalConsultaService.profissionalConsultaToDTO(listaItem.getProfissionalConsulta()) ,
                        listaItem.getEstabelecimentoSaude(),
                        listaItem.getMotivoConsulta(),
                        listaItem.getObservacaoConsulta(),
                        listaItem.getConsultaData()));

        return new ListaConsultasDTO(
                    listaConsultasUserPage.getContent(),
                    page,
                    pageSize,
                    listaConsultasUserPage.getTotalPages(),
                    listaConsultasUserPage.getTotalElements());
    }

    public Consulta acharConsultaPorId(Long idConsulta) {
        return consultaRepository.findById(idConsulta)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));
    }

}
