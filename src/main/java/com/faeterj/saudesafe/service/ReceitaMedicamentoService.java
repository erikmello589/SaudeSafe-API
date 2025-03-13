package com.faeterj.saudesafe.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.faeterj.saudesafe.dto.CreateReceitaMedicamentoDTO;
import com.faeterj.saudesafe.model.Receita;
import com.faeterj.saudesafe.model.ReceitaMedicamento;
import com.faeterj.saudesafe.repository.ReceitaMedicamentoRepository;

import jakarta.transaction.Transactional;

@Service
public class ReceitaMedicamentoService 
{
    private final ReceitaMedicamentoRepository receitaMedicamentoRepository;

    public ReceitaMedicamentoService(ReceitaMedicamentoRepository receitaMedicamentoRepository) {
        this.receitaMedicamentoRepository = receitaMedicamentoRepository;
    }

    public List<ReceitaMedicamento> converterListaDTOparaListaEntidade(List<CreateReceitaMedicamentoDTO> listaDTO, Receita receita) 
    {
        List<ReceitaMedicamento> listaMedicamentos = new ArrayList<>();
        for (CreateReceitaMedicamentoDTO medicamentoDTO : listaDTO)
        {
            ReceitaMedicamento medicamento = new ReceitaMedicamento();
            medicamento.setNomeMedicamento(medicamentoDTO.nomeMedicamento());
            medicamento.setDoseMedicamento(medicamentoDTO.doseMedicamento());
            medicamento.setQtdDoseMedicamento(medicamentoDTO.qtdDoseMedicamento());
            medicamento.setIntervaloMedicamento(medicamentoDTO.intervaloMedicamento());
            medicamento.setPeriodoMedicamento(medicamentoDTO.periodoMedicamento());
            medicamento.setObservacaoMedicamento(medicamentoDTO.observacaoMedicamento());
            medicamento.setReceita(receita);
            
            listaMedicamentos.add(receitaMedicamentoRepository.save(medicamento));
        }

        return listaMedicamentos;
    }

    @Transactional
    public void ApagarMedicamentosDaReceita(Long receitaId) 
    {
        receitaMedicamentoRepository.deleteByReceitaReceitaId(receitaId);
    }
}
