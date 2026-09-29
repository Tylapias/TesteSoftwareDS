package com.gerenciador.tarefas.service;

import com.gerenciador.tarefas.dto.TarefaRequestDTO;
import com.gerenciador.tarefas.dto.TarefaResponseDTO;
import com.gerenciador.tarefas.exception.RecursoNaoEncontradoException;
import com.gerenciador.tarefas.exception.RegraDeNegocioException;
import com.gerenciador.tarefas.model.StatusTarefa;
import com.gerenciador.tarefas.model.Tarefa;
import com.gerenciador.tarefas.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public TarefaResponseDTO criar(TarefaRequestDTO dto) {
        boolean existeDuplicado = tarefaRepository.existsByTituloAndStatusIn(
                dto.getTitulo(),
                List.of(StatusTarefa.PENDENTE, StatusTarefa.EM_ANDAMENTO)
        );

        if (existeDuplicado) {
            throw new RegraDeNegocioException("Ja existe uma tarefa com este titulo em andamento ou pendente.");
        }

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusTarefa.PENDENTE);
        tarefa.setDataCriacao(LocalDateTime.now());

        Tarefa salva = tarefaRepository.save(tarefa);
        return new TarefaResponseDTO(salva);
    }

    public List<TarefaResponseDTO> listarTodas() {
        return tarefaRepository.findAll().stream()
                .map(TarefaResponseDTO::new)
                .collect(Collectors.toList());
    }

    public TarefaResponseDTO buscarPorId(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa nao encontrada com ID: " + id));
        return new TarefaResponseDTO(tarefa);
    }

    public TarefaResponseDTO atualizar(Long id, TarefaRequestDTO dto) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa nao encontrada com ID: " + id));

        if (tarefa.getStatus() == StatusTarefa.PENDENTE && dto.getStatus() == StatusTarefa.CONCLUIDA) {
            throw new RegraDeNegocioException("Nao e permitido transitar diretamente de PENDENTE para CONCLUIDA.");
        }

        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        if (dto.getStatus() != null) {
            tarefa.setStatus(dto.getStatus());
        }

        Tarefa atualizada = tarefaRepository.save(tarefa);
        return new TarefaResponseDTO(atualizada);
    }

    public void deletar(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa nao encontrada com ID: " + id));

        if (tarefa.getStatus() == StatusTarefa.CONCLUIDA) {
            throw new RegraDeNegocioException("Nao e possivel deletar uma tarefa com status CONCLUIDA.");
        }

        tarefaRepository.delete(tarefa);
    }
}