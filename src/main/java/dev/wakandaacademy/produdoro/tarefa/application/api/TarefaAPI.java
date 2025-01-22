package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    
}
