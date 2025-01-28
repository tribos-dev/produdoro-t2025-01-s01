package dev.wakandaacademy.produdoro.tarefa.application.service;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
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
        when(tarefaRepository.listarTarefasPorIdusuario(any())).thenReturn(tarefas);
        tarefaApplicationService.limparTodasTarefas(usuario.getIdUsuario(), usuario.getEmail());
        verify(tarefaRepository, times(1)).limparTodasAsTarefas(tarefas);
    }


    @Test
    public void testNaoMudaOrdemDeUmaTarefa() {
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




    @Test
    public void deveMudarOrdemDeUmaTarefa() {
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> tarefas = DataHelper.createListTarefa();
    }

    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
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
}
