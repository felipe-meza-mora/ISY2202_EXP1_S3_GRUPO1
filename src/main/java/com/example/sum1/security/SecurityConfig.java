package com.example.sum1.security;

import com.example.sum1.security.JwtRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configurando SecurityFilterChain");
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (accesibles sin autenticación)
                .requestMatchers("/", "/home", "/login", "/css/**", "/js/**", "/img/**").permitAll()

                // Rutas de autenticación y registro de usuarios
                .requestMatchers("/api/auth/login", "/api/usuarios/register").permitAll()

                // Rutas específicas para usuarios y administradores
                .requestMatchers(HttpMethod.GET, "/api/usuarios/{username}").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/{id}").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/{id}").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")

                // Rutas para recetas
                .requestMatchers(HttpMethod.GET, "/api/recetas/**").permitAll() 
                .requestMatchers(HttpMethod.POST, "/api/recetas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/recetas/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/recetas/{id}").hasRole("ADMIN")

                // Cualquier otra solicitud necesita autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}