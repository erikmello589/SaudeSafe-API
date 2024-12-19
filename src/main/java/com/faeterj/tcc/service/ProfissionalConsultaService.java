package com.faeterj.tcc.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateProfissionalDTO;
import com.faeterj.tcc.dto.ReturnProfissionalDTO;
import com.faeterj.tcc.model.ProfissionalConsulta;
import com.faeterj.tcc.model.ProfissionalSaude;
import com.faeterj.tcc.repository.ProfissionalConsultaRepository;

@Service
public class ProfissionalConsultaService 
{
    private final ProfissionalConsultaRepository profissionalConsultaRepository;
    private final ProfissionalSaudeService profissionalSaudeService;

    public ProfissionalConsultaService(ProfissionalConsultaRepository profissionalConsultaRepository,
            ProfissionalSaudeService profissionalSaudeService) {
        this.profissionalConsultaRepository = profissionalConsultaRepository;
        this.profissionalSaudeService = profissionalSaudeService;
    }

    public ProfissionalConsulta criarProfissionalConsulta(CreateProfissionalDTO dto) {
        // Recuperar ou criar o ProfissionalSaude
        ProfissionalSaude profissionalSaude = profissionalSaudeService.acharProfissionalPorConselhoEstado(dto.numeroClasseConselho(), dto.estadoProfissional())
                .orElse(profissionalSaudeService.criarProfissional(dto));

        System.out.println("\n\n\nATENCAO AQUI:" + profissionalSaude.getNomeProfissional() + "\n\n\n");
    
        // Criar uma nova instância de ProfissionalConsulta
        ProfissionalConsulta profissionalConsulta = new ProfissionalConsulta();
        profissionalConsulta.setNomeProfissional(dto.nomeProfissional());
        profissionalConsulta.setEspecialidadeProfissional(dto.especialidadeProfissional());
        profissionalConsulta.setNumeroClasseConselho(dto.numeroClasseConselho());
        profissionalConsulta.setEstadoProfissional(dto.estadoProfissional());
    
        // Compartilhar o id do StatusProfissional existente
        profissionalConsulta.setStatusId(profissionalSaude.getStatusId());
    
        // Salvar a nova entidade ProfissionalConsulta
        return profissionalConsultaRepository.save(profissionalConsulta);
    }
    

    public ProfissionalConsulta inserirConsultaAoProfissional(Long idProfissional, Long idConsulta)
    {
        ProfissionalConsulta profissionalConsulta = profissionalConsultaRepository.findById(idProfissional)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProfissionalConsulta não encontrado"));
        
        profissionalConsulta.setConsultaId(idConsulta);
        return profissionalConsultaRepository.save(profissionalConsulta);
    }

    public ReturnProfissionalDTO profissionalConsultaToDTO(ProfissionalConsulta profissional)
    {
        ReturnProfissionalDTO profissionalConsultaDTO = new ReturnProfissionalDTO(profissional.getProfissionalSaudeId(),
            profissional.getNomeProfissional(),    
            profissional.getEspecialidadeProfissional(), 
            profissional.getNumeroClasseConselho(), 
            profissional.getEstadoProfissional(), 
            profissionalSaudeService.acharStatusProfissionalPorStatusId(profissional.getStatusId()), 
            profissional.getProfissionalDataCriacao());
        
        return profissionalConsultaDTO;
    }
}
