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

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.config.security.service.TokenService;
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
	void testPausaCurta() {
		
		Usuario usuario = DataHelper.createUsuario();
		
		when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
		
		usuarioApplicationService.statusPausaCurta(usuario.getEmail(), usuario.getIdUsuario());
		assertEquals(StatusUsuario.PAUSA_CURTA, usuario.getStatus());
		verify(usuarioRepository, times(1)).salva(usuario);
		
		

	}
	
	

	}
