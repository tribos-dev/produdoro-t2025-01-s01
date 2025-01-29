package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
    void limparTodasAsTarefas(List<Tarefa> tarefas);
    void desativaTarefa(UUID idUsuario);
    List<Tarefa> buscaTodasTarefasPorIdUsuario(UUID idUsuario);
    List<Tarefa> buscaTarefasDoUsuario(UUID idUsuario);

}
