package com.example.sum1.controller;

import com.example.sum1.model.Usuario;
import com.example.sum1.security.JwtUtil;
import com.example.sum1.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("username") String username, 
                                @RequestParam("password") String password, 
                                HttpServletResponse response) {
        Optional<Usuario> optionalUsuario = usuarioService.buscarPorUsername(username);

        if (optionalUsuario.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuarioDB = optionalUsuario.get();

        if (!passwordEncoder.matches(password, usuarioDB.getPassword())) {
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        // Generar el token JWT
        String jwt = jwtUtil.generateToken(usuarioDB);

        // Configurar la cookie
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(3600)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Redirigir al usuario a la página principal
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, "/home")
                .build();
    }

    // Verificar si el usuario está autenticado
    @GetMapping("/check")
    public ResponseEntity<Void> checkAuthentication(Principal principal) {
        if (principal != null) {
            return ResponseEntity.ok().build(); 
        } else {
            return ResponseEntity.status(401).build();
        }
    }
}
