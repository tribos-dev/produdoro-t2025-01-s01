package dev.wakandaacademy.produdoro.tarefa.application.service;


import static org.junit.jupiter.api.Assertions.*;
import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.*;
import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.infra.TarefaSpringMongoDBRepository;
import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

    //	@Autowired
    @InjectMocks
    TarefaApplicationService tarefaApplicationService;

    //	@MockBean
    @Mock
    TarefaRepository tarefaRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarIdTarefaNovaCriada() {
        TarefaRequest request = getTarefaRequest();
        int quantidadeDeTarefas = 1;
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request, 1));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);
        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }

    @Test
    void devEditarTarefa() {
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        EditaTarefaRequest editaTarefaRequest = DataHelper.creatEditaTarefa();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.editaTarefa(usuario.getEmail(), tarefa.getIdTarefa(), editaTarefaRequest);

        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
        verify(tarefaRepository, times(1)).buscaTarefaPorId(tarefa.getIdTarefa());
        assertEquals("tarefa2", tarefa.getDescricao());
    }


    @Test
    void deveConcluirTarefa() {
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.concluirTarefa(tarefa.getIdTarefa(), usuario.getEmail());
        assertEquals(tarefa.getStatus(), StatusTarefa.CONCLUIDA);

    }

    @Test
    void deveDeletarTodasAsTarefasDoUsuario() {
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> tarefas = DataHelper.createListTarefa();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTodasTarefasPorIdUsuario(any())).thenReturn(tarefas);
        tarefaApplicationService.limparTodasTarefas(usuario.getIdUsuario(), usuario.getEmail());
        verify(tarefaRepository, times(1)).limparTodasAsTarefas(tarefas);
    }
    @Test
    void deveDeletarTarefasConcluidas() {
        Usuario usuario = DataHelper.createUsuario();
        UUID idUsuario = usuario.getIdUsuario();
        String usuarioEmail = usuario.getEmail();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(tarefaRepository.deletaConcluidas(idUsuario, StatusTarefa.CONCLUIDA)).thenReturn(true);

        tarefaApplicationService.deletaTarefasConcluidas(usuarioEmail, idUsuario);
        verify(tarefaRepository, times(1)).deletaConcluidas(idUsuario, StatusTarefa.CONCLUIDA);
    }

    @Test
    void naoDeveExcluirTarefasConcluidas(){
        Usuario usuario = DataHelper.createUsuario();
        UUID idUsuario = usuario.getIdUsuario();
        String usuarioEmail = usuario.getEmail();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);

        assertThrows(APIException.class,() -> tarefaApplicationService.deletaTarefasConcluidas(usuarioEmail, idUsuario));
    }

    @Test
    void naoDeletarTarefasConcluidasQuandoUsuarioInvalido(){
        Usuario usuario = DataHelper.createUsuario();
        UUID idUsuario = UUID.randomUUID();
        String usuarioEmail = usuario.getEmail();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);

        assertThrows(APIException.class,() -> tarefaApplicationService.deletaTarefasConcluidas(usuarioEmail, idUsuario));
    }


    @Test
    public void deveMudarOrdemDeUmaTarefa() {
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> tarefas = DataHelper.createListTarefaToChangePosition();
        int posicao = 0;

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(tarefas.get(1)));

        when(tarefaRepository.buscaTodasTarefasPorIdUsuario(any()))
                .thenReturn(tarefas);

        assertThrows(APIException.class, () -> tarefaApplicationService.alteraPosicaoTarefa(
                usuario.getEmail(), tarefas.get(1).getIdTarefa(), posicao));
        tarefas.get(1).setPosicao(posicao);

        tarefaApplicationService.alteraPosicaoTarefa(usuario.getEmail(),
                tarefas.get(1).getIdTarefa(), posicao);

        assertEquals(posicao, tarefas.get(1).getPosicao());
    }

    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }
    
    @Test
    void deveListarTarefasDoUsuario() {
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> listaTarefas = DataHelper.createListTarefa();
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefasDoUsuario(any())).thenReturn(listaTarefas);
        String usuarioEmail = "email@email.com";
        UUID idUsuario = UUID.fromString("a713162f-20a9-4db9-a85b-90cd51ab18f4");
        List<TarefaListResponse> response = tarefaApplicationService.buscarTodasAsTarefas(usuarioEmail, idUsuario);
        assertNotNull(response);
        assertEquals(ArrayList.class, response.getClass());
        assertEquals(8, response.size());
    }

    @Test
    void ativaTarefaDeveAtivarTarefa() {
        UUID idTarefa = DataHelper.createTarefa().getIdTarefa();
        UUID idUsuario = DataHelper.createUsuario().getIdUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        Usuario usuario = DataHelper.createUsuario();
        String email = "email@gmail.com";
        when(usuarioRepository.buscaUsuarioPorEmail(email)).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.ativaTarefa(email, idTarefa);
        verify(tarefaRepository, times(1)).buscaTarefaPorId(idTarefa);
        verify(tarefaRepository, times(1)).desativaTarefa(idUsuario);
        assertEquals(StatusAtivacaoTarefa.ATIVA, tarefa.getStatusAtivacao());
    }
    
    @Test
    void deveIncrementarPomodoro() {
    	Usuario usuario = DataHelper.createUsuario();
    	Tarefa tarefa = DataHelper.createTarefa();
    	int contagemPomodoroInicio = tarefa.getContagemPomodoro();
    	
    	when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
    	when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
    	tarefaApplicationService.incrementaPomodoro(usuario.getEmail(), tarefa.getIdTarefa());
    	int contagemPomodoroFinal = tarefa.getContagemPomodoro();
    	verify(tarefaRepository, times(1)).salva(any());
    	assertEquals(contagemPomodoroInicio + 1, contagemPomodoroFinal);
     }
}



