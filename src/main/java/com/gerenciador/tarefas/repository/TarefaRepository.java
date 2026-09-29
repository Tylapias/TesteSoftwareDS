package com.gerenciador.tarefas.repository;

import com.gerenciador.tarefas.model.StatusTarefa;
import com.gerenciador.tarefas.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    boolean existsByTituloAndStatusIn(String titulo, List<StatusTarefa> status);
}