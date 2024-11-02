package com.example.sum1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.sum1.model.Receta;
import com.example.sum1.service.RecetaService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private RecetaService recetaService;

    // Redirigir a la página de inicio
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    // Mostrar la página principal con todas las recetas
    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal Principal principal) {
        List<Receta> recetas = recetaService.getAllRecetas();
        System.out.println("Recetas obtenidas: " + recetas);
        
        model.addAttribute("recetas", recetas);
        model.addAttribute("isAuthenticated", principal != null); 

        return "home"; 
    }

    // Cargar la página de login
    @GetMapping("/login")
    public String login() {
        return "login";  
    }

    // Cargar la página de registro
    @GetMapping("/register")
    public String register() {
        return "register"; 
    }
    
    // Mostrar el detalle de una receta por ID
    @GetMapping("/recetas/{id}")
    public String detalleReceta(@PathVariable("id") Long id, Model model) {
        try {
            Receta receta = recetaService.getRecetaById(id);
            model.addAttribute("receta", receta);
            return "detalle-receta"; 
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "error";  
        }
    }

    // Página de acceso denegado
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; 
    }


    @PostMapping("/logout")
    public String logout(HttpServletResponse response, @CookieValue(value = "jwt", required = false) Cookie jwtCookie) {
        // Eliminar la cookie jwt
        if (jwtCookie != null) {
            jwtCookie.setMaxAge(0); 
            jwtCookie.setPath("/"); 
            response.addCookie(jwtCookie);
        }
        return "Logout successful"; 
    }
}

