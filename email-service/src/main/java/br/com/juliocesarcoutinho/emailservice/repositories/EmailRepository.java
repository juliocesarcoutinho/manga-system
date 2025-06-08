package br.com.juliocesarcoutinho.emailservice.repositories;
import br.com.juliocesarcoutinho.emailservice.entities.Email;
import br.com.juliocesarcoutinho.emailservice.enums.StatusEmail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    Page<Email> findByStatus(StatusEmail status, Pageable pageable);

    Page<Email> findByEmailFromContainingIgnoreCase(String emailFrom, Pageable pageable);

    Page<Email> findByEmailToContainingIgnoreCase(String emailTo, Pageable pageable);

    @Query("SELECT e FROM Email e WHERE " +
           "(:emailFrom IS NULL OR e.emailFrom LIKE %:emailFrom%) AND " +
           "(:emailTo IS NULL OR e.emailTo LIKE %:emailTo%) AND " +
           "(:subject IS NULL OR e.subject LIKE %:subject%) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:startDate IS NULL OR e.sendDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.sendDate <= :endDate)")
    Page<Email> findEmailsWithFilters(
            @Param("emailFrom") String emailFrom,
            @Param("emailTo") String emailTo,
            @Param("subject") String subject,
            @Param("status") StatusEmail status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    List<Email> findTop10ByStatusOrderBySendDateDesc(StatusEmail status);

    long countByStatus(StatusEmail status);
}