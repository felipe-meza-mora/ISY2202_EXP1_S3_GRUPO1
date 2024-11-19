package com.example.sum1.config;

import com.example.sum1.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    

    @Bean
    public AuthenticationManager customAuthenticationManager(AuthenticationConfiguration config) throws Exception { // Renombrar aquí
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService); // Usar el CustomUserDetailsService
        authenticationProvider.setPasswordEncoder(applicationPasswordEncoder());
        return authenticationProvider;
    }

    @Bean
    @Primary // Marcar este bean como el principal
    public PasswordEncoder applicationPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
