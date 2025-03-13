package com.faeterj.saudesafe.service;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.saudesafe.dto.CreateProfissionalDTO;
import com.faeterj.saudesafe.dto.EditaProfissionalDTO;
import com.faeterj.saudesafe.dto.ListaProfissionaisDTO;
import com.faeterj.saudesafe.dto.ReturnProfissionalDTO;
import com.faeterj.saudesafe.model.ProfissionalSaude;
import com.faeterj.saudesafe.model.Role;
import com.faeterj.saudesafe.model.StatusEnum;
import com.faeterj.saudesafe.model.StatusProfissional;
import com.faeterj.saudesafe.model.User;
import com.faeterj.saudesafe.repository.ProfissionalConsultaRepository;
import com.faeterj.saudesafe.repository.ProfissionalSaudeRepository;
import com.faeterj.saudesafe.repository.StatusProfissionalRepository;

@Service
public class ProfissionalSaudeService 
{
    private final ProfissionalSaudeRepository profissionalSaudeRepository;
    private final StatusProfissionalRepository statusProfissionalRepository;
    private final ProfissionalConsultaRepository profissionalConsultaRepository;

    public ProfissionalSaudeService(ProfissionalSaudeRepository profissionalSaudeRepository,
            StatusProfissionalRepository statusProfissionalRepository,
            ProfissionalConsultaRepository profissionalConsultaRepository) {
        this.profissionalSaudeRepository = profissionalSaudeRepository;
        this.statusProfissionalRepository = statusProfissionalRepository;
        this.profissionalConsultaRepository = profissionalConsultaRepository;
    }

    public ProfissionalSaude criarProfissional(CreateProfissionalDTO dto, User user) 
    {
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (profissionalSaudeRepository.findByNumeroClasseConselhoAndEstadoProfissional(dto.numeroClasseConselho(), dto.estadoProfissional()).isPresent()) 
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Profissional já existente.");
        }

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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Status de Profissional enviado é inválido.");
            }
            StatusProfissional statusProfissional = statusProfissionalRepository.findByProfissionalId(profissionalSaude.getProfissionalSaudeId());
            statusProfissional.setStatus(status); 
            statusProfissional = statusProfissionalRepository.save(statusProfissional);

            profissionalSaude.setStatusId(statusProfissional.getStatusId());
            
            profissionalSaude = profissionalSaudeRepository.save(profissionalSaude);

            //altera todos os ProfissionalConsulta que tiverem o mesmo CRM que o que eu estou editando
            profissionalConsultaRepository.atualizaCrmProfissionaisConsulta(profissionalSaude.getNomeProfissional(), profissionalSaude.getEspecialidadeProfissional(), profissionalSaude.getNumeroClasseConselho(), profissionalSaude.getEstadoProfissional(), profissionalSaude.getStatusId());

            //retorna o profissional com as alterações desejadas
            return profissionalSaude;
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
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
    }

    public Optional<ProfissionalSaude> acharProfissionalPorConselhoEstado(String numeroClasseConselho, String estadoProfissional) {
        return profissionalSaudeRepository.findByNumeroClasseConselhoAndEstadoProfissional(numeroClasseConselho, estadoProfissional);
    }

    public StatusProfissional acharStatusProfissionalPorStatusId(Long statusId) {
        return statusProfissionalRepository.findById(statusId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "StatusProfissional não encontrado"));
    }

    public ReturnProfissionalDTO buscarProfissional(Long idProfissional)
    {
        return profissionalSaudeToDTO(acharProfissionalPorId(idProfissional));
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

    public void verificaProfissional(Long idProfissional, String statusRequisitado, User user) 
    {
        ProfissionalSaude profissionalSaude = profissionalSaudeRepository.findById(idProfissional)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado."));
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin)
        {
            StatusEnum status;
            try 
            {
                status = StatusEnum.valueOf(statusRequisitado.toUpperCase());
            } 
            catch (IllegalArgumentException e) 
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Status de Profissional enviado é inválido.");
            }
            StatusProfissional statusProfissional = statusProfissionalRepository.findByProfissionalId(profissionalSaude.getProfissionalSaudeId());
            statusProfissional.setStatus(status); 
            statusProfissional = statusProfissionalRepository.save(statusProfissional);

            profissionalSaude.setStatusId(statusProfissional.getStatusId());
            
            profissionalSaude = profissionalSaudeRepository.save(profissionalSaude);

            //altera todos os ProfissionalConsulta que tiverem o mesmo CRM que o que eu estou editando
            profissionalConsultaRepository.atualizaCrmProfissionaisConsulta(profissionalSaude.getNomeProfissional(), profissionalSaude.getEspecialidadeProfissional(), profissionalSaude.getNumeroClasseConselho(), profissionalSaude.getEstadoProfissional(), profissionalSaude.getStatusId());

            //retorno
            return;
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a editar este Profissional.");
        }

    }
    

}
