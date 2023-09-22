package com.vertical.jobapp.config.security;

import com.vertical.jobapp.service.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;

import java.io.*;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter  {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("AUTHORIZATION");


        if (authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }

        var jwtToken = authHeader.substring(7);
        var userEmail = jwtUtil.extractUsername(jwtToken);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails details = userService.loadUserByUsername(userEmail);
            if (jwtUtil.validateToken(jwtToken, details)) {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);

            }
            filterChain.doFilter(request,response);
        }

    }
}
