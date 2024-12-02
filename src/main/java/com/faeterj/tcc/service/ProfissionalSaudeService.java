package com.faeterj.tcc.service;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateProfissionalDTO;
import com.faeterj.tcc.dto.ListaProfissionaisDTO;
import com.faeterj.tcc.dto.ReturnProfissionalDTO;
import com.faeterj.tcc.model.ProfissionalSaude;
import com.faeterj.tcc.model.Role;
import com.faeterj.tcc.repository.ProfissionalSaudeRepository;
import com.faeterj.tcc.repository.UserRepository;

@Service
public class ProfissionalSaudeService 
{
    private final ProfissionalSaudeRepository profissionalSaudeRepository;
    private final UserRepository userRepository;

    public ProfissionalSaudeService(ProfissionalSaudeRepository profissionalSaudeRepository,
            UserRepository userRepository) {
        this.profissionalSaudeRepository = profissionalSaudeRepository;
        this.userRepository = userRepository;
    }

    public void criarProfissional(CreateProfissionalDTO dto) 
    {
        ProfissionalSaude profissionalSaude = new ProfissionalSaude();
        profissionalSaude.setNomeProfissional(dto.nomeProfissional());
        profissionalSaude.setEspecialidadeProfissional(dto.especialidadeProfissional());
        profissionalSaude.setNumeroClasseConselho(dto.numeroClasseConselho());
        profissionalSaude.setProfissionalIsVerified(false);

        profissionalSaudeRepository.save(profissionalSaude);
    }

    public void deletarProfissional(UUID idProfissional, JwtAuthenticationToken token) {
        var profissional = profissionalSaudeRepository.findById(idProfissional)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));

        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin) {
            profissionalSaudeRepository.deleteById(idProfissional);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a deletar este Profissional.");
        }
    }

    public ListaProfissionaisDTO listarProfissionais(int page, int pageSize, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        var listaProfissionaisPage = profissionalSaudeRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.ASC, "profissionalDataCriacao"))
                .map(profissional -> new ReturnProfissionalDTO(
                    profissional.getProfissionalSaudeId(),
                    profissional.getNomeProfissional(),
                    profissional.getEspecialidadeProfissional(),
                    profissional.getNumeroClasseConselho(),
                    profissional.getProfissionalIsVerified(),
                    profissional.getProfissionalDataCriacao()
                    ));

        return new ListaProfissionaisDTO(
                listaProfissionaisPage.getContent(),
                page,
                pageSize,
                listaProfissionaisPage.getTotalPages(),
                listaProfissionaisPage.getTotalElements());
    }

    public void verificarProfissional(UUID idProfissional, JwtAuthenticationToken token) {
        var profissional = profissionalSaudeRepository.findById(idProfissional)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));

        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin) {
            profissional.setProfissionalIsVerified(true);
            profissionalSaudeRepository.save(profissional);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a deletar este Profissional.");
        }
    }
}
