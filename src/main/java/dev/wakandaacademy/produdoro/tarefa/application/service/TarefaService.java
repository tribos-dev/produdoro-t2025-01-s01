package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
public interface TarefaService {
    TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);
    Tarefa detalhaTarefa(String usuario, UUID idTarefa);
	void editaTarefa(String emailUsuario, UUID idTarefa, @Valid EditaTarefaRequest editaTarefaRequest);
    void concluirTarefa(UUID idTarefa, String usuarioEmail);
    void limparTodasTarefas(UUID idUsuario, String emailUsuario);
    void deletaTarefasConcluidas(String token, UUID idUsuario);
    void ativaTarefa(String email, UUID idTarefa);
    void alteraPosicaoTarefa(String usuarioEmail, UUID idTarefa, int novaPosicao);
    List<TarefaListResponse> buscarTodasAsTarefas(String usuario, UUID idUsuario);
	void incrementaPomodoro(String usuarioEmail, UUID idTarefa);
}
