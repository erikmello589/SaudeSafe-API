package com.faeterj.tcc.service;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateAtestadoDTO;
import com.faeterj.tcc.model.Atestado;
import com.faeterj.tcc.model.Consulta;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.AtestadoRepository;

@Service
public class AtestadoService {

    private final AtestadoRepository atestadoRepository;
    private final ConsultaService consultaService;

    public AtestadoService(AtestadoRepository atestadoRepository, ConsultaService consultaService) {
        this.atestadoRepository = atestadoRepository;
        this.consultaService = consultaService;
    }

    public Atestado criarAtestado(Long idConsulta, CreateAtestadoDTO createAtestadoDTO, MultipartFile file, User user) throws IOException {

        // Busca a consulta associada
        Consulta consulta = consultaService.acharConsultaPorId(idConsulta);

        // Criação do objeto Atestado a partir do DTO
        Atestado atestado = new Atestado();
        atestado.setConsulta(consulta);
        atestado.setPeriodoAfastamento(createAtestadoDTO.periodoAfastamento());
        atestado.setObservacaoAtestado(createAtestadoDTO.observacaoAtestado());
        
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
            atestado.setPdfAnexado(file.getBytes());
        } 
        else 
        {
            atestado.setPdfAnexado(null);
        }

        return atestadoRepository.save(atestado);
    }

    private boolean isValidFileType(String contentType) {
        return contentType.equals("application/pdf") ||
               contentType.equals("image/jpeg") ||
               contentType.equals("image/png");
    }

}
