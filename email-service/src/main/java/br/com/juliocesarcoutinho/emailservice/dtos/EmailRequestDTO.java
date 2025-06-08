package br.com.juliocesarcoutinho.emailservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "Email Request DTO", description = "Data Transfer Object for Email Request")
public record EmailRequestDTO(
    @NotBlank @Email String emailFrom,
    @NotBlank @Email String emailTo,
    @Email String emailCc,
    @Email String emailBcc,
    @NotBlank String subject,
    @NotBlank String text
) {}