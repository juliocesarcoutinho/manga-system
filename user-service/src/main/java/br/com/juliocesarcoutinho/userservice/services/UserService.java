package br.com.juliocesarcoutinho.userservice.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.juliocesarcoutinho.userservice.dtos.UserRequestDTO;
import br.com.juliocesarcoutinho.userservice.dtos.UserResponseDTO;
import br.com.juliocesarcoutinho.userservice.dtos.UserUpdateDTO;
import br.com.juliocesarcoutinho.userservice.entities.User;
import br.com.juliocesarcoutinho.userservice.exceptions.ResourceAlreadyExistsException;
import br.com.juliocesarcoutinho.userservice.exceptions.ResourceNotFoundException;
import br.com.juliocesarcoutinho.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailIntegrationService emailIntegrationService;

    /**
     * Cria um novo usuário no sistema
     */
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.email())) {
            throw new ResourceAlreadyExistsException("Usuário com este e-mail já existe");
        }
        
        User user = User.builder()
                .fullname(userRequestDTO.fullname())
                .email(userRequestDTO.email())
                .password(passwordEncoder.encode(userRequestDTO.password()))
                .build();
                
        User savedUser = userRepository.save(user);

        // Enviar email de boas-vindas (apenas em produção)
        emailIntegrationService.sendWelcomeEmail(savedUser.getFullname(), savedUser.getEmail());
        
        // Aqui seria o ponto de integração com o auth-service quando ele estiver implementado
        // authServiceIntegration.syncUserCreation(toResponseDTO(savedUser), userRequestDTO.password());
        
        return toResponseDTO(savedUser);
    }
    
    /**
     * Busca um usuário pelo ID
     */
    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        return toResponseDTO(user);
    }
    
    /**
     * Busca um usuário pelo email
     */
    @Transactional(readOnly = true)
    public UserResponseDTO findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        return toResponseDTO(user);
    }
    
    /**
     * Lista todos os usuários com suporte à paginação
     */
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }
    
    /**
     * Atualiza um usuário existente
     */
    @Transactional
    public UserResponseDTO updateUser(UUID id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        // Se o email estiver sendo alterado, verifica se já existe
        if (!user.getEmail().equals(userUpdateDTO.email()) && 
                userRepository.existsByEmail(userUpdateDTO.email())) {
            throw new ResourceAlreadyExistsException("Este e-mail já está em uso por outro usuário");
        }
        
        // Atualiza os campos
        user.setFullname(userUpdateDTO.fullname());
        user.setEmail(userUpdateDTO.email());
        
        // Se foi informada uma nova senha, atualiza também
        if (userUpdateDTO.password() != null && !userUpdateDTO.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userUpdateDTO.password()));
        }
        
        User updatedUser = userRepository.save(user);
        
        log.info("Usuário atualizado com sucesso: {}", updatedUser.getId());
        
        return toResponseDTO(updatedUser);
    }
    
    /**
     * Exclui um usuário do sistema
     */
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        
        userRepository.deleteById(id);
        log.info("Usuário removido com sucesso: {}", id);
    }
    
    /**
     * Desativa um usuário no sistema
     */
    @Transactional
    public UserResponseDTO deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        user.setActive(false);
        User updatedUser = userRepository.save(user);
        
        log.info("Usuário desativado com sucesso: {}", id);
        
        return toResponseDTO(updatedUser);
    }
    
    /**
     * Ativa um usuário no sistema
     */
    @Transactional
    public UserResponseDTO activateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        user.setActive(true);
        User updatedUser = userRepository.save(user);
        
        log.info("Usuário ativado com sucesso: {}", id);
        
        return toResponseDTO(updatedUser);
    }
    
    /**
     * Busca usuários pelo nome
     */
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findUsersByName(String name, Pageable pageable) {
        return userRepository.findByFullnameContainingIgnoreCase(name, pageable)
                .map(this::toResponseDTO);
    }
    
    /**
     * Converte uma entidade User para DTO de resposta
     */
    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFullname(),
                user.getEmail(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
