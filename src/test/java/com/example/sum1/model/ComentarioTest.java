package com.example.sum1.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ComentarioTest {

    @Test
    void testGettersAndSetters() {
        Comentario comentario = new Comentario();
        Long id = 1L;
        String contenido = "Este es un comentario";
        Receta receta = new Receta();

        comentario.setId(id);
        comentario.setContenido(contenido);
        comentario.setReceta(receta);

        assertEquals(id, comentario.getId());
        assertEquals(contenido, comentario.getContenido());
        assertEquals(receta, comentario.getReceta());
    }

    @Test
    void testComentarioConstructor() {
        Comentario comentario = new Comentario();
        assertNotNull(comentario);
    }
}