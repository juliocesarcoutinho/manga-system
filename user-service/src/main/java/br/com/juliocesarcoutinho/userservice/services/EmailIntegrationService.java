package br.com.juliocesarcoutinho.userservice.services;

import br.com.juliocesarcoutinho.userservice.configs.RabbitMQConfig;
import br.com.juliocesarcoutinho.userservice.dtos.EmailRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailIntegrationService {

    private final RabbitTemplate rabbitTemplate;
    
    @Value("${spring.profiles.active}")
    private String activeProfile;
    
    public void sendWelcomeEmail(String name, String email) {
        // Apenas envia emails em produção
        if (!"prod".equalsIgnoreCase(activeProfile)) {
            log.info("Email não enviado em ambiente de desenvolvimento para: {}", email);
            return;
        }
        
        try {
            EmailRequestDTO emailRequest = EmailRequestDTO.builder()
                    .to(email)
                    .subject("Bem-vindo ao Manga System!")
                    .template("welcome")
                    .variables(Map.of(
                            "name", name,
                            "appName", "Manga System",
                            "loginUrl", "https://manga-system.com/login",
                            "currentYear", String.valueOf(java.time.Year.now().getValue())
                    ))
                    .build();
            
            log.info("Enviando mensagem de email para fila: {}", email);
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.WELCOME_EMAIL_ROUTING_KEY,
                    emailRequest
            );
            
            log.info("Mensagem de email enviada para fila com sucesso: {}", email);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem de email para fila: {}. Erro: {}", email, e.getMessage());
        }
    }
}
