package dev.wakandaacademy.produdoro.tarefa.application.api;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Value
@Builder
public class TarefaIdResponse {
    private UUID idTarefa;

    public static List<TarefaListResponse> converte(List<Tarefa> tarefas) {
        return tarefas.stream()
                .map(tarefa -> new TarefaListResponse(
                        tarefa.getIdTarefa(),
                        tarefa.getDescricao(),
                        tarefa.getIdUsuario(),
                        tarefa.getIdArea(),
                        tarefa.getIdProjeto(),
                        tarefa.getStatus(),
                        tarefa.getStatusAtivacao(),
                        tarefa.getContagemPomodoro()
                ))
                .collect(Collectors.toList());
    }
}
