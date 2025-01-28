package dev.wakandaacademy.produdoro.usuario.application.service;

import java.util.UUID;

import javax.validation.Valid;

import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.wakandaacademy.produdoro.credencial.application.service.CredencialService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.pomodoro.application.service.PomodoroService;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioCriadoResponse;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioNovoRequest;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class UsuarioApplicationService implements UsuarioService {
	private final PomodoroService pomodoroService;
	private final CredencialService credencialService;
	private final UsuarioRepository usuarioRepository;

	@Override
	public UsuarioCriadoResponse criaNovoUsuario(@Valid UsuarioNovoRequest usuarioNovo) {
		log.info("[inicia] UsuarioApplicationService - criaNovoUsuario");
		var configuracaoPadrao = pomodoroService.getConfiguracaoPadrao();
		credencialService.criaNovaCredencial(usuarioNovo);
		var usuario = new Usuario(usuarioNovo,configuracaoPadrao);
		usuarioRepository.salva(usuario);
		log.info("[finaliza] UsuarioApplicationService - criaNovoUsuario");
		return new UsuarioCriadoResponse(usuario);
	}


	@Override
	public UsuarioCriadoResponse buscaUsuarioPorId(UUID idUsuario) {
		log.info("[inicia] UsuarioApplicationService - buscaUsuarioPorId");
		Usuario usuario = usuarioRepository.buscaUsuarioPorId(idUsuario);
		log.info("[finaliza] UsuarioApplicationService - buscaUsuarioPorId");
		return new UsuarioCriadoResponse(usuario);
	}

	@Override
	public void mudaStatusPausaLonga(String email, UUID idUsuario) {
		log.info("[inicia] UsuarioApplicationService - mudaStatusPausaLonga");
		Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(email);
		usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuario.mudaStatusPausaLonga(idUsuario);
		usuarioRepository.salva(usuario);
		log.info("[finaliza] UsuarioApplicationService - mudaStatusPausaLonga");	
	}

	@Override
	public void mudaStatusParaFoco(String usuario, UUID idUsuario) {
		log.info("[inicia] UsuarioApplicationService - mudaStatusFoco");
		Usuario usuarioFoco = usuarioRepository.buscaUsuarioPorEmail(usuario);
		usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuarioFoco.mudaStatusParaFoco(idUsuario);
		usuarioRepository.salva(usuarioFoco);
		log.info("[finaliza] UsuarioApplicationService - mudaStatusFoco");
	}

	@Override
	public void statusPausaCurta(String email, UUID idUsuario) {
		log.info("[inicia] UsuarioApplicationService -statusPausaCurta");
		Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(email);
		usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuario.validaUsuario(idUsuario);
		usuario.pausaCurta();
		usuarioRepository.salva(usuario);
		log.info("[finaliza] UsuarioApplicationService - statusPausaCurta");
		
	}


}
		
	



