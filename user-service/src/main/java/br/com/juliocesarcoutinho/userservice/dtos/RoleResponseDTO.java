package br.com.juliocesarcoutinho.userservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "Role Response DTO", description = "Objeto de transferência para retorno de papel")
public record RoleResponseDTO(
    @Schema(description = "ID da role", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @Schema(description = "Nome da role (autoridade)", example = "ROLE_ADMIN")
    String authority
) {}
