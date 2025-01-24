package com.faeterj.tcc.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreatePedidoExameDTO;
import com.faeterj.tcc.model.Consulta;
import com.faeterj.tcc.model.PedidoExame;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.ConsultaRepository;
import com.faeterj.tcc.repository.PedidoExameRepository;

@Service
public class PedidoExameService 
{
    private final PedidoExameRepository pedidoExameRepository;
    private final ExameService exameService;
    private final ConsultaRepository consultaRepository;

    public PedidoExameService(PedidoExameRepository pedidoExameRepository, ExameService exameService,
            ConsultaRepository consultaRepository) {
        this.pedidoExameRepository = pedidoExameRepository;
        this.exameService = exameService;
        this.consultaRepository = consultaRepository;
    }

    public PedidoExame criarPedidoExame(Long idConsulta, CreatePedidoExameDTO createPedidoExameDTO, MultipartFile file, User user) throws IOException 
    {
        // Busca a consulta associada
        Consulta consulta = consultaRepository.findById(idConsulta)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));

        if (!consulta.getPaciente().getUser().getUserID().equals(user.getUserID())) 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a modificar essa consulta."); 
        }

        PedidoExame pedidoExame = new PedidoExame();
        pedidoExame.setConsulta(consulta);
        pedidoExame.setPedidoObservacao(createPedidoExameDTO.pedidoObservacao());

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
            pedidoExame.setPdfAnexado(file.getBytes());
            pedidoExame.setTemAnexo(true);
        } 
        else 
        {
            pedidoExame.setPdfAnexado(null);
        }

        pedidoExame = pedidoExameRepository.save(pedidoExame);
        pedidoExame.setExames(exameService.converterListaDTOparaListaEntidade(createPedidoExameDTO.exames(), pedidoExame));

        return pedidoExameRepository.save(pedidoExame);
    }

    public PedidoExame editarPedidoExame(Long idConsulta, CreatePedidoExameDTO createPedidoExameDTO, MultipartFile file, User user) throws IOException 
    {
        // Busca a consulta associada
        Consulta consulta = consultaRepository.findById(idConsulta)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));

        if (!consulta.getPaciente().getUser().getUserID().equals(user.getUserID())) 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a modificar essa consulta."); 
        }

        PedidoExame pedidoExame = pedidoExameRepository.findByConsultaConsultaId(idConsulta)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Não há Pedido de Exames anexado a essa consulta."));

        pedidoExame.setPedidoObservacao(createPedidoExameDTO.pedidoObservacao());
        
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
            pedidoExame.setPdfAnexado(file.getBytes());
            pedidoExame.setTemAnexo(true);
        }

        exameService.ApagarExamesDoPedido(pedidoExame.getPedidoExameId());

        pedidoExame = pedidoExameRepository.save(pedidoExame);
        pedidoExame.setExames(exameService.converterListaDTOparaListaEntidade(createPedidoExameDTO.exames(), pedidoExame));

        return pedidoExameRepository.save(pedidoExame);
    }

    public void excluirPedidoExame(Long idConsulta, User user) 
    {
        // Busca a consulta associada
        Consulta consulta = consultaRepository.findById(idConsulta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));
    
        if (!consulta.getPaciente().getUser().getUserID().equals(user.getUserID())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a modificar essa consulta.");
        }
    
        pedidoExameRepository.findByConsultaConsultaId(idConsulta)
                .ifPresent(pedidoExame -> {
                    exameService.ApagarExamesDoPedido(pedidoExame.getPedidoExameId());
                    pedidoExameRepository.deleteById(pedidoExame.getPedidoExameId());
                });
    }

    public Optional<PedidoExame> buscarPedidoExame(Long idConsulta)
    {
        return pedidoExameRepository.findByConsultaConsultaId(idConsulta);
    }

    private boolean isValidFileType(String contentType) {
        return contentType.equals("application/pdf") ||
               contentType.equals("image/jpeg") ||
               contentType.equals("image/png");
    }

}
