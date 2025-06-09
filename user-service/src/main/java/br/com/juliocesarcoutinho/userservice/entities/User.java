package br.com.juliocesarcoutinho.userservice.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<UserRole> userRoles = new HashSet<>();
    
    @Transient
    public Set<Role> getRoles() {
        return userRoles.stream().map(UserRole::getRole).collect(Collectors.toSet());
    }
    
    public void addRole(Role role) {
        UserRole userRole = new UserRole();
        userRole.setUser(this);
        userRole.setRole(role);
        userRoles.add(userRole);
    }
    
    public void removeRole(Role role) {
        userRoles.removeIf(userRole -> userRole.getRole().equals(role));
    }
    
    public void setRoles(Set<Role> roles) {
        // Clear existing roles
        userRoles.clear();
        
        // Add new roles
        if (roles != null) {
            roles.forEach(this::addRole);
        }
    }

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private boolean active;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        active = true;
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Builder pattern implementation to handle collections properly
    @Builder
    public User(UUID id, String fullname, String email, String password, LocalDateTime createdAt, 
                LocalDateTime updatedAt, boolean active) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.active = active;
        this.userRoles = new HashSet<>();
    }
}
