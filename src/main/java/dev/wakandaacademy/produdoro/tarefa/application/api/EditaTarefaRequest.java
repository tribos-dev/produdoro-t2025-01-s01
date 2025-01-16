package dev.wakandaacademy.produdoro.tarefa.application.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;

@Getter
public class EditaTarefaRequest {
	
	@NotBlank
	@Size (message = "Campo Descrição Tarefa Não Pode Estar Vazio", max = 255, min = 1)
	private String descricao;
	

}
