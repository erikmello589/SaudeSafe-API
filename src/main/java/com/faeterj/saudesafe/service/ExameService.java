package com.faeterj.saudesafe.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.saudesafe.dto.CreateExameDTO;
import com.faeterj.saudesafe.model.Exame;
import com.faeterj.saudesafe.model.PedidoExame;
import com.faeterj.saudesafe.model.SituacaoEnum;
import com.faeterj.saudesafe.repository.ExameRepository;

import jakarta.transaction.Transactional;

@Service
public class ExameService 
{
    private final ExameRepository exameRepository;

    public ExameService(ExameRepository exameRepository) {
        this.exameRepository = exameRepository;
    }

    public List<Exame> converterListaDTOparaListaEntidade(List<CreateExameDTO> listaDTO, PedidoExame pedidoExame) 
    {
        List<Exame> listaExames = new ArrayList<>();
        for (CreateExameDTO exameDTO : listaDTO)
        {
            Exame exame = new Exame();
            exame.setNomeExame(exameDTO.nomeExame());
            exame.setTipoExame(exameDTO.tipoExame());
            exame.setObservacaoExame(exameDTO.observacaoExame());

            SituacaoEnum situacaoExame;
            try 
            {
                situacaoExame = SituacaoEnum.valueOf(exameDTO.situacaoExame().toUpperCase());
            } 
            catch (IllegalArgumentException e) 
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alguma Situação de Exame enviada é inválida.");
            }
            exame.setSituacaoExame(situacaoExame);

            exame.setPedidoExame(pedidoExame);
            
            listaExames.add(exameRepository.save(exame));
        }

        return listaExames;
    }

    @Transactional
    public void ApagarExamesDoPedido(Long pedidoExameId) 
    {
        exameRepository.deleteByPedidoExamePedidoExameId(pedidoExameId);
    }
}
