package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.UUID;

import javax.validation.Valid;
public interface TarefaService {
    TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);
    Tarefa detalhaTarefa(String usuario, UUID idTarefa);
	void editaTarefa(String emailUsuario, UUID idTarefa, @Valid EditaTarefaRequest editaTarefaRequest);
    void concluirTarefa(UUID idTarefa, String usuarioEmail);

    void alteraPosicaoTarefa(String usuarioEmail, UUID idTarefa, int novaPosicao);
}
