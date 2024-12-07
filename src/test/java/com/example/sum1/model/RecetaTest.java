package com.example.sum1.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;




class RecetaTest {

    @Test
    void testRecetaConstructorAndGetters() {
        Receta receta = new Receta("Titulo", "Descripcion", "http://imagen.url", "30 minutos", "Paso 1, Paso 2");

        assertEquals("Titulo", receta.getTitulo());
        assertEquals("Descripcion", receta.getDescripcion());
        assertEquals("http://imagen.url", receta.getImagenUrl());
        assertEquals("30 minutos", receta.getTiempoPreparacion());
        assertEquals("Paso 1, Paso 2", receta.getPasos());
    }

    @Test
    void testSetters() {
        Receta receta = new Receta();
        receta.setTitulo("Nuevo Titulo");
        receta.setDescripcion("Nueva Descripcion");
        receta.setImagenUrl("http://nueva.imagen.url");
        receta.setTiempoPreparacion("45 minutos");
        receta.setPasos("Nuevo Paso 1, Nuevo Paso 2");

        assertEquals("Nuevo Titulo", receta.getTitulo());
        assertEquals("Nueva Descripcion", receta.getDescripcion());
        assertEquals("http://nueva.imagen.url", receta.getImagenUrl());
        assertEquals("45 minutos", receta.getTiempoPreparacion());
        assertEquals("Nuevo Paso 1, Nuevo Paso 2", receta.getPasos());
    }

    @Test
    void testComentarios() {
        Receta receta = new Receta();
        List<Comentario> comentarios = List.of(new Comentario(), new Comentario());
        receta.setComentarios(comentarios);

        assertEquals(2, receta.getComentarios().size());
    }

    @Test
    void testValoraciones() {
        Receta receta = new Receta();
        List<Valoracion> valoraciones = List.of(new Valoracion(), new Valoracion());
        receta.setValoraciones(valoraciones);

        assertEquals(2, receta.getValoraciones().size());
    }
}