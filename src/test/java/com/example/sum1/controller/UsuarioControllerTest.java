package com.example.sum1.controller;

import com.example.sum1.model.Usuario;
import com.example.sum1.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;




class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerTodosLosUsuarios() {
        List<Usuario> usuarios = List.of(new Usuario(), new Usuario());
        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(usuarios);

        ResponseEntity<List<Usuario>> response = usuarioController.obtenerTodosLosUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarios, response.getBody());
    }

    @Test
    void testBuscarUsuarioPorUsername_UsuarioNoEncontrado() {
        String username = "testUser";
        when(usuarioService.buscarPorUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<String> response = usuarioController.buscarUsuarioPorUsername(username, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getBody());
    }

    @Test
    void testBuscarUsuarioPorUsername_UsuarioEncontrado() {
        String username = "testUser";
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
    
        when(usuarioService.buscarPorUsername(username)).thenReturn(Optional.of(usuario));
        
        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("ROLE_USER");
        when(authentication.getName()).thenReturn(username);

        ResponseEntity<String> response = usuarioController.buscarUsuarioPorUsername(username, authentication);
    
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuario.toString(), response.getBody());
    }
    


    @Test
    void testActualizarUsuario_UsuarioNoEncontrado() {
        Long id = 1L;
        Usuario usuarioActualizado = new Usuario();
        when(usuarioService.buscarPorUsername(anyString())).thenReturn(Optional.empty());

        ResponseEntity<String> response = usuarioController.actualizarUsuario(id, usuarioActualizado, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getBody());
    }

    @Test
    void testEliminarUsuario_UsuarioNoEncontrado() {
        Long id = 1L;
        when(usuarioService.buscarPorUsername(anyString())).thenReturn(Optional.empty());

        ResponseEntity<String> response = usuarioController.eliminarUsuario(id, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getBody());
    }
}