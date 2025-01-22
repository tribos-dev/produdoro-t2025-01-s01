package dev.wakandaacademy.produdoro.usuario.application.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

	@Test
	void deveMudarStatusParaFoco(){
		Usuario usuario = DataHelper.createUsuario2();

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
		usuarioApplicationService.mudaStatusParaFoco(usuario.getEmail(), usuario.getIdUsuario());

		verify(usuarioRepository, times(1)).salva(usuario);
		assertEquals(StatusUsuario.FOCO, usuario.getStatus());
	}

	@Test
	void deveLancarExcecaoQuandoUsuarioNaoAutorizado(){
		Usuario usuarioEmail = DataHelper.createUsuario();
		Usuario usuarioId = DataHelper.createUsuario();
        UUID idInvalido = UUID.fromString("0d0cf6b7-1921-40ed-b898-4f08c3b33b0f");

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuarioEmail);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuarioId);

		assertThrows(APIException.class, () -> usuarioApplicationService.mudaStatusParaFoco(usuarioEmail.getEmail(), idInvalido));
		verify(usuarioRepository, never()).salva(usuarioId);
	}

	@Test
	void deveLancarExcecaoQuandoUsuarioJaEstaEmFoco(){
		Usuario usuario = DataHelper.createUsuario();

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);


		APIException e = assertThrows(APIException.class,
				() -> usuarioApplicationService.mudaStatusParaFoco(usuario.getEmail(), usuario.getIdUsuario()));
		assertEquals("Usuário já está em foco!", e.getMessage());
		assertThrows(APIException.class, () -> usuarioApplicationService.mudaStatusParaFoco(usuario.getEmail(), usuario.getIdUsuario()));
		assertEquals(HttpStatus.BAD_REQUEST, e.getStatusException());
	}
}