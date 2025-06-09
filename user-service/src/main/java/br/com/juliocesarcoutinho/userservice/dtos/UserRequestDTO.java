package br.com.juliocesarcoutinho.userservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "User Request DTO", description = "Objeto de transferência para criação de usuário")
public record UserRequestDTO(
    @Schema(description = "Nome completo do usuário", example = "Maria Silva")
    @NotBlank(message = "O nome completo é obrigatório")
    String fullname,
    
    @Schema(description = "E-mail do usuário", example = "maria.silva@email.com")
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    String email,
    
    @Schema(description = "Senha do usuário", example = "senha123")
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    String password
) {}
