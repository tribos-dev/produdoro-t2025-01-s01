package dev.wakandaacademy.produdoro.tarefa.infra;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TarefaInfraRepository implements TarefaRepository {

    private final TarefaSpringMongoDBRepository tarefaSpringMongoDBRepository;

    @Override
    public Tarefa salva(Tarefa tarefa) {
        log.info("[inicia] TarefaInfraRepository - salva");
        try {
            tarefaSpringMongoDBRepository.save(tarefa);
        } catch (DataIntegrityViolationException e) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Tarefa já cadastrada", e);
        }
        log.info("[finaliza] TarefaInfraRepository - salva");
        return tarefa;
    }
    @Override
    public Optional<Tarefa> buscaTarefaPorId(UUID idTarefa) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefaPorId");
        Optional<Tarefa> tarefaPorId = tarefaSpringMongoDBRepository.findByIdTarefa(idTarefa);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefaPorId");
        return tarefaPorId;

    }

    @Override
    public boolean deletaConcluidas(UUID idUsuario, StatusTarefa statusTarefa) {
        log.info("[inicia] TarefaInfraRepository - deletaConcluidas");
        long deletedCount = tarefaSpringMongoDBRepository.deleteByIdUsuarioAndStatus(idUsuario, StatusTarefa.CONCLUIDA);
        log.info("[inicia] TarefaInfraRepository - deletaConcluidas");
        return deletedCount > 0;
    }

    @Override
    public List<Tarefa> listarTarefasPorIdusuario(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - listarTarefasPorIdusuario");
        List<Tarefa> listaTarefa = tarefaSpringMongoDBRepository.findAllByIdUsuario(idUsuario);
        log.info("[fim] TarefaInfraRepository - listarTarefasPorIdusuario");
        return listaTarefa;
    }

    @Override
    public void limparTodasAsTarefas(List<Tarefa> tarefas) {
        log.info("[inicio] TarefaInfraRepository - limparTodasAsTarefas");

            tarefaSpringMongoDBRepository.deleteAll(tarefas);

        log.info("[final] TarefaInfraRepository - limparTodasAsTarefas");
    }

    @Override
    public List<Tarefa> buscaTarefasPorUsuario(UUID idUsuario) {
        return tarefaSpringMongoDBRepository.findAllByIdUsuario(idUsuario);
    }
}
