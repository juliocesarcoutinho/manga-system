package br.com.juliocesarcoutinho.userservice.configs;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.juliocesarcoutinho.userservice.entities.Role;
import br.com.juliocesarcoutinho.userservice.entities.User;
import br.com.juliocesarcoutinho.userservice.repositories.RoleRepository;
import br.com.juliocesarcoutinho.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            // Verifica se já existem roles cadastradas
            if (roleRepository.count() == 0) {
                initRoles();
            }
            
            // Verifica se já existem usuários cadastrados
            if (userRepository.count() == 0) {
                initUsers();
            }
        };
    }
    
    private void initRoles() {
        log.info("Inicializando roles padrão...");
        
        // Verificar e criar ROLE_ADMIN se não existir
        if (!roleRepository.existsByAuthority("ROLE_ADMIN")) {
            Role adminRole = Role.builder()
                    .authority("ROLE_ADMIN")
                    .build();
            roleRepository.save(adminRole);
            log.info("Role ROLE_ADMIN criada com sucesso!");
        }
        
        // Verificar e criar ROLE_USER se não existir
        if (!roleRepository.existsByAuthority("ROLE_USER")) {
            Role userRole = Role.builder()
                    .authority("ROLE_USER")
                    .build();
            roleRepository.save(userRole);
            log.info("Role ROLE_USER criada com sucesso!");
        }
        
        // Verificar e criar ROLE_EDITOR se não existir
        if (!roleRepository.existsByAuthority("ROLE_EDITOR")) {
            Role editorRole = Role.builder()
                    .authority("ROLE_EDITOR")
                    .build();
            roleRepository.save(editorRole);
            log.info("Role ROLE_EDITOR criada com sucesso!");
        }
        
        log.info("Inicialização de roles concluída!");
    }
    
    private void initUsers() {
        log.info("Inicializando usuários padrão...");
        
        // Busca as roles
        Role adminRole = roleRepository.findByAuthority("ROLE_ADMIN").orElseThrow();
        Role userRole = roleRepository.findByAuthority("ROLE_USER").orElseThrow();
        
        // Cria o conjunto de roles para o admin
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminRoles.add(userRole);
        
        // Cria o conjunto de roles para o usuário comum
        Set<Role> regularUserRoles = new HashSet<>();
        regularUserRoles.add(userRole);
        
        // Verifica se já existe um usuário admin
        if (!userRepository.existsByEmail("admin@email.com")) {
            // Cria o usuário admin
            User admin = User.builder()
                    .fullname("Administrador")
                    .email("admin@email.com")
                    .password(passwordEncoder.encode("admin123"))
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .build();
            
            // Adiciona roles ao admin
            admin.setRoles(adminRoles);
            
            userRepository.save(admin);
            log.info("Usuário administrador criado com sucesso!");
        }
        
        // Verifica se já existe um usuário comum
        if (!userRepository.existsByEmail("user@email.com")) {
            // Cria o usuário comum
            User regularUser = User.builder()
                    .fullname("Usuário Comum")
                    .email("user@email.com")
                    .password(passwordEncoder.encode("user123"))
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .build();
            
            // Adiciona roles ao usuário comum
            regularUser.setRoles(regularUserRoles);
            
            userRepository.save(regularUser);
            log.info("Usuário comum criado com sucesso!");
        }
        
        log.info("Inicialização de usuários concluída!");
    }
}
