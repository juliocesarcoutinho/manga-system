package br.com.juliocesarcoutinho.userservice.security.service;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Serviço responsável por se comunicar com o auth-service para realizar operações de autenticação
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {

    private final RestTemplate restTemplate;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final ObjectMapper objectMapper;
    
    /**
     * URL base do serviço de autenticação, a ser configurada no application.yml
     */
    private String authServiceUrl = "http://auth-service";
    
    /**
     * Valida as credenciais de um usuário no serviço de autenticação
     * 
     * @param username O email do usuário
     * @param password A senha do usuário
     * @return Um token JWT se as credenciais forem válidas, ou null caso contrário
     */
    public String validateCredentials(String username, String password) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("authService");
        
        try {
            String url = authServiceUrl + "/auth/login";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = Map.of(
                    "username", username,
                    "password", password
            );
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            return circuitBreaker.run(() -> {
                Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
                
                if (response != null && response.containsKey("token")) {
                    return (String) response.get("token");
                }
                
                return null;
            }, throwable -> {
                log.error("Erro ao validar credenciais no auth-service: {}", throwable.getMessage());
                return null;
            });
        } catch (Exception e) {
            log.error("Erro ao comunicar com o auth-service: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifica se um token JWT é válido no serviço de autenticação
     * 
     * @param token O token JWT a ser validado
     * @return true se o token for válido, false caso contrário
     */
    public boolean validateToken(String token) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("authService");
        
        try {
            String url = authServiceUrl + "/auth/validate";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = Map.of("token", token);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            return circuitBreaker.run(() -> {
                Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
                
                if (response != null && response.containsKey("valid")) {
                    return (Boolean) response.get("valid");
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
