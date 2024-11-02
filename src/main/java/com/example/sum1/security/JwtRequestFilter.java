package com.example.sum1.security;

import com.example.sum1.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Obtener el token desde la cookie
        String jwt = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) { 
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        logger.info("Token JWT desde cookie: {}", jwt);

        String username = null;

        if (jwt != null) {
            try {
                username = jwtUtil.extractUsername(jwt);
                logger.info("Username: {}", username);
            } catch (ExpiredJwtException e) {
                logger.warn("Token expirado", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado.");
                return;
            } catch (Exception e) {
                logger.error("Error al extraer el username del token", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error en el token.");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Usuario autenticado: {}", username);
            } else {
                logger.warn("Token no válido para el usuario: {}", username);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no válido.");
                return;
            }
        } else {
            logger.warn("No se encontró el username en el token o ya hay autenticación.");
        }

        chain.doFilter(request, response);
    }
}