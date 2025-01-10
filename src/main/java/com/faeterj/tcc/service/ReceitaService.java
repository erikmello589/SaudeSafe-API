package com.faeterj.tcc.service;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateReceitaDTO;
import com.faeterj.tcc.model.Consulta;
import com.faeterj.tcc.model.Receita;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.ReceitaRepository;

@Service
public class ReceitaService 
{
    private final ReceitaRepository receitaRepository;
    private final ReceitaMedicamentoService receitaMedicamentoService;
    private final ConsultaService consultaService;

    public ReceitaService(ReceitaRepository receitaRepository, ReceitaMedicamentoService receitaMedicamentoService,
            ConsultaService consultaService) {
        this.receitaRepository = receitaRepository;
        this.receitaMedicamentoService = receitaMedicamentoService;
        this.consultaService = consultaService;
    }

    public Receita criarReceita(Long idConsulta, CreateReceitaDTO createReceitaDTO, MultipartFile file, User user) throws IOException 
    {
        // Busca a consulta associada
        Consulta consulta = consultaService.acharConsultaPorId(idConsulta);

        if (!consulta.getPaciente().getUser().getUserID().equals(user.getUserID())) 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a modificar essa consulta."); 
        }

        // Criação do objeto Receita a partir do DTO
        Receita receita = new Receita();
        receita.setConsulta(consulta);
        receita.setObservacaoReceita(createReceitaDTO.observacaoReceita());

        if (!file.isEmpty()) 
        {
            // Limitar tamanho do arquivo a 10 MB (10 * 1024 * 1024 bytes)
            long maxFileSize = 10 * 1024 * 1024; // 10 MB
            if (file.getSize() > maxFileSize) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Arquivo excede o tamanho máximo.");
            }

            // Validar tipo do arquivo
            String contentType = file.getContentType();
            if (!isValidFileType(contentType)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de Arquivo não suportado.");
            }

            // Converter arquivo para byte[] e salvar na receita
            receita.setPdfAnexado(file.getBytes());
        } 
        else 
        {
            receita.setPdfAnexado(null);
        }

        receita = receitaRepository.save(receita);
        receita.setMedicamentos(receitaMedicamentoService.converterListaDTOparaListaEntidade(createReceitaDTO.medicamentos(), receita));

        return receitaRepository.save(receita);
    }

    public Receita editarReceita(Long idConsulta, String observacaoReceita, MultipartFile file, User user) throws IOException 
    {
        // Busca a consulta associada
        Consulta consulta = consultaService.acharConsultaPorId(idConsulta);

        if (!consulta.getPaciente().getUser().getUserID().equals(user.getUserID())) 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a modificar essa consulta."); 
        }

        // Criação do objeto Atestado a partir do DTO
        Receita receita = receitaRepository.findByConsultaConsultaId(idConsulta)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Não há receita anexada a essa consulta."));

        receita.setObservacaoReceita(observacaoReceita);
        
        if (!file.isEmpty()) 
        {
            // Limitar tamanho do arquivo a 10 MB (10 * 1024 * 1024 bytes)
            long maxFileSize = 10 * 1024 * 1024; // 10 MB
            if (file.getSize() > maxFileSize) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Arquivo excede o tamanho máximo.");
            }

            // Validar tipo do arquivo
            String contentType = file.getContentType();
            if (!isValidFileType(contentType)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de Arquivo não suportado.");
            }

            // Converter arquivo para byte[] e salvar no Atestado
            receita.setPdfAnexado(file.getBytes());
        }

        return receitaRepository.save(receita);
    }

    public void excluirReceita(Long idConsulta, User user)
    {
        // Busca a consulta associada
        Consulta consulta = consultaService.acharConsultaPorId(idConsulta);

        if (!consulta.getPaciente().getUser().getUserID().equals(user.getUserID())) 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a modificar essa consulta."); 
        }

        // Criação do objeto Receita a partir do DTO
        Receita receita = receitaRepository.findByConsultaConsultaId(idConsulta)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Não há receita anexada a essa consulta."));
        
        receitaRepository.deleteById(receita.getReceitaId());
    }

    private boolean isValidFileType(String contentType) {
        return contentType.equals("application/pdf") ||
               contentType.equals("image/jpeg") ||
               contentType.equals("image/png");
    }
}


