package br.com.juliocesarcoutinho.userservice.repositories;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.juliocesarcoutinho.userservice.entities.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    List<UserRole> findAllByUserId(UUID userId);
    
    Set<UserRole> findByUserId(UUID userId);
    
    boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);
    
    void deleteByUserIdAndRoleId(UUID userId, UUID roleId);
    
    long countByRoleId(UUID roleId);
}
