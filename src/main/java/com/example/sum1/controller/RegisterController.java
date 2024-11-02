package com.example.sum1.controller;

import com.example.sum1.model.Role;
import com.example.sum1.model.Usuario;
import com.example.sum1.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/usuarios/register")
public class RegisterController {

    @Autowired
    private UsuarioService usuarioService;

    // Cargar la página de registro
    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";  // Cargar la plantilla Thymeleaf "register.html"
    }

    @PostMapping
    public String registrarUsuario(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {

        // Chequear validaciones previas
        if (usuarioService.existePorUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya está en uso.");
            return "redirect:/register"; // Redirigir al formulario de registro
        }
        if (usuarioService.existePorEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está en uso.");
            return "redirect:/register"; // Redirigir al formulario de registro
        }

        // Crear un nuevo objeto Usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setPassword(password);
        nuevoUsuario.setRol(Role.ROLE_USER); // Asegúrate de que estás usando el enum

        // Si no hay errores, registra el usuario
        usuarioService.registrarUsuario(nuevoUsuario);
        
        // Redirigir al home después de un registro exitoso
        return "redirect:/home";
    }
}
