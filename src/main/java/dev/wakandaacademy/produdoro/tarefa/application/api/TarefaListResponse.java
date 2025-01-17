package dev.wakandaacademy.produdoro.tarefa.application.api;

import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Value
public class TarefaListResponse {
    private UUID idTarefa;
    private String descricao;
    private UUID idUsuario;
    private UUID idArea;
    private UUID idProjeto;
    private StatusTarefa status;
    private StatusAtivacaoTarefa statusAtivacao;
    private int contagemPomodoro;

    public TarefaListResponse(UUID idTarefa, String descricao, UUID idUsuario, UUID idArea, UUID idProjeto, StatusTarefa status, StatusAtivacaoTarefa statusAtivacao, int contagemPomodoro) {
        this.idTarefa = idTarefa;
        this.descricao = descricao;
        this.idUsuario = idUsuario;
        this.idArea = idArea;
        this.idProjeto = idProjeto;
        this.status = status;
        this.statusAtivacao = statusAtivacao;
        this.contagemPomodoro = contagemPomodoro;
    }

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
