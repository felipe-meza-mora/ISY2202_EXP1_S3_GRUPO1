package com.example.sum1.controller;

import com.example.sum1.model.Usuario;
import com.example.sum1.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;



public class HomeControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private HomeController homeController;

    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        model = new BindingAwareModelMap();
    }

    @Test
    public void testRegistrarUsuario_UsernameIsEmpty() {
        String viewName = homeController.registrarUsuario("", "test@example.com", "Nombre", "Apellido", "password", model);
        assertEquals("register", viewName);
        assertEquals("El nombre de usuario es obligatorio.", model.getAttribute("errorUsername"));
    }

    @Test
    public void testRegistrarUsuario_EmailIsEmpty() {
        String viewName = homeController.registrarUsuario("username", "", "Nombre", "Apellido", "password", model);
        assertEquals("register", viewName);
        assertEquals("El correo electrónico es obligatorio.", model.getAttribute("errorEmail"));
    }

    @Test
    public void testRegistrarUsuario_NombreIsEmpty() {
        String viewName = homeController.registrarUsuario("username", "test@example.com", "", "Apellido", "password", model);
        assertEquals("register", viewName);
        assertEquals("El nombre es obligatorio.", model.getAttribute("errorNombre"));
    }

    @Test
    public void testRegistrarUsuario_ApellidoIsEmpty() {
        String viewName = homeController.registrarUsuario("username", "test@example.com", "Nombre", "", "password", model);
        assertEquals("register", viewName);
        assertEquals("El apellido es obligatorio.", model.getAttribute("errorApellido"));
    }

    @Test
    public void testRegistrarUsuario_PasswordIsEmpty() {
        String viewName = homeController.registrarUsuario("username", "test@example.com", "Nombre", "Apellido", "", model);
        assertEquals("register", viewName);
        assertEquals("La contraseña es obligatoria.", model.getAttribute("errorPassword"));
    }

    @Test
    public void testRegistrarUsuario_UsernameInUse() {
        when(usuarioService.existePorUsername("username")).thenReturn(true);
        String viewName = homeController.registrarUsuario("username", "test@example.com", "Nombre", "Apellido", "password", model);
        assertEquals("register", viewName);
        assertEquals("El nombre de usuario ya está en uso.", model.getAttribute("errorUsername"));
    }

    @Test
    public void testRegistrarUsuario_EmailInUse() {
        when(usuarioService.existePorEmail("test@example.com")).thenReturn(true);
        String viewName = homeController.registrarUsuario("username", "test@example.com", "Nombre", "Apellido", "password", model);
        assertEquals("register", viewName);
        assertEquals("El correo electrónico ya está en uso.", model.getAttribute("errorEmail"));
    }

    @Test
    public void testRegistrarUsuario_Success() {
        when(usuarioService.existePorUsername("username")).thenReturn(false);
        when(usuarioService.existePorEmail("test@example.com")).thenReturn(false);

        String viewName = homeController.registrarUsuario("username", "test@example.com", "Nombre", "Apellido", "password", model);

        assertEquals("redirect:/login", viewName);
        verify(usuarioService, times(1)).registrarUsuario(any(Usuario.class));
    }
}