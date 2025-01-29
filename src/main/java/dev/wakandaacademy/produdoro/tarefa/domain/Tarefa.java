package dev.wakandaacademy.produdoro.tarefa.domain;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Document(collection = "Tarefa")
@ToString
@EqualsAndHashCode
public class Tarefa {
	@Id
	private UUID idTarefa;
	@NotBlank
	private String descricao;
	@Indexed
	private UUID idUsuario;
	@Indexed
	private UUID idArea;
	@Indexed
	private UUID idProjeto;
	private StatusTarefa status;
	private StatusAtivacaoTarefa statusAtivacao;
	private int contagemPomodoro;
	private int posicao;



	public Tarefa(TarefaRequest tarefaRequest, int posicao) {
		this.idTarefa = UUID.randomUUID();
		this.idUsuario = tarefaRequest.getIdUsuario();
		this.descricao = tarefaRequest.getDescricao();
		this.idArea = tarefaRequest.getIdArea();
		this.idProjeto = tarefaRequest.getIdProjeto();
		this.status = StatusTarefa.A_FAZER;
		this.statusAtivacao = StatusAtivacaoTarefa.INATIVA;
		this.contagemPomodoro = 1;
		this.posicao = posicao;
	}

	public void pertenceAoUsuario(Usuario usuarioPorEmail) {
		if(!this.idUsuario.equals(usuarioPorEmail.getIdUsuario())) {
			throw APIException.build(HttpStatus.UNAUTHORIZED, "Usuário não é dono da Tarefa solicitada!");
		}
	}
	
	public void incrementaPomodoro(Tarefa tarefa, Usuario usuario) {
		pertenceAoUsuario(usuario);
		if (!usuario.getStatus().equals(StatusUsuario.FOCO)) {
			ativaTarefa();
			usuario.mudaStatusParaFoco(usuario.getIdUsuario());
		} else {
			tarefa.incrementaPomodoro();
			verificaQuantidadePomodoro(tarefa, usuario);
		}
	}
	
	private void incrementaPomodoro() {
		this.contagemPomodoro++;
	}
	
	private void verificaQuantidadePomodoro(Tarefa tarefa, Usuario usuario) {
		int totalPomodoro = tarefa.getContagemPomodoro();
		if (totalPomodoro % 4 == 0) {
			usuario.mudaStatusPausaLonga(usuario.getIdUsuario());
		} else {
			usuario.mudaStatusPausaCurta(usuario.getIdUsuario());
		}
	}

	public void edita(@Valid EditaTarefaRequest editaTarefaRequest) {
		this.descricao = editaTarefaRequest.getDescricao();

	}

	public void verificaTarefaAtiva() {
		if (this.statusAtivacao.equals(StatusAtivacaoTarefa.ATIVA)) {
			throw APIException.build(HttpStatus.CONFLICT, "Tarefa já está ativa!");
		}
	}
	public void ativaTarefa() {
		if (this.statusAtivacao.equals(StatusAtivacaoTarefa.INATIVA)) {
			this.statusAtivacao = StatusAtivacaoTarefa.ATIVA;
		}
	}
}
