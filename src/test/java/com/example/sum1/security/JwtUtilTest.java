package com.example.sum1.security;

import com.example.sum1.model.Usuario;
import com.example.sum1.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;


import static org.junit.jupiter.api.Assertions.*;


class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private Usuario usuario;

    @Mock
    private Role role;

    @Value("${myapp.secret-key}")
    private String secret = "mysecretkeymysecretkeymysecretkeymysecretkey"; // Example key

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    }

    @Test
    void testExtractUsername() {
        // Crear un usuario
        Usuario user = new Usuario();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEmail("example.com");
        user.setNombre("testName");
        user.setApellido("testLastName");
        user.setRol(Role.ROLE_USER);

        // Generar el token y extraer el username
        String token = jwtUtil.generateToken(user);
        String username = jwtUtil.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    void testExtractUserRole() {
        // Crear un usuario con un rol
        Usuario user = new Usuario();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEmail("example.com");
        user.setNombre("testName");
        user.setApellido("testLastName");
        user.setRol(Role.ROLE_USER);

        // Generar el token y extraer el rol
        String token = jwtUtil.generateToken(user);
        String role = jwtUtil.extractUserRole(token);

        assertEquals("ROLE_USER", role);
    }

    @Test
    void testExtractExpiration() {
        // Crear un usuario
        Usuario user = new Usuario();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEmail("example.com");
        user.setNombre("testName");
        user.setApellido("testLastName");
        user.setRol(Role.ROLE_USER);

        // Generar el token y extraer la expiración
        String token = jwtUtil.generateToken(user);
        Date expiration = jwtUtil.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date())); // Verifica que la expiración sea en el futuro
    }

    @Test
    void testGenerateToken() {
        // Crear un usuario
        Usuario user = new Usuario();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEmail("example.com");
        user.setNombre("testName");
        user.setApellido("testLastName");
        user.setRol(Role.ROLE_USER);
    
        // Generar el token
        String token = jwtUtil.generateToken(user);
    
        // Imprimir el token completo en la consola para verificarlo
        System.out.println("Generated token: " + token);
    
        // Decodificar la parte del payload del token
        String[] parts = token.split("\\.");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
        System.out.println("Decoded payload: " + payload);
    
        // Comprobar si el payload contiene el nombre de usuario
        assertNotNull(token); // El token generado no debe ser nulo
        assertTrue(payload.contains("testUser")); // El payload decodificado debe contener el username
    }
    
    
}
