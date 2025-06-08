package br.com.juliocesarcoutinho.emailservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import br.com.juliocesarcoutinho.emailservice.enums.StatusEmail;

@Schema(name = "Email Response DTO", description = "Data Transfer Object for Email Response")
public record EmailResponseDTO(
    Long id,
    String emailFrom,
    String emailTo,
    String subject,
    StatusEmail status,
    LocalDateTime sendDate,
    String errorMessage
) {}