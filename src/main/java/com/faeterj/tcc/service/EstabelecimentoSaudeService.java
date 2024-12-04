package com.faeterj.tcc.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateEstabelecimentoDTO;
import com.faeterj.tcc.model.EstabelecimentoSaude;
import com.faeterj.tcc.model.Role;
import com.faeterj.tcc.repository.EstabelecimentoSaudeRepository;
import com.faeterj.tcc.repository.UserRepository;

@Service
public class EstabelecimentoSaudeService 
{

    private final EstabelecimentoSaudeRepository estabelecimentoSaudeRepository;
    private final UserRepository userRepository;

    public EstabelecimentoSaudeService(EstabelecimentoSaudeRepository estabelecimentoSaudeRepository,
            UserRepository userRepository) {
        this.estabelecimentoSaudeRepository = estabelecimentoSaudeRepository;
        this.userRepository = userRepository;
    }

    public void criarEstabelecimento(CreateEstabelecimentoDTO dto) 
    {
        EstabelecimentoSaude estabelecimentoSaude = new EstabelecimentoSaude();
        estabelecimentoSaude.setNomeEstabelecimento(dto.nomeEstabelecimento());
        estabelecimentoSaude.setCepEstabelecimento(dto.cepEstabelecimento());
        estabelecimentoSaude.setEnderecoEstabelecimento(dto.enderecoEstabelecimento());

        estabelecimentoSaudeRepository.save(estabelecimentoSaude);
    }


    public void deletarEstabelecimento(Long idEstabelecimento, JwtAuthenticationToken token) {
        var estabelecimento = estabelecimentoSaudeRepository.findById(idEstabelecimento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin) {
            estabelecimentoSaudeRepository.deleteById(idEstabelecimento);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a deletar este Estabelecimento.");
        }
    }
}
