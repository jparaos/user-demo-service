package cl.jparaos.app.security;

import cl.jparaos.app.config.JwtTokenUtil;
import cl.jparaos.app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil tokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = extractTokenFromHeader(request.getHeader("Authorization"));

        if (token != null && tokenUtil.validateToken(token)) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    tokenUtil.getUsernameFromToken(token), null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            return tokenHeader.substring(7);
        }
        return null;
    }
}
