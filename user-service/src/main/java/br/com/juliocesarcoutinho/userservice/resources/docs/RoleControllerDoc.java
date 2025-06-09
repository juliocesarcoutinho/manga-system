package br.com.juliocesarcoutinho.userservice.resources.docs;

import br.com.juliocesarcoutinho.userservice.dtos.RoleRequestDTO;
import br.com.juliocesarcoutinho.userservice.dtos.RoleResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Tag(name = "Roles", description = "API para gerenciamento de papéis/autoridades no sistema")
public interface RoleControllerDoc {

    @Operation(summary = "Criar um novo papel", description = "Cria uma nova role no sistema")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Role criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "409", description = "Role já existe com esse nome")
    })
    ResponseEntity<RoleResponseDTO> createRole(@Valid @RequestBody RoleRequestDTO roleRequestDTO);
    
    @Operation(summary = "Listar todos os papéis", description = "Lista todas as roles disponíveis no sistema")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json", 
                                array = @ArraySchema(schema = @Schema(implementation = RoleResponseDTO.class)))
            )
    })
    ResponseEntity<List<RoleResponseDTO>> getAllRoles();
    
    @Operation(summary = "Buscar papel por ID", description = "Retorna uma role por seu ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Role não encontrada")
    })
    ResponseEntity<RoleResponseDTO> getRoleById(@Parameter(description = "ID da role", required = true) @PathVariable UUID id);
    
    @Operation(summary = "Buscar papel pelo nome", description = "Retorna uma role pelo seu nome (authority)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Role não encontrada")
    })
    ResponseEntity<RoleResponseDTO> getRoleByAuthority(@Parameter(description = "Nome da role (authority)", required = true) String authority);
    
    @Operation(summary = "Atualizar um papel", description = "Atualiza uma role existente no sistema")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Role não encontrada"),
            @ApiResponse(responseCode = "409", description = "Nome de role já em uso")
    })
    ResponseEntity<RoleResponseDTO> updateRole(
            @Parameter(description = "ID da role", required = true) @PathVariable UUID id,
            @Valid @RequestBody RoleRequestDTO roleRequestDTO);
    
    @Operation(summary = "Excluir um papel", description = "Exclui uma role do sistema (apenas se não estiver em uso)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Role excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Role não encontrada"),
            @ApiResponse(responseCode = "409", description = "Role não pode ser excluída porque está em uso")
    })
    ResponseEntity<Void> deleteRole(@Parameter(description = "ID da role", required = true) @PathVariable UUID id);
}
