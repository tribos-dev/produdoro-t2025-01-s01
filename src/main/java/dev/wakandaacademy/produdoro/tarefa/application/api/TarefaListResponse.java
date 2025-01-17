package dev.wakandaacademy.produdoro.tarefa.application.api;

import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

public class TarefaListResponse {
    private UUID idTarefa;
    private String descricao;
    private UUID idUsuario;
    private UUID idArea;
    private UUID idProjeto;
    private StatusTarefa status;
    private StatusAtivacaoTarefa statusAtivacao;

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

    private int contagemPomodoro;
}
