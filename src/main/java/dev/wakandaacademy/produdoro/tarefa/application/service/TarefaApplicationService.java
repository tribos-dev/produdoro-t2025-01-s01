package dev.wakandaacademy.produdoro.tarefa.application.service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;


    @Override
    public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
        log.info("[inicia] TarefaApplicationService - criaNovaTarefa");
        int posicao = retornaProximaPosicaoDisponivel(tarefaRequest.getIdUsuario());
        Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest, posicao));
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
    public void alteraPosicaoTarefa(String usuarioEmail, UUID idTarefa, int novaPosicao) {
        log.info("[inicia] TarefaApplicationService - alteraPosicaoTarefa");

        Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(usuarioEmail);
        Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        verificaSeTarefaPertenceUsuario(usuario, tarefa);

        List<Tarefa> tarefasOrdenadas = tarefaRepository.buscaTodasTarefasPorIdUsuario(usuario.getIdUsuario()).stream()
                .sorted(Comparator.comparingInt(Tarefa::getPosicao))
                .collect(Collectors.toList());

        verificaRangeDePosicoes(tarefasOrdenadas, novaPosicao);

        mudaPosicao(tarefa, novaPosicao, tarefasOrdenadas);
        tarefasOrdenadas.forEach(tarefaRepository::salva);

        log.info("[fim] TarefaApplicationService - alteraPosicaoTarefa");
    }



    @Override
    public void concluirTarefa(UUID idTarefa, String usuarioEmail) {
        log.info("[inicia] TarefaApplicationService - concluirTarefa");

        Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(usuarioEmail);
        Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        verificaTarefaJaConcluida(tarefa, usuario);
        verificaSeTarefaPertenceUsuario(usuario, tarefa);

        tarefa.setStatus(StatusTarefa.CONCLUIDA);
        tarefaRepository.salva(tarefa);
        log.info("[finaliza] TarefaApplicationService - concluirTarefa");

    }


    private void verificaTarefaJaConcluida(Tarefa tarefa, Usuario usuario){
        log.info("[inicia] TarefaApplicationService - verificaTarefaJaConcluida");
        if(tarefa.getStatus().equals(StatusTarefa.CONCLUIDA))
            throw APIException.build(HttpStatus.BAD_REQUEST, "Tarefa já está concluída");
        log.info("[fim] TarefaApplicationService - verificaTarefa");

    }

    private void verificaSeTarefaPertenceUsuario(Usuario usuario, Tarefa tarefa) {
        log.info("[inicia] TarefaApplicationService - verificaSeTarefaPertenceUsuario");
        if(!tarefa.getIdUsuario().equals(usuario.getIdUsuario()))
            throw APIException.build(HttpStatus.UNAUTHORIZED, "Tarefa não pertence a esse usuario");
        log.info("[finaliza] Tarefa pertence a esse usuario");
    }

    private int retornaProximaPosicaoDisponivel(UUID idUsuario) {
        log.info("[inicia] TarefaApplicationService - retornaPosicaoDisponivel");
        List<Tarefa> listaTarefa = tarefaRepository.buscaTodasTarefasPorIdUsuario(idUsuario);

        Tarefa tarefaMaiorPosicao = listaTarefa.stream().max(Comparator.comparingInt(
                Tarefa::getPosicao)).orElse(null);

        log.info("[fim] TarefaApplicationService - retornaPosicaoTarefa");
        return tarefaMaiorPosicao == null ? 0 : (tarefaMaiorPosicao.getPosicao() + 1);
    }


    private static void mudaPosicao(Tarefa tarefa, int novaPosicao, List<Tarefa> tList) {
        log.info("[inicia] TarefaApplicationService - mudaPosicao");

        int indexAtual = tList.indexOf(tarefa);
        int posicaoAtual = tarefa.getPosicao();

        tarefa.setPosicao(novaPosicao);

        if (novaPosicao < posicaoAtual) {
            for (Tarefa t : tList) {
                if (t.getPosicao() >= novaPosicao && t.getPosicao() < posicaoAtual) {
                    t.setPosicao(t.getPosicao() + 1);
                }
            }
        } else if (novaPosicao > posicaoAtual) {
            for (Tarefa t : tList) {
                if (t.getPosicao() <= novaPosicao && t.getPosicao() > posicaoAtual) {
                    t.setPosicao(t.getPosicao() - 1);
                }
            }
        }
        tList.get(indexAtual).setPosicao(novaPosicao);
        tList.sort(Comparator.comparingInt(Tarefa::getPosicao));

        log.info("[finaliza] TarefaApplicationService - mudaPosicao");
    }

    private void verificaRangeDePosicoes(List<Tarefa> tarefasOrdenadas, int novaPosicao) {
        log.info("[inicia] TarefaApplicationService - verificaRangeDePosicoes");
        Tarefa TarefaUltimaPosicao = tarefasOrdenadas.stream().max(Comparator.comparingInt(
                Tarefa::getPosicao)).orElse(null);

        if(TarefaUltimaPosicao != null && novaPosicao > TarefaUltimaPosicao.getPosicao())
            throw APIException.build(HttpStatus.BAD_REQUEST, "Posicao fora do range de posicoes");
        log.info("[fim] TarefaApplicationService - verificaRangeDePosicoes");
    }




    @Override
    public void limparTodasTarefas(UUID idUsuario, String emailUsuario) {
        log.info("[inicia] TarefaApplicationService - limparTodasTarefas");
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(emailUsuario);
        Usuario usuarioPorId = usuarioRepository.buscaUsuarioPorId(idUsuario);
        handleLimparTodasTarefasVerification(usuarioPorId, usuarioPorEmail);
        handleAmountTaskVerification(usuarioPorEmail);
        tarefaRepository.limparTodasAsTarefas(
                tarefaRepository.buscaTodasTarefasPorIdUsuario(idUsuario));
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
        List<Tarefa> tarefas = tarefaRepository.buscaTodasTarefasPorIdUsuario(usuario.getIdUsuario());
        if(tarefas.isEmpty())
            throw APIException.build(HttpStatus.CONFLICT, "Usuário não possui tarefa(as) cadastrada(as)");

       if(tarefas.size() < 2)
           throw APIException.build(HttpStatus.CONFLICT,
                   "Deve existir pelo menos duas tarefas cadastradas no registro");
   }

    @Override
    public void deletaTarefasConcluidas(String usuarioEmail, UUID idUsuario) {
        log.info("[inicia] TarefaApplicationService - deletaTarefaconcluida");
        usuarioRepository.buscaUsuarioPorId(idUsuario);
        Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(usuarioEmail);
        log.info("[usuarioPorEmail] {}", usuario);
        usuario.validaUsuario(idUsuario);
        boolean tarefasExcluidas = tarefaRepository.deletaConcluidas(idUsuario, StatusTarefa.CONCLUIDA);
        log.info("[tarefasExcluidas] {}", tarefasExcluidas);
        if (!tarefasExcluidas) {
            throw APIException.build(HttpStatus.NOT_FOUND, "Usuário não possui nenhuma tarefa concluída!");
        }
        log.info("[finaliza] TarefaApplicationService - deletaTarefaconcluida");
    }
    
    @Override
    public List<TarefaListResponse> buscarTodasAsTarefas(String usuario, UUID idUsuario) {
        log.info("[inicia] TarefaApplicationService - buscaTodasTarefas");
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
        usuarioRepository.buscaUsuarioPorId(idUsuario);
        usuarioPorEmail.validaUsuario(idUsuario);
        List<Tarefa> tarefas = tarefaRepository.buscaTarefasDoUsuario(idUsuario);
        log.info("[finaliza] TarefaApplicationService - buscaTodasTarefas");
        return TarefaListResponse.converte(tarefas);
    }

	@Override
	public void incrementaPomodoro(String usuarioEmail, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - incrementaPomodoro");
	    Tarefa tarefa = detalhaTarefa(usuarioEmail, idTarefa);
	    Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(usuarioEmail);
	    tarefa.incrementaPomodoro(tarefa, usuario);
	    usuarioRepository.salva(usuario);
	    tarefaRepository.salva(tarefa);
	    log.info("[finaliza] TarefaApplicationService - incrementaPomodoro");
		
	}
}
