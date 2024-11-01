package com.example.sum1.controller;

import com.example.sum1.model.Usuario;
import com.example.sum1.security.JwtUtil;
import com.example.sum1.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        Optional<Usuario> optionalUsuario = usuarioService.buscarPorUsername(usuario.getUsername());

        if (optionalUsuario.isEmpty()) {
            logger.error("Usuario no encontrado: {}", usuario.getUsername());
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuarioDB = optionalUsuario.get();

        if (!passwordEncoder.matches(usuario.getPassword(), usuarioDB.getPassword())) {
            logger.warn("Contraseña incorrecta para el usuario: {}", usuario.getUsername());
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        // Generar el token JWT
        String jwt = jwtUtil.generateToken(usuarioDB);

        // Configurar la cookie
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)  // Evita el acceso a la cookie desde JavaScript
                .secure(true)    // Solo se envía a través de HTTPS
                .path("/")       // Disponible en toda la aplicación
                .maxAge(3600)    // Tiempo de vida de la cookie (en segundos)
                .build();

        logger.info("Login exitoso para el usuario: {}", usuario.getUsername());
        logger.info("Rol del usuario {}: {}", usuario.getUsername(), usuarioDB.getRol().name());

        // Retornar una respuesta vacía con la cookie configurada
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Login exitoso");
    }

    // Verificar si el usuario está autenticado
    @GetMapping("/check")
    public ResponseEntity<Void> checkAuthentication(Principal principal) {
        if (principal != null) {
            return ResponseEntity.ok().build(); // Usuario autenticado
        } else {
            return ResponseEntity.status(401).build(); // Usuario no autenticado
        }
    }
}
