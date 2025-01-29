package dev.wakandaacademy.produdoro.usuario.application.api;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import dev.wakandaacademy.produdoro.config.security.service.TokenService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Validated
@Log4j2
@RequiredArgsConstructor
public class UsuarioController implements UsuarioAPI {
	
	private final TokenService tokenService;
	private final UsuarioService usuarioAppplicationService;
	
	


	@Override
	public UsuarioCriadoResponse postNovoUsuario(@Valid UsuarioNovoRequest usuarioNovo) {
		log.info("[inicia] UsuarioController - postNovoUsuario");
		UsuarioCriadoResponse usuarioCriado = usuarioAppplicationService.criaNovoUsuario(usuarioNovo);
		log.info("[finaliza] UsuarioController - postNovoUsuario");
		return usuarioCriado;
	}
	@Override
	public UsuarioCriadoResponse buscaUsuarioPorId(UUID idUsuario) {
		log.info("[inicia] UsuarioController - buscaUsuarioPorId");
		log.info("[idUsuario] {}", idUsuario);
		UsuarioCriadoResponse buscaUsuario = usuarioAppplicationService.buscaUsuarioPorId(idUsuario);
		log.info("[finaliza] UsuarioController - buscaUsuarioPorId");
		return buscaUsuario;
	}
	@Override
	public void mudaStatusPausaCurta(String token, UUID idUsuario) {
		log.info("[inicia] UsuarioController - mudaStatusPausaCurta");
		log.info("[idUsuario] {}", idUsuario);
		String usuario = tokenService.getUsuarioByBearerToken(token)
				.orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED,"Token incorreto"));
		usuarioAppplicationService.statusPausaCurta(usuario, idUsuario);
		log.info("[finaliza] UsuarioController - mudaStatusPausaCurta");
	}
	
	@Override
	public void mudaStatusPausaLonga(String token, UUID idUsuario) {
		log.info("[inicia] UsuarioController - mudaStatusPausaLonga");
		String email = getUsuarioToken(token);
		usuarioAppplicationService.mudaStatusPausaLonga(email, idUsuario);
		log.info("[finaliza] UsuarioController - mudaStatusPausaLonga");		
	}

	@Override
	public void mudaStatusParaFoco(String token, UUID idUsuario) {
		log.info("[inicia] UsuarioController - mudaStatusFoco");
		String usuario = validaUsuarioToken(token);
		usuarioAppplicationService.mudaStatusParaFoco(usuario, idUsuario);
		log.info("[finaliza] UsuarioController - mudaStatusFoco");
	}

	private String validaUsuarioToken(String token) {
		return tokenService.getUsuarioByBearerToken(token)
				.orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED,"Credenciais invalidas") );
	}


	private String getUsuarioToken(String token) {
		log.debug("[token] {}", token);
		String usuario = tokenService.getUsuarioByBearerToken(token).
				orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, "Credencial de autenticação não é válida."));
		log.debug("[token] {}", usuario);
		return usuario;
	}
}
