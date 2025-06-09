package br.com.juliocesarcoutinho.userservice.repositories;

import br.com.juliocesarcoutinho.userservice.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByAuthority(String authority);
    
    boolean existsByAuthority(String authority);
}
