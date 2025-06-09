# Plano de Implementação do Auth-Service

## Estrutura do Projeto

O novo microserviço `auth-service` será responsável por:

1. Autenticação de usuários
2. Geração e validação de tokens JWT
3. Autorização baseada em papéis (roles)
4. Integração com o `user-service` para sincronização de dados

## Principais Componentes

### 1. Configuração de Segurança
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/auth/login").permitAll();
                    authorize.requestMatchers("/auth/validate").permitAll();
                    authorize.requestMatchers("/auth/sync/**").permitAll();
                    authorize.anyRequest().authenticated();
                })
                .build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 2. Modelo de Dados
```java
@Entity
@Table(name = "tb_auth_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
    @Id
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private boolean active;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_auth_users_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<AuthRole> roles;
}
```

### 3. Serviço de Autenticação
```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    
    public String authenticate(String email, String password) {
        AuthUser user = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Credenciais inválidas"));
        
        if (!user.isActive()) {
            throw new AuthenticationException("Usuário desativado");
        }
        
        if (passwordEncoder.matches(password, user.getPassword())) {
            return tokenService.generateToken(user);
        }
        
        throw new AuthenticationException("Credenciais inválidas");
    }
    
    public boolean validateToken(String token) {
        return tokenService.validateToken(token);
    }
    
    public void syncUser(UUID userId, String email, String password, boolean active) {
        AuthUser authUser = authUserRepository.findById(userId)
                .orElse(AuthUser.builder()
                        .id(userId)
                        .build());
        
        authUser.setEmail(email);
        authUser.setPassword(password);
        authUser.setActive(active);
        
        // Adicionar role padrão se for novo usuário
        if (authUser.getRoles() == null || authUser.getRoles().isEmpty()) {
            AuthRole userRole = authRoleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role padrão não encontrada"));
            authUser.setRoles(Set.of(userRole));
        }
        
        authUserRepository.save(authUser);
    }
}
```

### 4. Controller de Autenticação
```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = authService.authenticate(request.getEmail(), request.getPassword());
        
        Map<String, String> response = Map.of(
                "token", token,
                "username", request.getEmail()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody TokenRequest request) {
        boolean valid = authService.validateToken(request.getToken());
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);
        
        if (valid) {
            String username = authService.extractUsername(request.getToken());
            response.put("username", username);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/sync/user")
    public ResponseEntity<Void> syncUser(@RequestBody UserSyncRequest request) {
        authService.syncUser(
                UUID.fromString(request.getUserId()),
                request.getEmail(),
                request.getPassword(),
                request.isActive()
        );
        
        return ResponseEntity.ok().build();
    }
}
```

## Passos para Implementação

1. Criar um novo projeto Spring Boot
2. Configurar as dependências necessárias
3. Implementar o modelo de dados
4. Implementar os serviços de autenticação e token
5. Implementar os controladores REST
6. Configurar a comunicação com o Eureka Server
7. Testar a integração com o `user-service`

## Configuração application.yml

```yaml
spring:
  application:
    name: auth-service
  datasource:
    url: jdbc:h2:mem:authdb
    driverClassName: org.h2.Driver
    username: sa
    password: password
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console

server:
  port: 8083

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URI:http://localhost:8761/eureka}
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    hostname: localhost

app:
  security:
    jwt:
      secret: ${JWT_SECRET:5r2ek1l98tl5xmdfgr1mdrg21rmg2sdgdfg165erg1dfg32}
      expiration: ${JWT_EXPIRATION:86400000}  # 24 horas em milissegundos
```

## Integração com User-Service

O `auth-service` se comunicará com o `user-service` de duas formas:

1. O `user-service` enviará dados de usuários novos ou atualizados para o `auth-service` através do endpoint `/auth/sync/user`.
2. O `user-service` validará tokens JWT com o `auth-service` através do endpoint `/auth/validate`.

Para completar a integração, será necessário modificar o `user-service` para:

1. Remover a implementação local de autenticação
2. Atualizar o filtro JWT para usar o serviço de autenticação externo
3. Configurar o circuito-breaker para gerenciar falhas de comunicação com o `auth-service`
