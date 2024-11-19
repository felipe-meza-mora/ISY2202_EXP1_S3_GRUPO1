package com.example.sum1.service;

import com.example.sum1.model.Usuario;
import com.example.sum1.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        Usuario foundUser = usuario.get();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(foundUser.getRol().name());
        return User
                .withUsername(foundUser.getUsername())
                .password(foundUser.getPassword())
                .authorities(Collections.singletonList(authority))
                .build();
    }
}

