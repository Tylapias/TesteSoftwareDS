package com.gerenciador.tarefas.service;

import com.gerenciador.tarefas.dto.TarefaRequestDTO;
import com.gerenciador.tarefas.dto.TarefaResponseDTO;
import com.gerenciador.tarefas.exception.RecursoNaoEncontradoException;
import com.gerenciador.tarefas.exception.RegraDeNegocioException;
import com.gerenciador.tarefas.model.StatusTarefa;
import com.gerenciador.tarefas.model.Tarefa;
import com.gerenciador.tarefas.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private TarefaService tarefaService;

    private Tarefa tarefa;

    @BeforeEach
    void setUp() {
        tarefa = new Tarefa(1L, "Desenvolver API", "Descricao detalhada", StatusTarefa.PENDENTE, LocalDateTime.now(), null);
    }

    @Test
    @DisplayName("Deve criar tarefa com sucesso")
    void deveCriarTarefaComSucesso() {
        TarefaRequestDTO dto = new TarefaRequestDTO("Desenvolver API", "Descricao detalhada", StatusTarefa.PENDENTE);

        when(tarefaRepository.existsByTituloAndStatusIn(anyString(), any())).thenReturn(false);
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        TarefaResponseDTO response = tarefaService.criar(dto);

        assertNotNull(response);
        assertEquals("Desenvolver API", response.getTitulo());
        verify(tarefaRepository, times(1)).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Lancar excecao ao criar titulo duplicado")
    void deveLancarExcecaoAoCriarTituloDuplicado() {
        TarefaRequestDTO dto = new TarefaRequestDTO("Desenvolver API", "Descricao", StatusTarefa.PENDENTE);

        when(tarefaRepository.existsByTituloAndStatusIn(anyString(), any())).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> tarefaService.criar(dto));
        verify(tarefaRepository, never()).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Lancar excecao ao buscar ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> tarefaService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Lancar excecao ao mudar de PENDENTE para CONCLUIDA")
    void deveLancarExcecaoAoMudarTransicaoStatusInvalida() {
        TarefaRequestDTO dto = new TarefaRequestDTO("Desenvolver API", "Descricao", StatusTarefa.CONCLUIDA);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        assertThrows(RegraDeNegocioException.class, () -> tarefaService.atualizar(1L, dto));
    }

    @Test
    @DisplayName("Lancar excecao ao deletar tarefa CONCLUIDA")
    void deveLancarExcecaoAoDeletarTarefaConcluida() {
        tarefa.setStatus(StatusTarefa.CONCLUIDA);
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        assertThrows(RegraDeNegocioException.class, () -> tarefaService.deletar(1L));
        verify(tarefaRepository, never()).delete(any(Tarefa.class));
    }
}