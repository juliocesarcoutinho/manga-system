package br.com.juliocesarcoutinho.emailservice.configs;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customApi() {
        List<String> emails = List.of(
                "julio@toponesystem.com.br"
        );

        Contact contact = new Contact()
                .name("Equipe de Desenvolvimento Topone System")
                .email("contato@toponesystem.com.br")
                .url("https://www.toponesystem.com.br/contato");
        contact.addExtension("x-emails", emails);

        return new OpenAPI()
                .info(new Info()
                        .title("Api de Envio de Emails - Topone System")
                        .version("v1")
                        .description("Api RestFull para o envio de emails para o microservices" +
                                "Permite o envio de emails com suporte a anexos, cópias e cópias ocultas.")
                        .termsOfService("https://www.toponesystem.com.br/termos-de-uso")
                        .license(new License()
                                .name("Topone System License")
                                .url("https://www.toponesystem.com.br/licenca"))
                        .contact(contact)
                )
                // Adicionando definições de servidor
                .addServersItem(new Server()
                        .url("/email-service")
                        .description("Via API Gateway"))
                .addServersItem(new Server()
                        .url("")
                        .description("Direct access"));
    }
}
