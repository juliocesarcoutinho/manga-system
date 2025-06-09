package br.com.juliocesarcoutinho.userservice.resources.docs;

import br.com.juliocesarcoutinho.userservice.dtos.UserRequestDTO;
import br.com.juliocesarcoutinho.userservice.dtos.UserResponseDTO;
import br.com.juliocesarcoutinho.userservice.dtos.UserUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Usuários", description = "API para gerenciamento de usuários")
public interface UserControllerDoc {

    @Operation(summary = "Criar um novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já está em uso")
    })
    ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO);
    
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID do usuário", required = true) UUID id);
    
    @Operation(summary = "Buscar usuário por e-mail", description = "Retorna um usuário pelo seu endereço de e-mail")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<UserResponseDTO> getUserByEmail(
            @Parameter(description = "E-mail do usuário", required = true) String email);
    
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista paginada de todos os usuários")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuários retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "204", description = "Nenhum usuário encontrado")
    })
    ResponseEntity<Page<UserResponseDTO>> getAllUsers(@ParameterObject Pageable pageable);
    
    @Operation(summary = "Buscar usuários por nome", description = "Retorna uma lista paginada de usuários que contenham o texto informado no nome")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuários encontrados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "204", description = "Nenhum usuário encontrado com o nome informado")
    })
    ResponseEntity<Page<UserResponseDTO>> searchUsersByName(
            @Parameter(description = "Parte do nome a ser buscado", required = true) String name,
            @ParameterObject Pageable pageable);
    
    @Operation(summary = "Atualizar um usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "E-mail já está em uso por outro usuário")
    })
    ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID do usuário", required = true) UUID id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO);
    
    @Operation(summary = "Excluir um usuário", description = "Exclui permanentemente um usuário do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID do usuário", required = true) UUID id);
    
    @Operation(summary = "Ativar um usuário", description = "Ativa um usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário ativado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<UserResponseDTO> activateUser(
            @Parameter(description = "ID do usuário", required = true) UUID id);
    
    @Operation(summary = "Desativar um usuário", description = "Desativa um usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário desativado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<UserResponseDTO> deactivateUser(
            @Parameter(description = "ID do usuário", required = true) UUID id);
}