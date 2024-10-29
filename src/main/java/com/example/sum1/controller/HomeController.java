package com.example.sum1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.sum1.model.Receta;
import com.example.sum1.service.RecetaService;

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
    public String home(Model model) {
        List<Receta> recetas = recetaService.getAllRecetas();
        System.out.println("Recetas obtenidas: " + recetas);
        model.addAttribute("recetas", recetas);
        return "home";  // Cargar la plantilla Thymeleaf "home.html"
    }

    // Cargar la página de login
    @GetMapping("/login")
    public String login() {
        return "login";  // Cargar la plantilla Thymeleaf "login.html"
    }

    // Mostrar el detalle de una receta por ID
    @GetMapping("/recetas/{id}")
    public String detalleReceta(@PathVariable("id") Long id, Model model) {
        try {
            Receta receta = recetaService.getRecetaById(id);
            model.addAttribute("receta", receta);
            return "detalle-receta";  // Cargar la plantilla "detalle-receta.html"
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "error";  // Cargar una plantilla de error en caso de fallo
        }
    }

    // Página de acceso denegado
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";  // Cargar la plantilla "access-denied.html"
    }
}