package br.com.juliocesarcoutinho.userservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(name = "Role Request DTO", description = "Objeto de transferência para criação/atualização de papel")
public record RoleRequestDTO(
    @Schema(description = "Nome da role (autoridade)", example = "ROLE_ADMIN")
    @NotBlank(message = "O nome da role é obrigatório")
    @Pattern(regexp = "^ROLE_[A-Z]+$", message = "O nome da role deve seguir o padrão ROLE_NOME, com letras maiúsculas")
    String authority
) {}