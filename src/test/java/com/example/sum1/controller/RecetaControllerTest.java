package com.example.sum1.controller;

import com.example.sum1.model.Receta;
import com.example.sum1.service.RecetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.example.sum1.model.Comentario;
import com.example.sum1.model.Valoracion;

class RecetaControllerTest {

    @Mock
    private RecetaService recetaService;

    @InjectMocks
    private RecetaController recetaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRecetas() {
        Receta receta1 = new Receta();
        Receta receta2 = new Receta();
        List<Receta> recetas = Arrays.asList(receta1, receta2);

        when(recetaService.getAllRecetas()).thenReturn(recetas);

        ResponseEntity<List<Receta>> response = recetaController.getAllRecetas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(recetaService, times(1)).getAllRecetas();
    }

    @Test
    void testGetRecetaById() {
        Receta receta = new Receta();
        receta.setId(1L);

        when(recetaService.getRecetaById(1L)).thenReturn(receta);

        ResponseEntity<String> response = recetaController.getRecetaById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(receta.toString(), response.getBody());
        verify(recetaService, times(1)).getRecetaById(1L);
    }

    @Test
    void testGetRecetaByIdNotFound() {
        when(recetaService.getRecetaById(1L)).thenThrow(new RuntimeException());

        ResponseEntity<String> response = recetaController.getRecetaById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Receta no encontrada con el ID: 1", response.getBody());
        verify(recetaService, times(1)).getRecetaById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveReceta() {
        Receta receta = new Receta();
        when(recetaService.saveReceta(receta)).thenReturn(receta);

        ResponseEntity<Receta> response = recetaController.saveReceta(receta);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(receta, response.getBody());
        verify(recetaService, times(1)).saveReceta(receta);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateReceta() {
        Receta receta = new Receta();
        receta.setId(1L);

        when(recetaService.updateReceta(1L, receta)).thenReturn(receta);

        ResponseEntity<String> response = recetaController.updateReceta(1L, receta);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(receta.toString(), response.getBody());
        verify(recetaService, times(1)).updateReceta(1L, receta);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateRecetaNotFound() {
        Receta receta = new Receta();
        receta.setId(1L);

        when(recetaService.updateReceta(1L, receta)).thenThrow(new RuntimeException());

        ResponseEntity<String> response = recetaController.updateReceta(1L, receta);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Receta no encontrada con el ID: 1", response.getBody());
        verify(recetaService, times(1)).updateReceta(1L, receta);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteReceta() {
        doNothing().when(recetaService).deleteReceta(1L);

        ResponseEntity<String> response = recetaController.deleteReceta(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Receta eliminada con éxito", response.getBody());
        verify(recetaService, times(1)).deleteReceta(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteRecetaNotFound() {
        doThrow(new RuntimeException()).when(recetaService).deleteReceta(1L);

        ResponseEntity<String> response = recetaController.deleteReceta(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Receta no encontrada con el ID: 1", response.getBody());
        verify(recetaService, times(1)).deleteReceta(1L);
    }

    @Test
    void testSaveComentario() {
        Comentario comentario = new Comentario();
        Receta receta = new Receta();
        receta.setId(1L);
        comentario.setReceta(receta);

        when(recetaService.getRecetaById(1L)).thenReturn(receta);
        when(recetaService.saveReceta(receta)).thenReturn(receta);

        ResponseEntity<Comentario> response = recetaController.saveComentario(comentario);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(comentario, response.getBody());
        verify(recetaService, times(1)).getRecetaById(1L);
        verify(recetaService, times(1)).saveReceta(receta);
    }

    @Test
    void testSaveValoracion() {
        Valoracion valoracion = new Valoracion();
        Receta receta = new Receta();
        receta.setId(1L);
        valoracion.setReceta(receta);

        when(recetaService.getRecetaById(1L)).thenReturn(receta);
        when(recetaService.saveReceta(receta)).thenReturn(receta);

        ResponseEntity<Valoracion> response = recetaController.saveValoracion(valoracion);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(valoracion, response.getBody());
        verify(recetaService, times(1)).getRecetaById(1L);
        verify(recetaService, times(1)).saveReceta(receta);
    }
}