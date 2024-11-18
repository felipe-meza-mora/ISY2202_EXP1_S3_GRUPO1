package com.example.sum1.controller;

import com.example.sum1.model.Usuario;
import com.example.sum1.security.JwtUtil;
import com.example.sum1.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private RecetaService recetaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Redirigir a la página de inicio
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    // Mostrar la página principal con todas las recetas
    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal Principal principal) {
        List<Receta> recetas = recetaService.getAllRecetas();
        logger.info("Recetas obtenidas: {}", recetas);
        
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
            modelAndView.addObject("errorUsername", "Usuario no encontrado");
            return modelAndView;
        }
    
        Usuario usuarioDB = optionalUsuario.get();
    
        logger.info("Contraseña ingresada: {}", password);
        logger.info("Contraseña en la base de datos: {}", usuarioDB.getPassword());
    
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
        modelAndView.setViewName("redirect:/home");
        return modelAndView;
    }
    
    // Cargar la página de registro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register"; // Cargar la plantilla Thymeleaf "register.html"
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

        logger.info("Registrando usuario con username: {}", username);
        logger.info("Contraseña recibida: {}", password);

        // Validar si el usuario o el correo ya existen
        if (username == null || username.isEmpty()) {
            model.addAttribute("errorUsername", "El nombre de usuario es obligatorio.");
            return "register";
        }

        if (email == null || email.isEmpty()) {
            model.addAttribute("errorEmail", "El correo electrónico es obligatorio.");
            return "register";
        }

        if (nombre == null || nombre.isEmpty()) {
            model.addAttribute("errorNombre", "El nombre es obligatorio.");
            return "register";
        }

        if (apellido == null || apellido.isEmpty()) {
            model.addAttribute("errorApellido", "El apellido es obligatorio.");
            return "register";
        }

        if (password == null || password.isEmpty()) {
            model.addAttribute("errorPassword", "La contraseña es obligatoria.");
            return "register";
        }

        if (usuarioService.existePorUsername(username)) {
            model.addAttribute("errorUsername", "El nombre de usuario ya está en uso.");
            return "register";
        }

        if (usuarioService.existePorEmail(email)) {
            model.addAttribute("errorEmail", "El correo electrónico ya está en uso.");
            return "register";
        }

        // Crear y registrar al nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setPassword(password); // No encriptar aquí
        nuevoUsuario.setRol(Role.ROLE_USER); // Establecer el rol predeterminado a ROLE_USER

        logger.info("Contraseña antes de registrar: {}", nuevoUsuario.getPassword());

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
            // Registrar el error en el log sin exponer detalles al usuario
            logger.error("Error al obtener la receta con ID: " + id, e);

            // Mensaje genérico para el usuario
            model.addAttribute("errorMessage", "Receta no encontrada. Intenta con otro ID.");
            return "error";  // Cargar una plantilla de error genérica
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
            jwtCookie.setMaxAge(0); // Establecer la edad máxima a 0 para eliminar la cookie
            jwtCookie.setPath("/"); // Asegurarse de que se elimine en todo el contexto de la aplicación
            response.addCookie(jwtCookie); // Agregar la cookie modificada a la respuesta
        }

        // Invalidar la sesión actual
        if (session != null) {
            session.invalidate();
        }

        // Eliminar la cookie JSESSIONID en el cliente
        Cookie jsessionCookie = new Cookie("JSESSIONID", null);
        jsessionCookie.setMaxAge(0); // Eliminar la cookie
        jsessionCookie.setPath("/"); // Asegurarte de que se elimine para todo el contexto
        response.addCookie(jsessionCookie);

        // Redirigir a la página de inicio después de hacer logout
        return "redirect:/home";
    }

}
