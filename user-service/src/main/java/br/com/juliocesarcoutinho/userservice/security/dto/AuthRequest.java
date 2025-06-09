package br.com.juliocesarcoutinho.userservice.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String username;
    
    @NotBlank(message = "A senha é obrigatória")
    private String password;
}
