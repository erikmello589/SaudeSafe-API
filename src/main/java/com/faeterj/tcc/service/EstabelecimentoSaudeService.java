package com.faeterj.tcc.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateEstabelecimentoDTO;
import com.faeterj.tcc.model.EstabelecimentoSaude;
import com.faeterj.tcc.model.Role;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.EstabelecimentoSaudeRepository;

@Service
public class EstabelecimentoSaudeService 
{

    private final EstabelecimentoSaudeRepository estabelecimentoSaudeRepository;

    public EstabelecimentoSaudeService(EstabelecimentoSaudeRepository estabelecimentoSaudeRepository) {
        this.estabelecimentoSaudeRepository = estabelecimentoSaudeRepository;
    }

    public EstabelecimentoSaude criarEstabelecimento(CreateEstabelecimentoDTO dto) 
    {
        EstabelecimentoSaude estabelecimentoSaude = new EstabelecimentoSaude();
        estabelecimentoSaude.setNomeEstabelecimento(dto.nomeEstabelecimento());
        estabelecimentoSaude.setCepEstabelecimento(dto.cepEstabelecimento());
        estabelecimentoSaude.setEnderecoEstabelecimento(dto.enderecoEstabelecimento());

        return estabelecimentoSaudeRepository.save(estabelecimentoSaude);
    }

    public EstabelecimentoSaude editarEstabelecimento(Long idEstabelecimento, CreateEstabelecimentoDTO dto) 
    {
        EstabelecimentoSaude estabelecimentoSaude = estabelecimentoSaudeRepository.findById(idEstabelecimento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        estabelecimentoSaude.setNomeEstabelecimento(dto.nomeEstabelecimento());
        estabelecimentoSaude.setCepEstabelecimento(dto.cepEstabelecimento());
        estabelecimentoSaude.setEnderecoEstabelecimento(dto.enderecoEstabelecimento());

        return estabelecimentoSaudeRepository.save(estabelecimentoSaude);
    }


    public void deletarEstabelecimento(Long idEstabelecimento, User user) {
        EstabelecimentoSaude estabelecimento = estabelecimentoSaudeRepository.findById(idEstabelecimento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin) {
            estabelecimentoSaudeRepository.delete(estabelecimento);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a deletar este Estabelecimento.");
        }
    }
}
