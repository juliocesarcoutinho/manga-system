package br.com.juliocesarcoutinho.userservice.security.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocesarcoutinho.userservice.security.dto.AuthRequest;
import br.com.juliocesarcoutinho.userservice.security.dto.TokenValidationRequest;
import br.com.juliocesarcoutinho.userservice.security.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador para autenticação local.
 * Esta classe será removida quando o auth-service estiver implementado.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "API para autenticação (temporária até implementação do auth-service)")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    
    @PostMapping("/login")
    @Operation(summary = "Realiza login e retorna um token JWT")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest request) {
        log.info("Tentativa de login para usuário: {}", request.getUsername());
        
        // Cria o token de autenticação com as credenciais do usuário
        UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        
        // Autentica o usuário
        Authentication authentication = authenticationManager.authenticate(authToken);
        
        // Gera o token JWT
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = tokenService.generateToken(userDetails);
        
        log.info("Login realizado com sucesso para usuário: {}", request.getUsername());
        
        // Prepara a resposta
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("username", userDetails.getUsername());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/validate")
    @Operation(summary = "Valida um token JWT")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody TokenValidationRequest request) {
        boolean valid = tokenService.validateToken(request.getToken());
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);
        
        if (valid) {
            String username = tokenService.extractUsername(request.getToken());
            response.put("username", username);
            log.info("Token validado com sucesso para usuário: {}", username);
        } else {
            log.warn("Tentativa de validar token inválido");
        }
        
        return ResponseEntity.ok(response);
    }
}
