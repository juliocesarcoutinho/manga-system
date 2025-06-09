package br.com.juliocesarcoutinho.userservice.resources;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocesarcoutinho.userservice.dtos.RoleRequestDTO;
import br.com.juliocesarcoutinho.userservice.dtos.RoleResponseDTO;
import br.com.juliocesarcoutinho.userservice.resources.docs.RoleControllerDoc;
import br.com.juliocesarcoutinho.userservice.services.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController implements RoleControllerDoc {

    private final RoleService roleService;
    
    @PostMapping
    @Override
    public ResponseEntity<RoleResponseDTO> createRole(@Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        log.info("Requisição para criar uma nova role: {}", roleRequestDTO.authority());
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(roleRequestDTO));
    }
    
    @GetMapping
    @Override
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        log.info("Listando todas as roles");
        return ResponseEntity.ok(roleService.getAllRoles());
    }
    
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable UUID id) {
        log.info("Buscando role pelo ID: {}", id);
        return ResponseEntity.ok(roleService.getRoleById(id));
    }
    
    @GetMapping("/authority/{authority}")
    @Override
    public ResponseEntity<RoleResponseDTO> getRoleByAuthority(@PathVariable String authority) {
        log.info("Buscando role pelo nome: {}", authority);
        return ResponseEntity.ok(roleService.getRoleByAuthority(authority));
    }
    
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<RoleResponseDTO> updateRole(
            @PathVariable UUID id, 
            @Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        log.info("Atualizando role com ID: {}", id);
        return ResponseEntity.ok(roleService.updateRole(id, roleRequestDTO));
    }
    
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        log.info("Excluindo role com ID: {}", id);
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
