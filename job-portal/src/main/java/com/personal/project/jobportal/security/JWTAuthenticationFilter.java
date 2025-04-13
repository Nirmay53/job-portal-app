package com.personal.project.jobportal.security;

import java.io.IOException;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.personal.project.jobportal.client.AuthClient;
import com.personal.project.jobportal.model.UserInfo;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    
    private AuthClient authClient;

    public JWTAuthenticationFilter(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        System.out.println("Filter hit!");
        
        String token = extractToken(request);
        if (token != null && authClient.isValidToken(token)) {
            UserInfo userInfo = authClient.getUserInfo(token);
            String username = userInfo.getUserName();
            System.out.println(userInfo.getRoles());
            User userDetails = new User(username, "", userInfo.getRoles().stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList()));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            System.out.println("No valid token, authentication context not set... Should trigger 401");
        }

        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
    }
}
