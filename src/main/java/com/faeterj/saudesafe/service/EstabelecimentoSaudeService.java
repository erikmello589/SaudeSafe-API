package com.faeterj.saudesafe.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.saudesafe.dto.CreateEstabelecimentoDTO;
import com.faeterj.saudesafe.model.EstabelecimentoSaude;
import com.faeterj.saudesafe.repository.EstabelecimentoSaudeRepository;

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


    public void excluirEstabelecimento(Long idEstabelecimento) {
        EstabelecimentoSaude estabelecimento = estabelecimentoSaudeRepository.findById(idEstabelecimento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));
       
        estabelecimentoSaudeRepository.delete(estabelecimento);
    }
}
