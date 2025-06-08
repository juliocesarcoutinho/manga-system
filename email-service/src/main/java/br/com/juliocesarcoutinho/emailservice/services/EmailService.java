package br.com.juliocesarcoutinho.emailservice.services;
import br.com.juliocesarcoutinho.emailservice.dtos.EmailRequestDTO;
import br.com.juliocesarcoutinho.emailservice.dtos.EmailResponseDTO;
import br.com.juliocesarcoutinho.emailservice.entities.Email;
import br.com.juliocesarcoutinho.emailservice.enums.StatusEmail;
import br.com.juliocesarcoutinho.emailservice.repositories.EmailRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final EmailRepository emailRepository;
    private final JavaMailSender emailSender;

    @Transactional
    public EmailResponseDTO sendEmail(EmailRequestDTO emailRequest) {
        Email email = new Email();
        email.setEmailFrom(emailRequest.emailFrom());
        email.setEmailTo(emailRequest.emailTo());
        email.setEmailCc(emailRequest.emailCc());
        email.setEmailBcc(emailRequest.emailBcc());
        email.setSubject(emailRequest.subject());
        email.setText(emailRequest.text());
        email.setSendDate(LocalDateTime.now());

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(email.getEmailFrom());
            helper.setTo(email.getEmailTo());

            if (email.getEmailCc() != null && !email.getEmailCc().isEmpty()) {
                helper.setCc(email.getEmailCc());
            }

            if (email.getEmailBcc() != null && !email.getEmailBcc().isEmpty()) {
                helper.setBcc(email.getEmailBcc());
            }

            helper.setSubject(email.getSubject());
            helper.setText(email.getText(), true); // true indica que o conteúdo é HTML

            emailSender.send(message);

            email.setStatus(StatusEmail.SENT);
            log.info("Email enviado com sucesso para: {}", email.getEmailTo());
        } catch (Exception e) {
            email.setStatus(StatusEmail.ERROR);
            email.setErrorMessage(e.getMessage());
            log.error("Erro ao enviar email para: {}", email.getEmailTo(), e);
        }

        Email savedEmail = emailRepository.save(email);

        return new EmailResponseDTO(
                savedEmail.getId(),
                savedEmail.getEmailFrom(),
                savedEmail.getEmailTo(),
                savedEmail.getSubject(),
                savedEmail.getStatus(),
                savedEmail.getSendDate(),
                savedEmail.getErrorMessage()
        );
    }

    public Page<EmailResponseDTO> getAllEmails(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return emailRepository.findAll(pageable)
                .map(email -> new EmailResponseDTO(
                        email.getId(),
                        email.getEmailFrom(),
                        email.getEmailTo(),
                        email.getSubject(),
                        email.getStatus(),
                        email.getSendDate(),
                        email.getErrorMessage()
                ));
    }

    public Page<EmailResponseDTO> getEmailsByStatus(StatusEmail status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return emailRepository.findByStatus(status, pageable)
                .map(email -> new EmailResponseDTO(
                        email.getId(),
                        email.getEmailFrom(),
                        email.getEmailTo(),
                        email.getSubject(),
                        email.getStatus(),
                        email.getSendDate(),
                        email.getErrorMessage()
                ));
    }

    public Page<EmailResponseDTO> findEmailsWithFilters(String emailFrom, String emailTo,
                                                            String subject, String status,
                                                            LocalDateTime startDate, LocalDateTime endDate,
                                                            int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        StatusEmail statusEnum = status != null ? StatusEmail.valueOf(status) : null;

        return emailRepository.findEmailsWithFilters(
                        emailFrom, emailTo, subject, statusEnum, startDate, endDate, pageable)
                .map(email -> new EmailResponseDTO(
                        email.getId(),
                        email.getEmailFrom(),
                        email.getEmailTo(),
                        email.getSubject(),
                        email.getStatus(),
                        email.getSendDate(),
                        email.getErrorMessage()
                ));
    }

    public long countEmailsByStatus(StatusEmail status) {
        return emailRepository.countByStatus(status);
    }
}