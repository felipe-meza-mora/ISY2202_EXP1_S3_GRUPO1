package com.example.sum1.controller;

import com.example.sum1.model.Usuario;
import com.example.sum1.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado"; // Definir constante
    private static final String ROLE_ADMIN = "ROLE_ADMIN"; // Nueva constante
    private static final String ROLE_USER = "ROLE_USER"; // Nueva constante

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Obtener todos los usuarios (GET) - Solo ROLE_ADMIN puede acceder
    @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // Obtener el propio usuario o buscar cualquier usuario solo ADMIN puede buscar cualquier usuario 
    @PreAuthorize("hasRole('" + ROLE_USER + "') or hasRole('" + ROLE_ADMIN + "')")
    @GetMapping("/{username}")
    public ResponseEntity<String> buscarUsuarioPorUsername(@PathVariable String username, Authentication authentication) {
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(username);

        if (usuario.isPresent()) {
            if (authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals(ROLE_ADMIN)) &&
                    !authentication.getName().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permiso para acceder a esta información.");
            }
            return ResponseEntity.ok(usuario.get().toString());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NO_ENCONTRADO); // Usar constante
        }
    }

    // Actualizar el propio usuario o cualquier usuario - ROLE_USER puede actualizar
    // su propio usuario, ROLE_ADMIN puede actualizar cualquier usuario
    @PreAuthorize("hasRole('" + ROLE_USER + "') or hasRole('" + ROLE_ADMIN + "')")
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuarioActualizado,
            Authentication authentication) {
        Optional<Usuario> usuarioExistente = usuarioService.buscarPorUsername(authentication.getName());
        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NO_ENCONTRADO); // Usar constante
        }
        if (authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals(ROLE_ADMIN)) &&
                !usuarioExistente.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para modificar este usuario.");
        }

        Usuario usuario = usuarioService.actualizarUsuario(id, usuarioActualizado);
        return ResponseEntity.ok(usuario.toString());
    }

    // Eliminar el propio usuario o cualquier usuario - ROLE_USER puede eliminar su
    // propio usuario, ROLE_ADMIN puede eliminar cualquier usuario
    @PreAuthorize("hasRole('" + ROLE_USER + "') or hasRole('" + ROLE_ADMIN + "')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id, Authentication authentication) {
        Optional<Usuario> usuarioExistente = usuarioService.buscarPorUsername(authentication.getName());
        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NO_ENCONTRADO); // Usar constante
        }
        if (authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals(ROLE_ADMIN)) &&
                !usuarioExistente.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar este usuario.");
        }

        usuarioService.eliminarUsuario(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
