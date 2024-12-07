package com.example.sum1.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




class ValoracionTest {

    @Test
    void testGetId() {
        Valoracion valoracion = new Valoracion();
        valoracion.setId(1L);
        assertEquals(1L, valoracion.getId());
    }

    @Test
    void testSetId() {
        Valoracion valoracion = new Valoracion();
        valoracion.setId(1L);
        assertEquals(1L, valoracion.getId());
    }

    @Test
    void testGetPuntuacion() {
        Valoracion valoracion = new Valoracion();
        valoracion.setPuntuacion(5);
        assertEquals(5, valoracion.getPuntuacion());
    }

    @Test
    void testSetPuntuacion() {
        Valoracion valoracion = new Valoracion();
        valoracion.setPuntuacion(5);
        assertEquals(5, valoracion.getPuntuacion());
    }

    @Test
    void testGetReceta() {
        Valoracion valoracion = new Valoracion();
        Receta receta = new Receta();
        valoracion.setReceta(receta);
        assertEquals(receta, valoracion.getReceta());
    }

    @Test
    void testSetReceta() {
        Valoracion valoracion = new Valoracion();
        Receta receta = new Receta();
        valoracion.setReceta(receta);
        assertEquals(receta, valoracion.getReceta());
    }
}