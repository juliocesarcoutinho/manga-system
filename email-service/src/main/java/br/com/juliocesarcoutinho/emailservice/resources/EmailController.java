package br.com.juliocesarcoutinho.emailservice.resources;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocesarcoutinho.emailservice.dtos.EmailRequestDTO;
import br.com.juliocesarcoutinho.emailservice.dtos.EmailResponseDTO;
import br.com.juliocesarcoutinho.emailservice.enums.StatusEmail;
import br.com.juliocesarcoutinho.emailservice.resources.docs.EmailControllerDoc;
import br.com.juliocesarcoutinho.emailservice.services.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController implements EmailControllerDoc {

    private final EmailService emailService;
    
    @PostMapping
    @Override
    public ResponseEntity<EmailResponseDTO> sendEmail(@Valid @RequestBody EmailRequestDTO emailRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emailService.sendEmail(emailRequest));
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<EmailResponseDTO>> getAllEmails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(emailService.getAllEmails(page, size));
    }

    @GetMapping("/status/{status}")
    @Override
    public ResponseEntity<Page<EmailResponseDTO>> getEmailsByStatus(
            @PathVariable StatusEmail status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(emailService.getEmailsByStatus(status, page, size));
    }

    @GetMapping("/filter")
    @Override
    public ResponseEntity<Page<EmailResponseDTO>> filterEmails(
            @RequestParam(required = false) String emailFrom,
            @RequestParam(required = false) String emailTo,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(emailService.findEmailsWithFilters(
                emailFrom, emailTo, subject, status, startDate, endDate, page, size));
    }

    @GetMapping("/count/{status}")
    public ResponseEntity<Long> countEmailsByStatus(@PathVariable StatusEmail status) {
        return ResponseEntity.ok(emailService.countEmailsByStatus(status));
    }
}