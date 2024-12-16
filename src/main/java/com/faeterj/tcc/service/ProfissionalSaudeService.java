package com.faeterj.tcc.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateProfissionalDTO;
import com.faeterj.tcc.dto.ListaProfissionaisDTO;
import com.faeterj.tcc.dto.ReturnProfissionalDTO;
import com.faeterj.tcc.dto.VerificaProfissionalDTO;
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

    public ProfissionalSaude criarProfissional(CreateProfissionalDTO dto) 
    {
        ProfissionalSaude profissionalSaude = new ProfissionalSaude();
        profissionalSaude.setNomeProfissional(dto.nomeProfissional());
        profissionalSaude.setEspecialidadeProfissional(dto.especialidadeProfissional());
        profissionalSaude.setNumeroClasseConselho(dto.numeroClasseConselho());
        profissionalSaude.setEstadoProfissional(dto.estadoProfissional());

        // Salvar o ProfissionalSaude sem o status (gera o ID primeiro)
        profissionalSaude = profissionalSaudeRepository.save(profissionalSaude);

        // Criar o StatusProfissional inicial
        StatusProfissional statusProfissional = new StatusProfissional();
        statusProfissional.setStatus(StatusEnum.SOB_ANALISE); // Status inicial padrão
        statusProfissional.setProfissionalSaude(profissionalSaude);

        // Salvar o StatusProfissional
        statusProfissional = statusProfissionalRepository.save(statusProfissional);

        // Vincular o status ao profissional
        profissionalSaude.setStatusProfissional(statusProfissional);

        // Salvar novamente o profissional com o devido status inicial (SOB ANALISE)
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

    public ProfissionalSaude editarProfissional(Long idProfissional, User user, CreateProfissionalDTO dto) 
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
                    profissional.getStatusProfissional(),
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

    public void verificarProfissional(User user, VerificaProfissionalDTO dto) 
    {
        ProfissionalSaude profissional = profissionalSaudeRepository.findById(dto.idProfissional())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
    
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));
    
        if (isAdmin) 
        {
            StatusEnum status;
            try 
            {
                status = StatusEnum.valueOf(dto.statusSolicitado().toUpperCase());
            } 
            catch (IllegalArgumentException e) 
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status requisitado é inválido.");
            }
            
            StatusProfissional statusProfissional = statusProfissionalRepository.findByProfissionalSaudeProfissionalSaudeId(profissional.getProfissionalSaudeId());
            statusProfissional.setStatus(status); 
            statusProfissional = statusProfissionalRepository.save(statusProfissional);
        } 
        else 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a fazer essa verificação.");
        }
    }
    

}
