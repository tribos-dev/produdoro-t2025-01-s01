package dev.wakandaacademy.produdoro.usuario.application.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {

	@InjectMocks
	UsuarioApplicationService usuarioApplicationService;
	
	@Mock
	UsuarioRepository usuarioRepository;
	
	@Test
	void mudaStatusParaPausaLonga() {
		Usuario usuario = DataHelper.createUsuario();

		when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
		usuarioApplicationService.mudaStatusPausaLonga(usuario.getEmail(), usuario.getIdUsuario());
		assertEquals(StatusUsuario.PAUSA_LONGA, usuario.getStatus());
		verify(usuarioRepository, times(1)).salva(usuario);
	}

	@Test
	void naoMudaParaPausaLonga() {
		Usuario usuario = DataHelper.createUsuario();

		UUID idUsuario = UUID.fromString("5365f9d4-1156-433d-839c-231dd2dd5b95");
		when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
		APIException ex = assertThrows(APIException.class, () -> usuarioApplicationService.mudaStatusPausaLonga(usuario.getEmail(), idUsuario));
		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusException());
	}
}