package br.com.juliocesarcoutinho.userservice.resources;

import br.com.juliocesarcoutinho.userservice.dtos.UserRequestDTO;
import br.com.juliocesarcoutinho.userservice.dtos.UserResponseDTO;
import br.com.juliocesarcoutinho.userservice.dtos.UserUpdateDTO;
import br.com.juliocesarcoutinho.userservice.resources.docs.UserControllerDoc;
import br.com.juliocesarcoutinho.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserControllerDoc {

    private final UserService userService;

    @PostMapping
    @Override
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        log.info("Requisição para criar um novo usuário recebida");
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestDTO));
    }
    
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        log.info("Buscando usuário pelo ID: {}", id);
        return ResponseEntity.ok(userService.findUserById(id));
    }
    
    @GetMapping("/email/{email}")
    @Override
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        log.info("Buscando usuário pelo e-mail: {}", email);
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }
    
    @GetMapping
    @Override
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @PageableDefault(sort = "fullname", direction = Sort.Direction.ASC, page = 0, size = 10)
            Pageable pageable) {
        log.info("Listando todos os usuários com paginação: {}", pageable);
        Page<UserResponseDTO> users = userService.findAllUsers(pageable);
        
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/search")
    @Override
    public ResponseEntity<Page<UserResponseDTO>> searchUsersByName(
            @RequestParam String name,
            @PageableDefault(sort = "fullname", direction = Sort.Direction.ASC, page = 0, size = 10)
            Pageable pageable) {
        log.info("Buscando usuários pelo nome: {} com paginação: {}", name, pageable);
        Page<UserResponseDTO> users = userService.findUsersByName(name, pageable);
        
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("Atualizando usuário com ID: {}", id);
        return ResponseEntity.ok(userService.updateUser(id, userUpdateDTO));
    }
    
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Excluindo usuário com ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/activate")
    @Override
    public ResponseEntity<UserResponseDTO> activateUser(@PathVariable UUID id) {
        log.info("Ativando usuário com ID: {}", id);
        return ResponseEntity.ok(userService.activateUser(id));
    }
    
    @PatchMapping("/{id}/deactivate")
    @Override
    public ResponseEntity<UserResponseDTO> deactivateUser(@PathVariable UUID id) {
        log.info("Desativando usuário com ID: {}", id);
        return ResponseEntity.ok(userService.deactivateUser(id));
    }
}