package com.example.sum1.service;

import com.example.sum1.model.Receta;
import com.example.sum1.repository.RecetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import com.example.sum1.exception.RecetaNotFoundException;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;




class RecetaServiceImplTest {

    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private RecetaServiceImpl recetaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRecetas() {
        recetaService.getAllRecetas();
        verify(recetaRepository, times(1)).findAll();
    }

    @Test
    void testSaveReceta() {
        Receta receta = new Receta();
        when(recetaRepository.save(receta)).thenReturn(receta);
        Receta savedReceta = recetaService.saveReceta(receta);
        assertEquals(receta, savedReceta);
        verify(recetaRepository, times(1)).save(receta);
    }

    @Test
    void testGetRecetaById() {
        Receta receta = new Receta();
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        Receta foundReceta = recetaService.getRecetaById(1L);
        assertEquals(receta, foundReceta);
    }

    @Test
    void testGetRecetaByIdNotFound() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RecetaNotFoundException.class, () -> recetaService.getRecetaById(1L));
    }

    @Test
    void testUpdateReceta() {
        Receta existingReceta = new Receta();
        Receta updatedReceta = new Receta();
        updatedReceta.setTitulo("Updated Title");

        when(recetaRepository.findById(1L)).thenReturn(Optional.of(existingReceta));
        when(recetaRepository.save(existingReceta)).thenReturn(existingReceta);

        Receta result = recetaService.updateReceta(1L, updatedReceta);
        assertEquals("Updated Title", result.getTitulo());
        verify(recetaRepository, times(1)).findById(1L);
        verify(recetaRepository, times(1)).save(existingReceta);
    }

    @Test
    void testDeleteReceta() {
        Receta receta = new Receta();
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        doNothing().when(recetaRepository).delete(receta);
        recetaService.deleteReceta(1L);
        verify(recetaRepository, times(1)).findById(1L);
        verify(recetaRepository, times(1)).delete(receta);
    }
}