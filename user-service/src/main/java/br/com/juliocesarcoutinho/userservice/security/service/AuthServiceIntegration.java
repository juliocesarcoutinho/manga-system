package br.com.juliocesarcoutinho.userservice.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.juliocesarcoutinho.userservice.dtos.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para comunicação com o futuro auth-service
 * Esta classe demostra como será a integração entre o user-service e o auth-service
 * quando este último for implementado
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceIntegration {

    private final RestTemplate restTemplate;
    private final CircuitBreakerFactory circuitBreakerFactory;
    
    @Value("${app.security.auth-service.url:http://auth-service}")
    private String authServiceUrl;
    
    /**
     * Método que será usado para sincronizar a criação de usuários entre os serviços
     * O auth-service será responsável por gerenciar autenticação e autorização
     * enquanto o user-service gerencia os dados do usuário
     */
    public void syncUserCreation(UserResponseDTO user, String rawPassword) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("authService");
        
        try {
            String url = authServiceUrl + "/auth/sync/user";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // O DTO a ser enviado para o auth-service
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("userId", user.id().toString());
            requestBody.put("email", user.email());
            requestBody.put("password", rawPassword);
            requestBody.put("active", user.active());
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            circuitBreaker.run(() -> {
                ResponseEntity<Void> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        Void.class
                );
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Usuário sincronizado com sucesso no auth-service: {}", user.id());
                } else {
                    log.error("Erro ao sincronizar usuário com auth-service: status {}", response.getStatusCode());
                }
                
                return null;
            }, throwable -> {
                log.error("Erro ao sincronizar usuário com auth-service: {}", throwable.getMessage());
                return null;
            });
        } catch (Exception e) {
            log.error("Erro ao comunicar com o auth-service: {}", e.getMessage());
        }
    }
    
    /**
     * Método para verificar se um token JWT é válido no auth-service
     */
    public boolean validateToken(String token) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("authService");
        
        try {
            String url = authServiceUrl + "/auth/validate";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("token", token);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            return circuitBreaker.run(() -> {
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
                );
                
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return (Boolean) response.getBody().get("valid");
                }
                
                return false;
            }, throwable -> {
                log.error("Erro ao validar token no auth-service: {}", throwable.getMessage());
                return false;
            });
        } catch (Exception e) {
            log.error("Erro ao comunicar com o auth-service: {}", e.getMessage());
            return false;
        }
    }
}
