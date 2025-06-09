package br.com.juliocesarcoutinho.userservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "User Update DTO", description = "Objeto de transferência para atualização de usuário")
public record UserUpdateDTO(
    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    @NotBlank(message = "O nome completo é obrigatório")
    String fullname,
    
    @Schema(description = "E-mail do usuário", example = "joao.silva@email.com")
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    String email,
    
    @Schema(description = "Senha do usuário (opcional na atualização)", example = "novasenha123")
    String password
) {}
