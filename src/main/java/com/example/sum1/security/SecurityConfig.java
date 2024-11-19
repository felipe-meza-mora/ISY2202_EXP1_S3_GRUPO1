package com.example.sum1.security;

import com.example.sum1.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

    private static final String ROLE_ADMIN = "ADMIN"; // Definir constante
    private static final String ROLE_USER = "USER"; // Definir constante

    private final JwtRequestFilter jwtRequestFilter;
    private final CustomUserDetailsService customUserDetailsService;


    public SecurityConfig(JwtRequestFilter jwtRequestFilter, CustomUserDetailsService customUserDetailsService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (accesibles sin autenticación)
                .requestMatchers("/", "/home", "/login", "/logout","/register", "/css/**", "/js/**", "/img/**", "/bootstrap/**").permitAll()
                // Rutas específicas para usuarios y administradores
                .requestMatchers(HttpMethod.GET, "/api/usuarios/{username}").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/{id}").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/{id}").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole(ROLE_ADMIN)
                // Rutas para recetas
                .requestMatchers(HttpMethod.GET, "/api/recetas/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/recetas").hasRole(ROLE_ADMIN)
                .requestMatchers(HttpMethod.PUT, "/api/recetas/{id}").hasRole(ROLE_ADMIN)
                .requestMatchers(HttpMethod.DELETE, "/api/recetas/{id}").hasRole(ROLE_ADMIN)
                // Rutas para vistas de recetas, requieren autenticación
                .requestMatchers("/recetas/**").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                // Cualquier otra solicitud necesita autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exceptionHandling ->
                exceptionHandling.accessDeniedPage("/access-denied")
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder securityPasswordEncoder() { // Renombrado
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager securityAuthenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception { // Renombrado
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider securityAuthenticationProvider() { // Renombrado
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(securityPasswordEncoder());
        return authenticationProvider;
    }
}
