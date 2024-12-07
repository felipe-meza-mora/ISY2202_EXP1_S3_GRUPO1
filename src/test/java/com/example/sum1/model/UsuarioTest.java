package com.example.sum1.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
     void testGettersAndSetters() {
        Usuario usuario = new Usuario();
        
        usuario.setId(1L);
        assertEquals(1L, usuario.getId());

        usuario.setUsername("testuser");
        assertEquals("testuser", usuario.getUsername());

        usuario.setPassword("password123");
        assertEquals("password123", usuario.getPassword());

        usuario.setEmail("test@example.com");
        assertEquals("test@example.com", usuario.getEmail());

        usuario.setNombre("Test");
        assertEquals("Test", usuario.getNombre());

        usuario.setApellido("User");
        assertEquals("User", usuario.getApellido());

        usuario.setRol(Role.ROLE_USER);
        assertEquals(Role.ROLE_USER, usuario.getRol());
    }

    // Eliminar pruebas de valores nulos
}