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
import com.faeterj.tcc.repository.ConsultaRepository;

@Service
public class AtestadoService {

    private final AtestadoRepository atestadoRepository;
    private final ConsultaRepository consultaRepository;

    public AtestadoService(AtestadoRepository atestadoRepository, ConsultaRepository consultaRepository) {
        this.atestadoRepository = atestadoRepository;
        this.consultaRepository = consultaRepository;
    }

    public Atestado criarAtestado(Long idConsulta, CreateAtestadoDTO createAtestadoDTO, MultipartFile file, User user) throws IOException 
    {
        // Busca a consulta associada
        Consulta consulta = consultaRepository.findById(idConsulta)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));

        if (!consulta.getPaciente().getUser().getUserID().equals(user.getUserID())) 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a modificar essa consulta."); 
        }

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

    public Atestado editarAtestado(Long idConsulta, CreateAtestadoDTO createAtestadoDTO, MultipartFile file, User user) throws IOException 
    {
        // Busca a consulta associada
        Consulta consulta = consultaRepository.findById(idConsulta)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));

        if (!consulta.getPaciente().getUser().getUserID().equals(user.getUserID())) 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a modificar essa consulta."); 
        }

        // Criação do objeto Atestado a partir do DTO
        Atestado atestado = atestadoRepository.findByConsultaConsultaId(idConsulta)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Não há atestado anexado a essa consulta."));

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

        return atestadoRepository.save(atestado);
    }

    public void excluirAtestado(Long idConsulta, User user)
    {
        // Busca a consulta associada
        Consulta consulta = consultaRepository.findById(idConsulta)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));

        if (!consulta.getPaciente().getUser().getUserID().equals(user.getUserID())) 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a modificar essa consulta."); 
        }

        atestadoRepository.findByConsultaConsultaId(idConsulta)
                .ifPresent(atestado -> {
                    atestadoRepository.deleteById(atestado.getAtestadoId());
                });
    }

    private boolean isValidFileType(String contentType) {
        return contentType.equals("application/pdf") ||
               contentType.equals("image/jpeg") ||
               contentType.equals("image/png");
    }

}
