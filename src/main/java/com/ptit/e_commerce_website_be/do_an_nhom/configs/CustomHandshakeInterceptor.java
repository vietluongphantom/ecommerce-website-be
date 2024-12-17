package com.ptit.e_commerce_website_be.do_an_nhom.configs;

import com.ptit.e_commerce_website_be.do_an_nhom.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
       // final String authHeader = request.getHeaders().getFirst("Auvthorization");
        String authHeader = request.getURI().getQuery();
        if (authHeader != null && authHeader.startsWith("token=")) {
            final String jwt = authHeader.substring(6);
            String userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    if (request instanceof ServletServerHttpRequest) {
                        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(servletRequest));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        }

        return true;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}

    private boolean validateToken(String token) {
        // Validate JWT token logic here
        return true; // Assuming token is valid
    }

    private String extractUsername(String token) {
        // Extract username logic here
        return "user";
    }
}
