package br.com.juliocesarcoutinho.emailservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "Email Filter DTO", description = "Data Transfer Object for Email Filtering")
public record EmailFilterDTO(
    String emailFrom,
    String emailTo,
    String subject,
    String status,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer page,
    Integer size
) {}