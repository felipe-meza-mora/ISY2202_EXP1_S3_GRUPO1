package com.example.sum1.controller;

import com.example.sum1.model.Receta;
import com.example.sum1.service.RecetaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import com.example.sum1.model.Comentario;
import com.example.sum1.model.Valoracion;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    private static final String RECETA_NO_ENCONTRADA = "Receta no encontrada con el ID: "; 
    private static final String ROLE_ADMIN = "ROLE_ADMIN"; 


    private final RecetaService recetaService;


    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }
    

    @GetMapping
    public ResponseEntity<List<Receta>> getAllRecetas() {
        List<Receta> recetas = recetaService.getAllRecetas();
        return ResponseEntity.ok(recetas);
    }

 
    @GetMapping("/{id}")
    public ResponseEntity<String> getRecetaById(@PathVariable("id") Long id) {
        try {
            Receta receta = recetaService.getRecetaById(id);
            return ResponseEntity.ok(receta.toString());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RECETA_NO_ENCONTRADA + id); // Usar constante
        }
    }

  
    @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    @PostMapping
    public ResponseEntity<Receta> saveReceta(@Valid @RequestBody Receta receta) {
        Receta recetaGuardada = recetaService.saveReceta(receta);
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaGuardada);
    }

 
    @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    @PutMapping("/recetas/{id}")
    public ResponseEntity<String> updateReceta(@PathVariable("id") Long id, @Valid @RequestBody Receta receta) {
        try {
            Receta recetaActualizada = recetaService.updateReceta(id, receta);
            return ResponseEntity.ok(recetaActualizada.toString());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RECETA_NO_ENCONTRADA + id); // Usar constante
        }
    }

   
    @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    @DeleteMapping("/recetas/{id}")
    public ResponseEntity<String> deleteReceta(@PathVariable("id") Long id) {
        try {
            recetaService.deleteReceta(id);
            return ResponseEntity.ok("Receta eliminada con éxito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RECETA_NO_ENCONTRADA + id); // Usar constante
        }
    }

    @PostMapping("/comentarios")
    public ResponseEntity<Comentario> saveComentario(@Valid @RequestBody Comentario comentario) {
        Receta receta = recetaService.getRecetaById(comentario.getReceta().getId());
        comentario.setReceta(receta);
        receta.getComentarios().add(comentario);
        recetaService.saveReceta(receta);
        return ResponseEntity.status(HttpStatus.CREATED).body(comentario);
    }

    @PostMapping("/valoraciones")
    public ResponseEntity<Valoracion> saveValoracion(@Valid @RequestBody Valoracion valoracion) {
        Receta receta = recetaService.getRecetaById(valoracion.getReceta().getId());
        valoracion.setReceta(receta);
        receta.getValoraciones().add(valoracion);
        recetaService.saveReceta(receta);
        return ResponseEntity.status(HttpStatus.CREATED).body(valoracion);
    }
}
