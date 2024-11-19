package com.example.sum1.service;

import com.example.sum1.model.Receta;
import com.example.sum1.repository.RecetaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class RecetaServiceTest {

    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private RecetaServiceImpl recetaServiceImpl;

    private Receta receta;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        receta = new Receta();
        receta.setId(1L);
        receta.setTitulo("Test Receta");
        // Asegúrate de que el método setTitulo esté correctamente configurado
    }

    @Test
    public void testGetAllRecetas() {
        List<Receta> recetas = Arrays.asList(receta);
        when(recetaRepository.findAll()).thenReturn(recetas);

        List<Receta> result = recetaServiceImpl.getAllRecetas();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recetaRepository, times(1)).findAll();
    }

    @Test
    public void testSaveReceta() {
        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);

        Receta result = recetaServiceImpl.saveReceta(receta);
        assertNotNull(result);
        assertEquals(receta.getId(), result.getId());
        verify(recetaRepository, times(1)).save(any(Receta.class));
    }

    @Test
    public void testGetRecetaById() {
        when(recetaRepository.findById(anyLong())).thenReturn(java.util.Optional.of(receta));

        Receta result = recetaServiceImpl.getRecetaById(1L);
        assertNotNull(result);
        assertEquals(receta.getId(), result.getId());
        verify(recetaRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testUpdateReceta() {
        when(recetaRepository.findById(anyLong())).thenReturn(java.util.Optional.of(receta));
        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);

        Receta result = recetaServiceImpl.updateReceta(1L, receta);
        assertNotNull(result);
        assertEquals(receta.getId(), result.getId());
        verify(recetaRepository, times(1)).findById(anyLong());
        verify(recetaRepository, times(1)).save(any(Receta.class));
    }

    @Test
    public void testDeleteReceta() {
        when(recetaRepository.findById(anyLong())).thenReturn(java.util.Optional.of(receta));
        doNothing().when(recetaRepository).delete(any(Receta.class));

        recetaServiceImpl.deleteReceta(1L);
        verify(recetaRepository, times(1)).findById(anyLong());
        verify(recetaRepository, times(1)).delete(any(Receta.class));
    }
}