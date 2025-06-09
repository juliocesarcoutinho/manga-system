package br.com.juliocesarcoutinho.userservice.configs.swagger;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

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
                        .title("API de Gerenciamento de Usuários - Manga System")
                        .version("v1")
                        .description("API RESTful para gerenciamento de usuários do sistema Manga System")
                        .termsOfService("https://www.toponesystem.com.br/termos-de-uso")
                        .license(new License()
                                .name("Topone System License")
                                .url("https://www.toponesystem.com.br/licenca"))
                        .contact(contact)
                )
                .addServersItem(new Server()
                        .url("/")
                        .description("Via API Gateway"))
                .addServersItem(new Server()
                        .url("")
                        .description("Acesso direto"));
    }
}
