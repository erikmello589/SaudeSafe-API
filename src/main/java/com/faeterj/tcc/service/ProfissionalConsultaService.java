package com.faeterj.tcc.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateProfissionalDTO;
import com.faeterj.tcc.model.ProfissionalConsulta;
import com.faeterj.tcc.model.ProfissionalSaude;
import com.faeterj.tcc.model.StatusProfissional;
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
        ProfissionalSaude profissionalSaude = profissionalSaudeService
                .acharProfissionalPorConselhoEstado(dto.numeroClasseConselho(), dto.estadoProfissional())
                .orElse(profissionalSaudeService.criarProfissional(dto));
    
        // Criar uma nova instância de ProfissionalConsulta
        ProfissionalConsulta profissionalConsulta = new ProfissionalConsulta();
        profissionalConsulta.setNomeProfissional(dto.nomeProfissional());
        profissionalConsulta.setEspecialidadeProfissional(dto.especialidadeProfissional());
        profissionalConsulta.setNumeroClasseConselho(dto.numeroClasseConselho());
        profissionalConsulta.setEstadoProfissional(dto.estadoProfissional());
    
        // Compartilhar o StatusProfissional existente
        StatusProfissional statusProfissional = profissionalSaude.getStatusProfissional();
        profissionalConsulta.setStatusProfissional(statusProfissional);
    
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
}
