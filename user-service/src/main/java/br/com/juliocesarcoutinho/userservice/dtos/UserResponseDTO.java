package br.com.juliocesarcoutinho.userservice.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "User Response DTO", description = "Objeto de transferência para resposta de usuário")
public record UserResponseDTO(
  UUID id,
  String fullname,
  String email,
  boolean active,
  LocalDateTime createdAt
) {}
