package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/tarefa")
public interface TarefaAPI {
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    TarefaIdResponse postNovaTarefa(@RequestBody @Valid TarefaRequest tarefaRequest);

    @GetMapping("/{idTarefa}")
    @ResponseStatus(code = HttpStatus.OK)
    TarefaDetalhadoResponse detalhaTarefa(@RequestHeader(name = "Authorization",required = true) String token, 
    		@PathVariable UUID idTarefa);

    @PatchMapping("/editaTarefa/{idTarefa}")
    void editaTarefa (@RequestHeader(name = "Authorization",required = true) String token, @PathVariable UUID idTarefa,
    		@RequestBody @Valid EditaTarefaRequest editaTarefaRequest);


    @PatchMapping(value = "/conclui-tarefa/{idTarefa}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void concluiTarefa(@RequestHeader(name = "Authorization", required = true) String token,
                                    @PathVariable UUID idTarefa);

    @PatchMapping(value = "/muda-ordem-tarefa")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void alteraOrdemTarefa(@RequestHeader(name = "Authorization", required = true) String token,
                       @RequestParam UUID idTarefa, @RequestParam int novaPosicao);

    @DeleteMapping("limpar-tarefas/{idUsuario}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void limparTodasTarefas(@RequestHeader(name = "Authorization",required = true) String token,
                                          @PathVariable UUID idUsuario);

    @PatchMapping("/ativaTarefa/{idTarefa}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void usuarioAtivaTarefa(@RequestHeader(name = "Authorization",required = true) String token,
                            @PathVariable UUID idTarefa);

    @GetMapping("/listarTarefas/{idUsuario}")
    @ResponseStatus(code = HttpStatus.OK)
    List<TarefaListResponse> listarTarefasUsuario(@RequestHeader(name = "Authorization",required = true) String token,@PathVariable UUID idUsuario);
    
    @PostMapping("/incrementaPomodoro/{idTarefa}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void incrementaPomodoro(@RequestHeader(name = "Authorization", required = true) String token, @PathVariable UUID idTarefa);

}
