package com.example.sum1.controller;

import com.example.sum1.model.Receta;
import com.example.sum1.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;


@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;
    
    // Obtener todas las recetas - Disponible para todos
    @GetMapping
    public ResponseEntity<List<Receta>> getAllRecetas() {
        List<Receta> recetas = recetaService.getAllRecetas();
        return ResponseEntity.ok(recetas);
    }

    // Obtener una receta por su ID - Disponible para todos
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecetaById(@PathVariable("id") Long id) {
        try {
            Receta receta = recetaService.getRecetaById(id);
            return ResponseEntity.ok(receta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receta no encontrada con el ID: " + id);
        }
    }

    //Crear una nueva receta - Solo para administradores
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Receta> saveReceta(@Valid @RequestBody Receta receta) {
        Receta recetaGuardada = recetaService.saveReceta(receta);
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaGuardada);
    }

    //Actualizar una receta - Solo para administradores
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/recetas/{id}")
    public ResponseEntity<?> updateReceta(@PathVariable("id") Long id, @Valid @RequestBody Receta receta) {
        try {
            Receta recetaActualizada = recetaService.updateReceta(id, receta);
            return ResponseEntity.ok(recetaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receta no encontrada con el ID: " + id);
        }
    }

    //Eliminar una receta - Solo para administradores
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/recetas/{id}")
    public ResponseEntity<?> deleteReceta(@PathVariable("id") Long id) {
        try {
            recetaService.deleteReceta(id);
            return ResponseEntity.ok("Receta eliminada con éxito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receta no encontrada con el ID: " + id);
        }
    }

    
}
