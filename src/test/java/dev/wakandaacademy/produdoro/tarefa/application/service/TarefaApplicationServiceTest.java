package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
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
    void deveConcluirTarefa(){
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));

        tarefaApplicationService.concluirTarefa(tarefa.getIdTarefa(), usuario.getEmail());
        assertEquals(tarefa.getStatus(), StatusTarefa.CONCLUIDA);

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
}
