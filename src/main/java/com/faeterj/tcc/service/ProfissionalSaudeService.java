package com.faeterj.tcc.service;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateProfissionalDTO;
import com.faeterj.tcc.dto.EditaProfissionalDTO;
import com.faeterj.tcc.dto.ListaProfissionaisDTO;
import com.faeterj.tcc.dto.ReturnProfissionalDTO;
import com.faeterj.tcc.model.ProfissionalSaude;
import com.faeterj.tcc.model.Role;
import com.faeterj.tcc.model.StatusEnum;
import com.faeterj.tcc.model.StatusProfissional;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.ProfissionalSaudeRepository;
import com.faeterj.tcc.repository.StatusProfissionalRepository;

@Service
public class ProfissionalSaudeService 
{
    private final ProfissionalSaudeRepository profissionalSaudeRepository;
    private final StatusProfissionalRepository statusProfissionalRepository;

    public ProfissionalSaudeService(ProfissionalSaudeRepository profissionalSaudeRepository,
            StatusProfissionalRepository statusProfissionalRepository) {
        this.profissionalSaudeRepository = profissionalSaudeRepository;
        this.statusProfissionalRepository = statusProfissionalRepository;
    }

    public ProfissionalSaude criarProfissional(CreateProfissionalDTO dto, User user) 
    {
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        ProfissionalSaude profissionalSaude = new ProfissionalSaude();
        profissionalSaude.setNomeProfissional(dto.nomeProfissional());
        profissionalSaude.setEspecialidadeProfissional(dto.especialidadeProfissional());
        profissionalSaude.setNumeroClasseConselho(dto.numeroClasseConselho());
        profissionalSaude.setEstadoProfissional(dto.estadoProfissional());

        // Salvar o ProfissionalSaude sem o status (gera o ID primeiro)
        profissionalSaude = profissionalSaudeRepository.save(profissionalSaude);

        // Criar o StatusProfissional inicial
        StatusProfissional statusProfissional = new StatusProfissional();

        if (isAdmin)
        {
            statusProfissional.setStatus(StatusEnum.REGULAR); // Status inicial padrão caso seja criado por um usuario admin
        }
        else
        {
            statusProfissional.setStatus(StatusEnum.SOB_ANALISE);  // Status inicial padrão caso seja criado por um usuario basico
        }
        statusProfissional.setProfissionalId(profissionalSaude.getProfissionalSaudeId());

        // Salvar o StatusProfissional
        statusProfissional = statusProfissionalRepository.save(statusProfissional);

        // Vincular o status ao profissional
        profissionalSaude.setStatusId(statusProfissional.getStatusId());

        // Salvar novamente o profissional com o devido status inicial 
        return profissionalSaudeRepository.save(profissionalSaude);
    }

    public void deletarProfissional(Long idProfissional, User user) 
    {
        ProfissionalSaude profissional = profissionalSaudeRepository.findById(idProfissional)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin) {
            profissionalSaudeRepository.delete(profissional);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a deletar este Profissional.");
        }
    }

    public ProfissionalSaude editarProfissional(Long idProfissional, User user, EditaProfissionalDTO dto) 
    {
        ProfissionalSaude profissionalSaude = profissionalSaudeRepository.findById(idProfissional)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin)
        {
            profissionalSaude.setNomeProfissional(dto.nomeProfissional());
            profissionalSaude.setEspecialidadeProfissional(dto.especialidadeProfissional());
            profissionalSaude.setNumeroClasseConselho(dto.numeroClasseConselho());
            profissionalSaude.setEstadoProfissional(dto.estadoProfissional());

            StatusEnum status;
            try 
            {
                status = StatusEnum.valueOf(dto.statusProfissional().toUpperCase());
            } 
            catch (IllegalArgumentException e) 
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status requisitado é inválido.");
            }
            StatusProfissional statusProfissional = statusProfissionalRepository.findByProfissionalId(profissionalSaude.getProfissionalSaudeId());
            statusProfissional.setStatus(status); 
            statusProfissional = statusProfissionalRepository.save(statusProfissional);

            profissionalSaude.setStatusId(statusProfissional.getStatusId());
            
            // Salvar novamente o profissional com as alterações desejadas
            return profissionalSaudeRepository.save(profissionalSaude);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a editar este Profissional.");
        }

    }

    public ListaProfissionaisDTO listarProfissionais(int page, int pageSize) 
    {
        var listaProfissionaisPage = profissionalSaudeRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.ASC, "profissionalDataCriacao"))
                .map(profissional -> new ReturnProfissionalDTO(
                    profissional.getProfissionalSaudeId(),
                    profissional.getNomeProfissional(),
                    profissional.getEspecialidadeProfissional(),
                    profissional.getNumeroClasseConselho(),
                    profissional.getEstadoProfissional(),
                    acharStatusProfissionalPorStatusId(profissional.getStatusId()),
                    profissional.getProfissionalDataCriacao()
                    ));

        return new ListaProfissionaisDTO(
                listaProfissionaisPage.getContent(),
                page,
                pageSize,
                listaProfissionaisPage.getTotalPages(),
                listaProfissionaisPage.getTotalElements());
    }

    public ProfissionalSaude acharProfissionalPorId(Long idProfissional) {
        return profissionalSaudeRepository.findById(idProfissional)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    public Optional<ProfissionalSaude> acharProfissionalPorConselhoEstado(String numeroClasseConselho, String estadoProfissional) {
        return profissionalSaudeRepository.findByNumeroClasseConselhoAndEstadoProfissional(numeroClasseConselho, estadoProfissional);
    }

    public StatusProfissional acharStatusProfissionalPorStatusId(Long statusId) {
        return statusProfissionalRepository.findById(statusId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "StatusProfissional não encontrado"));
    }

    public ReturnProfissionalDTO profissionalSaudeToDTO(ProfissionalSaude profissional)
    {
        ReturnProfissionalDTO profissionalConsultaDTO = new ReturnProfissionalDTO(profissional.getProfissionalSaudeId(),
            profissional.getNomeProfissional(),    
            profissional.getEspecialidadeProfissional(), 
            profissional.getNumeroClasseConselho(), 
            profissional.getEstadoProfissional(), 
            acharStatusProfissionalPorStatusId(profissional.getStatusId()), 
            profissional.getProfissionalDataCriacao());
        
        return profissionalConsultaDTO;
    }

    public ReturnProfissionalDTO buscarProfissionalDTOPorConselhoEstado(String numeroClasseConselho, String estadoProfissional) {
        
        ProfissionalSaude profissional = acharProfissionalPorConselhoEstado(numeroClasseConselho, estadoProfissional)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
    
        // Converte para DTO e retorna
        return profissionalSaudeToDTO(profissional);
    }
    

}
