package com.example.sum1.service;

import com.example.sum1.exception.ResourceNotFoundException;
import com.example.sum1.model.Role;
import com.example.sum1.model.Usuario;
import com.example.sum1.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("John");
        usuario.setApellido("Doe");
        usuario.setUsername("johndoe");
        usuario.setPassword("password");
        usuario.setEmail("johndoe@example.com");
        usuario.setRol(Role.ROLE_USER);
    }

    @Test
    void registrarUsuario() {
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.registrarUsuario(usuario);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    // Agregar pruebas para validar campos nulos o vacíos

    @Test
    void registrarUsuario_FaltaNombre() {
        usuario.setNombre(null);

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuario);
        }, "El nombre es obligatorio.");
    }

    @Test
    void registrarUsuario_FaltaApellido() {
        usuario.setApellido(null);

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuario);
        }, "El apellido es obligatorio.");
    }

    @Test
    void registrarUsuario_FaltaUsername() {
        usuario.setUsername(null);

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuario);
        }, "El nombre de usuario es obligatorio.");
    }

    @Test
    void registrarUsuario_FaltaPassword() {
        usuario.setPassword(null);

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuario);
        }, "La contraseña es obligatoria.");
    }

    @Test
    void registrarUsuario_FaltaEmail() {
        usuario.setEmail(null);

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuario);
        }, "El correo electrónico es obligatorio.");
    }

    @Test
    void registrarUsuario_FaltaRol() {
        usuario.setRol(null);

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuario);
        }, "El rol es obligatorio.");
    }

    @Test
    void buscarPorUsername() {
        when(usuarioRepository.findByUsername(any(String.class))).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.buscarPorUsername("johndoe");

        assertTrue(result.isPresent());
        assertEquals("johndoe", result.get().getUsername());
    }

    @Test
    void buscarPorEmail() {
        when(usuarioRepository.findByEmail(any(String.class))).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.buscarPorEmail("johndoe@example.com");

        assertNotNull(result);
        assertEquals("johndoe@example.com", result.getEmail());
    }

    @Test
    void buscarPorEmail_NotFound() {
        when(usuarioRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.buscarPorEmail("notfound@example.com");
        });
    }

    @Test
    void existePorUsername() {
        when(usuarioRepository.existsByUsername(any(String.class))).thenReturn(true);

        Boolean result = usuarioService.existePorUsername("johndoe");

        assertTrue(result);
    }

    @Test
    void existePorEmail() {
        when(usuarioRepository.existsByEmail(any(String.class))).thenReturn(true);

        Boolean result = usuarioService.existePorEmail("johndoe@example.com");

        assertTrue(result);
    }

    @Test
    void obtenerTodosLosUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> result = usuarioService.obtenerTodosLosUsuarios();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void esAdmin() {
        usuario.setRol(Role.ROLE_ADMIN);
        when(usuarioRepository.findByUsername(any(String.class))).thenReturn(Optional.of(usuario));

        Boolean result = usuarioService.esAdmin("johndoe");

        assertTrue(result);
    }

    @Test
    void esAdmin_NoEsAdmin() {
        usuario.setRol(Role.ROLE_USER);  // Rol de usuario normal
        when(usuarioRepository.findByUsername(any(String.class))).thenReturn(Optional.of(usuario));

        Boolean result = usuarioService.esAdmin("johndoe");

        assertFalse(result);
    }

    @Test
    void actualizarUsuario() {
        when(usuarioRepository.findById(any(Long.class))).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setUsername("newUsername");
        usuarioActualizado.setPassword("newPassword");
        usuarioActualizado.setEmail("newEmail@example.com");
        usuarioActualizado.setNombre("NewName");
        usuarioActualizado.setApellido("NewApellido");
        usuarioActualizado.setRol(Role.ROLE_USER);

        Usuario result = usuarioService.actualizarUsuario(1L, usuarioActualizado);

        assertNotNull(result);
        assertEquals("newUsername", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("newEmail@example.com", result.getEmail());
        assertEquals("NewName", result.getNombre());
        assertEquals("NewApellido", result.getApellido());
        assertEquals(Role.ROLE_USER, result.getRol());
    }

    @Test
    void actualizarUsuario_NoExiste() {
        when(usuarioRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.actualizarUsuario(1L, usuario);
        });
    }

    @Test
    void eliminarUsuario() {
        when(usuarioRepository.findById(any(Long.class))).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).delete(any(Usuario.class));

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository, times(1)).delete(any(Usuario.class));
    }

    @Test
    void eliminarUsuario_NoExiste() {
        when(usuarioRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.eliminarUsuario(1L);
        });
    }
}
