package com.example.sum1.security;

import com.example.sum1.service.CustomUserDetailsService;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        logger.info("Authorization Header");
        logger.debug(authorizationHeader);
    
        String username = null;
        String jwt = null;
    
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                logger.info("Username");
                logger.debug(username);
                
            } catch (ExpiredJwtException e) {
                logger.warn("Token expirado", e); // Registra la advertencia con la excepción
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado.");
                return;
            } catch (Exception e) {
                logger.error("Error al extraer el username del token", e); // Registra el error con la excepción
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
                logger.info("Username: {}");
                logger.debug(username);
            } else {
                logger.info("Username: {}");
                logger.warn( username);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no válido.");
                return;
            }
        } else {
            logger.warn("No se encontró el username en el token o ya hay autenticación.");
        }
    
        chain.doFilter(request, response);
    }
    

}
