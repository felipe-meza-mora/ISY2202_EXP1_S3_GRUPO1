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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;




public class RecetaControllerTest {

    @Mock
    private RecetaService recetaService;

    @InjectMocks
    private RecetaController recetaController;

    private MockMvc mockMvc;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recetaController).build();
    }

    @Test
    public void testGetAllRecetas() {
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
    public void testGetRecetaById() {
        Receta receta = new Receta();
        receta.setId(1L);

        when(recetaService.getRecetaById(1L)).thenReturn(receta);

        ResponseEntity<String> response = recetaController.getRecetaById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(receta.toString(), response.getBody());
        verify(recetaService, times(1)).getRecetaById(1L);
    }

    @Test
    public void testGetRecetaByIdNotFound() {
        when(recetaService.getRecetaById(1L)).thenThrow(new RuntimeException());

        ResponseEntity<String> response = recetaController.getRecetaById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Receta no encontrada con el ID: 1", response.getBody());
        verify(recetaService, times(1)).getRecetaById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSaveReceta() {
        Receta receta = new Receta();
        when(recetaService.saveReceta(receta)).thenReturn(receta);

        ResponseEntity<Receta> response = recetaController.saveReceta(receta);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(receta, response.getBody());
        verify(recetaService, times(1)).saveReceta(receta);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateReceta() {
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
    public void testUpdateRecetaNotFound() {
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
    public void testDeleteReceta() {
        doNothing().when(recetaService).deleteReceta(1L);

        ResponseEntity<String> response = recetaController.deleteReceta(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Receta eliminada con éxito", response.getBody());
        verify(recetaService, times(1)).deleteReceta(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteRecetaNotFound() {
        doThrow(new RuntimeException()).when(recetaService).deleteReceta(1L);

        ResponseEntity<String> response = recetaController.deleteReceta(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Receta no encontrada con el ID: 1", response.getBody());
        verify(recetaService, times(1)).deleteReceta(1L);
    }
}