package com.example.sum1.controller;

import com.example.sum1.model.Usuario;
import com.example.sum1.security.JwtUtil;
import com.example.sum1.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.sum1.model.Receta;
import com.example.sum1.model.Role;
import com.example.sum1.service.RecetaService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private static final String REDIRECT_HOME = "redirect:/home"; // Definir constante
    private static final String ERROR_MESSAGE = "error"; // Definir constante
    private static final String REGISTER_PAGE = "register"; // Definir constante
    private static final String USERNAME_IN_USE_ERROR = "El nombre de usuario ya está en uso."; // Definir constante
    private static final String EMAIL_IN_USE_ERROR = "El correo electrónico ya está en uso."; // Definir constante
    private static final String RECETA_NO_ENCONTRADA = "Receta no encontrada. Intenta con otro ID."; // Definir constante
    private static final String USERNAME_REQUIRED_ERROR = "El nombre de usuario es obligatorio."; // Nueva constante
    private static final String ERROR_USERNAME = "errorUsername"; // Nueva constante

    private final RecetaService recetaService;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public HomeController(RecetaService recetaService, UsuarioService usuarioService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.recetaService = recetaService;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Redirigir a la página de inicio
    @GetMapping("/")
    public String redirectToHome() {
        return REDIRECT_HOME; // Usar constante
    }

    // Mostrar la página principal con todas las recetas
    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal Principal principal) {
        List<Receta> recetas = recetaService.getAllRecetas();
        
        model.addAttribute("recetas", recetas);
        model.addAttribute("isAuthenticated", principal != null); // Verificar si el usuario está autenticado

        return "home";  // Cargar la plantilla Thymeleaf "home.html"
    }

    @GetMapping("/login")
    public String login() {
        return "login";  // Cargar la plantilla Thymeleaf "login.html"
    }

    @PostMapping("/login")
    public ModelAndView loginPost(@RequestParam("username") String username, 
                                  @RequestParam("password") String password, 
                                  HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("login");
        Optional<Usuario> optionalUsuario = usuarioService.buscarPorUsername(username);
    
        if (optionalUsuario.isEmpty()) {
            modelAndView.addObject(ERROR_USERNAME, "Usuario no encontrado"); // Usar constante
            return modelAndView;
        }
    
        Usuario usuarioDB = optionalUsuario.get();
    
        if (!passwordEncoder.matches(password, usuarioDB.getPassword())) {
            modelAndView.addObject("errorPassword", "Contraseña incorrecta");
            return modelAndView;
        }
    
        // Generar el token JWT
        String jwt = jwtUtil.generateToken(usuarioDB);
    
        // Configurar la cookie
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(3600)
                .build();
    
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    
        // Redirigir al usuario a la página principal
        modelAndView.setViewName(REDIRECT_HOME); // Usar constante
        return modelAndView;
    }
    
    // Cargar la página de registro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return REGISTER_PAGE; // Usar constante
    }

    // Manejar el registro de usuario
    @PostMapping("/register")
    public String registrarUsuario(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("password") String password,
            Model model) {

        // Validar si el usuario o el correo ya existen
        if (username == null || username.isEmpty()) {
            model.addAttribute(ERROR_USERNAME, USERNAME_REQUIRED_ERROR); // Usar constante
            return REGISTER_PAGE; // Usar constante
        }

        if (email == null || email.isEmpty()) {
            model.addAttribute("errorEmail", "El correo electrónico es obligatorio.");
            return REGISTER_PAGE; // Usar constante
        }

        if (nombre == null || nombre.isEmpty()) {
            model.addAttribute("errorNombre", "El nombre es obligatorio.");
            return REGISTER_PAGE; // Usar constante
        }

        if (apellido == null || apellido.isEmpty()) {
            model.addAttribute("errorApellido", "El apellido es obligatorio.");
            return REGISTER_PAGE; // Usar constante
        }

        if (password == null || password.isEmpty()) {
            model.addAttribute("errorPassword", "La contraseña es obligatoria.");
            return REGISTER_PAGE; // Usar constante
        }

        if (Boolean.TRUE.equals(usuarioService.existePorUsername(username))) { // Usar expresión booleana primitiva
            model.addAttribute(ERROR_USERNAME, USERNAME_IN_USE_ERROR); // Usar constante
            return REGISTER_PAGE; // Usar constante
        }

        if (Boolean.TRUE.equals(usuarioService.existePorEmail(email))) { // Usar expresión booleana primitiva
            model.addAttribute("errorEmail", EMAIL_IN_USE_ERROR); // Usar constante
            return REGISTER_PAGE; // Usar constante
        }

        // Crear y registrar al nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setPassword(password); // No encriptar aquí
        nuevoUsuario.setRol(Role.ROLE_USER); // Establecer el rol predeterminado a ROLE_USER

        usuarioService.registrarUsuario(nuevoUsuario);

        // Redirigir al home después del registro exitoso
        return "redirect:/login";
    }
    
    // Mostrar el detalle de una receta por ID
    @GetMapping("/recetas/{id}")
    public String detalleReceta(@PathVariable("id") Long id, Model model) {
        try {
            Receta receta = recetaService.getRecetaById(id);
            model.addAttribute("receta", receta);
            return "detalle-receta";  // Cargar la plantilla "detalle-receta.html"
        } catch (RuntimeException e) {
            // Mensaje genérico para el usuario
            model.addAttribute(ERROR_MESSAGE, RECETA_NO_ENCONTRADA); // Usar constante
            return ERROR_MESSAGE;  // Cargar una plantilla de error genérica
        }
    }

    // Página de acceso denegado
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";  // Cargar la plantilla "access-denied.html"
    }

    // Cerrar sesión y eliminar el JWT de las cookies
    @GetMapping("/logout")
    public String logout(
            HttpServletResponse response,
            HttpSession session,
            @CookieValue(value = "jwt", required = false) Cookie jwtCookie) {
        // Eliminar la cookie JWT si existe
        if (jwtCookie != null) {
            jwtCookie.setMaxAge(0); 
            jwtCookie.setPath("/"); 
            response.addCookie(jwtCookie); 
        }

        // Invalidar la sesión actual
        if (session != null) {
            session.invalidate();
        }

        // Eliminar la cookie JSESSIONID en el cliente
        Cookie jsessionCookie = new Cookie("JSESSIONID", null);
        jsessionCookie.setMaxAge(0); 
        jsessionCookie.setPath("/"); 

        // Establecer las banderas de seguridad
        jsessionCookie.setSecure(true); 
        jsessionCookie.setHttpOnly(true); 
        response.addCookie(jsessionCookie);

        // Redirigir a la página de inicio después de hacer logout
        return REDIRECT_HOME; // Usar constante
    }

}
