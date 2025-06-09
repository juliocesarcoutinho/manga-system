package br.com.juliocesarcoutinho.userservice.services;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.juliocesarcoutinho.userservice.dtos.RoleRequestDTO;
import br.com.juliocesarcoutinho.userservice.dtos.RoleResponseDTO;
import br.com.juliocesarcoutinho.userservice.entities.Role;
import br.com.juliocesarcoutinho.userservice.enums.RoleName;
import br.com.juliocesarcoutinho.userservice.exceptions.ResourceAlreadyExistsException;
import br.com.juliocesarcoutinho.userservice.exceptions.ResourceInUseException;
import br.com.juliocesarcoutinho.userservice.exceptions.ResourceNotFoundException;
import br.com.juliocesarcoutinho.userservice.repositories.RoleRepository;
import br.com.juliocesarcoutinho.userservice.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    
    /**
     * Inicializa as roles padrão do sistema
     */
    @Transactional
    public void initializeRoles() {
        log.info("Inicializando roles padrão do sistema");
        
        Arrays.asList(RoleName.values()).forEach(roleName -> {
            String authority = roleName.toString();
            if (!roleRepository.existsByAuthority(authority)) {
                Role role = Role.builder()
                        .authority(authority)
                        .build();
                roleRepository.save(role);
                log.info("Role criada: {}", authority);
            }
        });
    }
    
    /**
     * Cria uma nova role
     */
    @Transactional
    public RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO) {
        if (roleRepository.existsByAuthority(roleRequestDTO.authority())) {
            throw new ResourceAlreadyExistsException("Role com este nome já existe");
        }
        
        Role role = Role.builder()
                .authority(roleRequestDTO.authority())
                .build();
                
        Role savedRole = roleRepository.save(role);
        log.info("Nova role criada: {}", savedRole.getAuthority());
        
        return toResponseDTO(savedRole);
    }
    
    /**
     * Retorna uma role pelo ID
     */
    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada"));
        
        return toResponseDTO(role);
    }
    
    /**
     * Retorna uma role pelo nome (authority)
     */
    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleByAuthority(String authority) {
        Role role = roleRepository.findByAuthority(authority)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada"));
        
        return toResponseDTO(role);
    }
    
    /**
     * Lista todas as roles
     */
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Atualiza uma role existente
     */
    @Transactional
    public RoleResponseDTO updateRole(UUID id, RoleRequestDTO roleRequestDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada"));
        
        // Se está alterando a authority, verifica se já existe
        if (!role.getAuthority().equals(roleRequestDTO.authority()) && 
                roleRepository.existsByAuthority(roleRequestDTO.authority())) {
            throw new ResourceAlreadyExistsException("Já existe uma role com este nome");
        }
        
        role.setAuthority(roleRequestDTO.authority());
        
        Role updatedRole = roleRepository.save(role);
        log.info("Role atualizada: {}", updatedRole.getAuthority());
        
        return toResponseDTO(updatedRole);
    }
    
    /**
     * Exclui uma role (apenas se não estiver em uso)
     */
    @Transactional
    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada"));
        
        // Verifica se a role está em uso
        if (userRoleRepository.countByRoleId(id) > 0) {
            throw new ResourceInUseException("Não é possível excluir esta role pois está em uso por usuários");
        }
        
        roleRepository.delete(role);
        log.info("Role excluída: {}", role.getAuthority());
    }
    
    /**
     * Obtém ou cria uma role pelo nome
     */
    @Transactional
    public Role getOrCreateByAuthority(String authority) {
        return roleRepository.findByAuthority(authority)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .authority(authority)
                            .build();
                    return roleRepository.save(newRole);
                });
    }
    
    /**
     * Converte uma entidade Role para DTO de resposta
     */
    private RoleResponseDTO toResponseDTO(Role role) {
        return new RoleResponseDTO(
                role.getId(),
                role.getAuthority()
        );
    }
}
