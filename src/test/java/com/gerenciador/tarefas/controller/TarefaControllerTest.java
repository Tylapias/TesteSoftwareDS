package com.gerenciador.tarefas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerenciador.tarefas.dto.TarefaRequestDTO;
import com.gerenciador.tarefas.dto.TarefaResponseDTO;
import com.gerenciador.tarefas.model.StatusTarefa;
import com.gerenciador.tarefas.model.Tarefa;
import com.gerenciador.tarefas.service.TarefaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TarefaController.class)
class TarefaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TarefaService tarefaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/tarefas deve retornar 201 Created quando valido")
    void deveCriarTarefaE_Retornar201() throws Exception {
        TarefaRequestDTO request = new TarefaRequestDTO("Criar testes", "Descricao do teste", StatusTarefa.PENDENTE);
        Tarefa tarefa = new Tarefa(1L, "Criar testes", "Descricao do teste", StatusTarefa.PENDENTE, LocalDateTime.now(), null);
        TarefaResponseDTO response = new TarefaResponseDTO(tarefa);

        when(tarefaService.criar(any(TarefaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Criar testes"));
    }

    @Test
    @DisplayName("POST /api/tarefas deve retornar 400 Bad Request se titulo for curto")
    void deveRetornar400AoCriarComTituloInvalido() throws Exception {
        TarefaRequestDTO request = new TarefaRequestDTO("abc", "Descricao", StatusTarefa.PENDENTE);

        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}