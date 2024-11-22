package com.example.sum1.controller;

import com.example.sum1.model.Usuario;
import com.example.sum1.service.UsuarioService;
import com.example.sum1.model.Receta;
import com.example.sum1.service.RecetaService;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.sum1.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Optional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.servlet.ModelAndView;
import com.example.sum1.model.Role;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class HomeControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private RecetaService recetaService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private HomeController homeController;

    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        model = new BindingAwareModelMap();
    }

    @Test
    void testRegistrarUsuario_UsernameIsEmpty() {
        String viewName = homeController.registrarUsuario("", "test@example.com", "Nombre", "Apellido", "password", model);
        assertEquals("register", viewName);
        assertEquals("El nombre de usuario es obligatorio.", model.getAttribute("errorUsername"));
    }

    @Test
    void testRegistrarUsuario_EmailIsEmpty() {
        String viewName = homeController.registrarUsuario("username", "", "Nombre", "Apellido", "password", model);
        assertEquals("register", viewName);
        assertEquals("El correo electrónico es obligatorio.", model.getAttribute("errorEmail"));
    }

    @Test
    void testRegistrarUsuario_NombreIsEmpty() {
        String viewName = homeController.registrarUsuario("username", "test@example.com", "", "Apellido", "password", model);
        assertEquals("register", viewName);
        assertEquals("El nombre es obligatorio.", model.getAttribute("errorNombre"));
    }

    @Test
    void testRegistrarUsuario_ApellidoIsEmpty() {
        String viewName = homeController.registrarUsuario("username", "test@example.com", "Nombre", "", "password", model);
        assertEquals("register", viewName);
        assertEquals("El apellido es obligatorio.", model.getAttribute("errorApellido"));
    }

    @Test
    void testRegistrarUsuario_PasswordIsEmpty() {
        String viewName = homeController.registrarUsuario("username", "test@example.com", "Nombre", "Apellido", "", model);
        assertEquals("register", viewName);
        assertEquals("La contraseña es obligatoria.", model.getAttribute("errorPassword"));
    }

    @Test
    void testRegistrarUsuario_UsernameInUse() {
        when(usuarioService.existePorUsername("username")).thenReturn(true);
        String viewName = homeController.registrarUsuario("username", "test@example.com", "Nombre", "Apellido", "password", model);
        assertEquals("register", viewName);
        assertEquals("El nombre de usuario ya está en uso.", model.getAttribute("errorUsername"));
    }

    @Test
    void testRegistrarUsuario_EmailInUse() {
        when(usuarioService.existePorEmail("test@example.com")).thenReturn(true);
        String viewName = homeController.registrarUsuario("username", "test@example.com", "Nombre", "Apellido", "password", model);
        assertEquals("register", viewName);
        assertEquals("El correo electrónico ya está en uso.", model.getAttribute("errorEmail"));
    }

    @Test
    void testRegistrarUsuario_Success() {
        when(usuarioService.existePorUsername("username")).thenReturn(false);
        when(usuarioService.existePorEmail("test@example.com")).thenReturn(false);

        String viewName = homeController.registrarUsuario("username", "test@example.com", "Nombre", "Apellido", "password", model);

        assertEquals("redirect:/login", viewName);
        verify(usuarioService, times(1)).registrarUsuario(any(Usuario.class));
    }

    @Test
    void testRedirectToHome() {
        String viewName = homeController.redirectToHome();
        assertEquals("redirect:/home", viewName);
    }

    @Test
    void testHome() {
        Principal principal = mock(Principal.class);
        List<Receta> recetas = List.of(new Receta(), new Receta());
        when(recetaService.getAllRecetas()).thenReturn(recetas);

        String viewName = homeController.home(model, principal);
        assertEquals("home", viewName);
        assertEquals(recetas, model.getAttribute("recetas"));
        assertEquals(true, model.getAttribute("isAuthenticated"));
    }

    @Test
    void testLogin() {
        String viewName = homeController.login();
        assertEquals("login", viewName);
    }

    @Test
    void testLoginPost() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        Usuario usuario = new Usuario();
        usuario.setPassword("encodedPassword");
        when(usuarioService.buscarPorUsername("username")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(usuario)).thenReturn("jwtToken");

        ModelAndView modelAndView = homeController.loginPost("username", "password", response);
        assertEquals("redirect:/home", modelAndView.getViewName());
    }

    @Test
    void testDetalleReceta() {
        Receta receta = new Receta();
        when(recetaService.getRecetaById(1L)).thenReturn(receta);

        String viewName = homeController.detalleReceta(1L, model);
        assertEquals("detalle-receta", viewName);
        assertEquals(receta, model.getAttribute("receta"));
    }

    @Test
    void testLogout() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        Cookie jwtCookie = new Cookie("jwt", "jwtToken");

        String viewName = homeController.logout(response, session, jwtCookie);
        assertEquals("redirect:/home", viewName);
    }

    @Test
    void testAccessDenied() {
        String viewName = homeController.accessDenied();
        assertEquals("access-denied", viewName);
    }

    @Test
    void testAdminview() {
        String viewName = homeController.adminview();
        assertEquals("admin", viewName);
    }

    @Test
    void testListarUsuarios() {
        List<Usuario> usuarios = List.of(new Usuario(), new Usuario());
        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(usuarios);

        String viewName = homeController.listarUsuarios(model);
        assertEquals("admin-clientes", viewName);
        assertEquals(usuarios, model.getAttribute("usuarios"));
    }

    @Test
    void testMostrarFormularioEdicion() {
        Usuario usuario = new Usuario();
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        String viewName = homeController.mostrarFormularioEdicion(1L, model);
        assertEquals("editar-cliente", viewName);
        assertEquals(usuario, model.getAttribute("usuario"));
    }

    @Test
    void testActualizarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setUsername("username");
        usuario.setEmail("email@example.com");
        usuario.setNombre("Nombre");
        usuario.setApellido("Apellido");
        usuario.setRol(Role.ROLE_USER);

        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        String viewName = homeController.actualizarUsuario(1L, usuario, model);
        assertEquals("redirect:/admin-clientes", viewName);
        verify(usuarioService, times(1)).actualizarUsuario(1L, usuario);
    }

    @Test
    void testEliminarUsuario() {
        String viewName = homeController.eliminarUsuario(1L);
        assertEquals("redirect:/admin-clientes", viewName);
        verify(usuarioService, times(1)).eliminarUsuario(1L);
    }

    @Test
    void testRegistrarUsuario_RedirectToLogin() {
        when(usuarioService.existePorUsername("username")).thenReturn(false);
        when(usuarioService.existePorEmail("test@example.com")).thenReturn(false);

        String viewName = homeController.registrarUsuario("username", "test@example.com", "Nombre", "Apellido", "password", model);

        assertEquals("redirect:/login", viewName);
    }

    @Test
    void testActualizarUsuario_RedirectToAdminClientes() {
        Usuario usuario = new Usuario();
        usuario.setUsername("username");
        usuario.setEmail("email@example.com");
        usuario.setNombre("Nombre");
        usuario.setApellido("Apellido");
        usuario.setRol(Role.ROLE_USER);

        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        String viewName = homeController.actualizarUsuario(1L, usuario, model);
        assertEquals("redirect:/admin-clientes", viewName);
    }

    @Test
    void testEliminarUsuario_RedirectToAdminClientes() {
        String viewName = homeController.eliminarUsuario(1L);
        assertEquals("redirect:/admin-clientes", viewName);
    }
}