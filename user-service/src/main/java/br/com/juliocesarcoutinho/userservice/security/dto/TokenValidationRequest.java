package br.com.juliocesarcoutinho.userservice.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenValidationRequest {
    
    @NotBlank(message = "O token é obrigatório")
    private String token;
}
