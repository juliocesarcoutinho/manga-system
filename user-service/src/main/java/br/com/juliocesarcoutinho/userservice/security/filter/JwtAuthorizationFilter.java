package br.com.juliocesarcoutinho.userservice.security.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.juliocesarcoutinho.userservice.security.service.TokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request, 
                                    @org.springframework.lang.NonNull HttpServletResponse response, 
                                    @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        // Obtém o token do cabeçalho
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        // Se não houver token ou não começar com Bearer, passa para o próximo filtro
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // Extrai o token sem o prefixo Bearer
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            
            // Valida o token e extrai as claims
            if (tokenService.validateToken(token)) {
                Claims claims = tokenService.extractAllClaims(token);
                String username = claims.getSubject();
                
                // Extrai as autoridades do claim "roles"
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) claims.get("roles");
                
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                
                // Cria um objeto de autenticação
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                
                // Define a autenticação no SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Usuário autenticado com sucesso: {}", username);
            }
        } catch (Exception e) {
            log.error("Erro ao validar token JWT: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }
}
