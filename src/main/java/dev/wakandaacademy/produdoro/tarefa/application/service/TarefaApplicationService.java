package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;


    @Override
    public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
        log.info("[inicia] TarefaApplicationService - criaNovaTarefa");
        Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest));
        log.info("[finaliza] TarefaApplicationService - criaNovaTarefa");
        return TarefaIdResponse.builder().idTarefa(tarefaCriada.getIdTarefa()).build();
    }
    
    @Override
    public Tarefa detalhaTarefa(String usuario, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - detalhaTarefa");
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
        log.info("[usuarioPorEmail] {}", usuarioPorEmail);
        Tarefa tarefa =
                tarefaRepository.buscaTarefaPorId(idTarefa).orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
        tarefa.pertenceAoUsuario(usuarioPorEmail);
        log.info("[finaliza] TarefaApplicationService - detalhaTarefa");
        return tarefa;
    }
	@Override
	public void editaTarefa(String emailUsuario, UUID idTarefa, 
			@Valid EditaTarefaRequest editaTarefaRequest) {
		log.info("[inicia] TarefaApplicationService - editaTarefa");
		Tarefa tarefa = detalhaTarefa(emailUsuario, idTarefa);
		tarefa.edita(editaTarefaRequest);
		tarefaRepository.salva(tarefa);
		log.info("[inicia] TarefaApplicationService - editaTarefa");
	}

    @Override
    public void concluirTarefa(UUID idTarefa, String usuarioEmail) {
        log.info("[inicia] TarefaApplicationService - concluirTarefa");

        Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(usuarioEmail);
        Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        handlerConcluirTarefaVerification(tarefa, usuario);

        tarefa.setStatus(StatusTarefa.CONCLUIDA);
        tarefaRepository.salva(tarefa);
        log.info("[finaliza] TarefaApplicationService - concluirTarefa");


    }
    private void handlerConcluirTarefaVerification(Tarefa tarefa, Usuario usuario){
        if(!tarefa.getIdUsuario().equals(usuario.getIdUsuario()))
            throw APIException.build(HttpStatus.UNAUTHORIZED, "Tarefa não pertence a esse usuario");

        if(tarefa.getStatus().equals(StatusTarefa.CONCLUIDA))
            throw APIException.build(HttpStatus.BAD_REQUEST, "Tarefa já está concluída");

    }



    @Override
    public void limparTodasTarefas(UUID idUsuario, String emailUsuario) {
        log.info("[inicia] TarefaApplicationService - limparTodasTarefas");
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(emailUsuario);
        Usuario usuarioPorId = usuarioRepository.buscaUsuarioPorId(idUsuario);
        handleLimparTodasTarefasVerification(usuarioPorId, usuarioPorEmail);
        handleAmountTaskVerification(usuarioPorEmail);
        tarefaRepository.limparTodasAsTarefas(
                tarefaRepository.listarTarefasPorIdusuario(idUsuario));
        log.info("[fim] TarefaApplicationService - limparTodasTarefas");
    }

    private void handleLimparTodasTarefasVerification(Usuario usuarioPorId, Usuario usuarioPorEmail) {

        if(usuarioPorId == null)
            throw APIException.build(
                    HttpStatus.NOT_FOUND, "Usuário não encontrado");

        if(!usuarioPorEmail.getIdUsuario().equals(usuarioPorId.getIdUsuario()))
            throw APIException.build(
                    HttpStatus.UNAUTHORIZED, "Usuário(a) não autorizado(a) para a requisição solicitada");

    }

    @Override
    public void ativaTarefa(String email, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - ativaTarefa");
        Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
                .orElseThrow(()-> APIException.build(HttpStatus.NOT_FOUND, "ID da tarefa inválido"));
        Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(email);
        tarefa.pertenceAoUsuario(usuario);
        tarefa.verificaTarefaAtiva();
        tarefaRepository.desativaTarefa(usuario.getIdUsuario());
        tarefa.ativaTarefa();
        tarefaRepository.salva(tarefa);
        log.info("[finaliza] TarefaApplicationService - ativaTarefa");
    }


    private void handleAmountTaskVerification(Usuario usuario) {
        List<Tarefa> tarefas = tarefaRepository.listarTarefasPorIdusuario(usuario.getIdUsuario());
        if(tarefas.isEmpty())
            throw APIException.build(HttpStatus.CONFLICT, "Usuário não possui tarefa(as) cadastrada(as)");

       if(tarefas.size() < 2)
           throw APIException.build(HttpStatus.CONFLICT,
                   "Deve existir pelo menos duas tarefas cadastradas no registro");
   }
}
