# Integração entre User-Service e Auth-Service

Este documento descreve como o serviço `user-service` está preparado para integração com o futuro microserviço `auth-service`.

## Estrutura Atual

O `user-service` está configurado com Spring Security e JWT para controle de acesso aos endpoints. Atualmente, ele opera com uma implementação local de autenticação, mas está preparado para se comunicar com um microserviço dedicado à autenticação e autorização (`auth-service`).

### Funcionalidades Implementadas

1. **Configuração do Spring Security**: Todos os endpoints estão protegidos exceto aqueles definidos como públicos.
2. **Filtro JWT**: Verifica a validade dos tokens JWT nas requisições.
3. **Serviço de Token**: Gera e valida tokens JWT localmente (será substituído pela integração com o `auth-service`).
4. **AuthController Temporário**: Fornece endpoints para login e validação de token (será substituído pelo `auth-service`).
5. **Preparação para Integração**: Classes criadas para facilitar a comunicação com o futuro `auth-service`.

## Configurando o Auth-Service

Para implementar o `auth-service`, siga estas etapas:

1. Criar um novo projeto Spring Boot com as seguintes dependências:

   - Spring Security
   - Spring Web
   - Spring Cloud Netflix Eureka Client
   - Spring Data JPA
   - JWT (io.jsonwebtoken)
   - H2 Database (para desenvolvimento)
   - MySQL (para produção)

2. Implementar as seguintes funcionalidades:
   - Autenticação de usuários
   - Geração de tokens JWT
   - Validação de tokens JWT
   - Sincronização de usuários com o `user-service`
   - Gerenciamento de permissões e papéis

## Estrutura do Auth-Service

O `auth-service` deverá ter a seguinte estrutura:

```
auth-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/
│   │   │       └── com/
│   │   │           └── juliocesarcoutinho/
│   │   │               └── authservice/
│   │   │                   ├── AuthServiceApplication.java
│   │   │                   ├── config/
│   │   │                   │   └── SecurityConfig.java
│   │   │                   ├── controller/
│   │   │                   │   └── AuthController.java
│   │   │                   ├── dto/
│   │   │                   │   ├── AuthRequest.java
│   │   │                   │   ├── AuthResponse.java
│   │   │                   │   └── UserSyncRequest.java
│   │   │                   ├── model/
│   │   │                   │   ├── User.java
│   │   │                   │   └── Role.java
│   │   │                   ├── repository/
│   │   │                   │   ├── UserRepository.java
│   │   │                   │   └── RoleRepository.java
│   │   │                   └── service/
│   │   │                       ├── AuthService.java
│   │   │                       ├── TokenService.java
│   │   │                       └── UserService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

## Endpoints do Auth-Service

O `auth-service` deverá implementar os seguintes endpoints:

1. **POST /auth/login**

   - Recebe: `{ "username": "email@example.com", "password": "senha" }`
   - Retorna: `{ "token": "jwt-token", "username": "email@example.com" }`

2. **POST /auth/validate**

   - Recebe: `{ "token": "jwt-token" }`
   - Retorna: `{ "valid": true, "username": "email@example.com" }` ou `{ "valid": false }`

3. **POST /auth/sync/user**
   - Recebe: `{ "userId": "uuid", "email": "email@example.com", "password": "senha", "active": true }`
   - Retorna: 200 OK se a sincronização for bem-sucedida

## Integração entre os Serviços

Quando o `auth-service` estiver implementado:

1. O `user-service` continuará sendo responsável pelo gerenciamento de dados dos usuários.
2. O `auth-service` será responsável pela autenticação e autorização.
3. Ao criar um novo usuário no `user-service`, os dados de autenticação serão enviados para o `auth-service`.
4. Todas as requisições para o `user-service` precisarão de um token JWT gerado pelo `auth-service`.

## Configuração da Comunicação

A comunicação entre os serviços é feita via REST com suporte a Circuit Breaker e Retry para garantir a resiliência:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      authService:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 5s
        failureRateThreshold: 50
        eventConsumerBufferSize: 10
  retry:
    instances:
      authService:
        maxAttempts: 3
        waitDuration: 3s
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
```

## Segurança

Para garantir a segurança da comunicação, ambos os serviços devem usar a mesma chave secreta para JWT:

```yaml
app:
  security:
    jwt:
      secret: ${JWT_SECRET:5r2ek1l98tl5xmdfgr1mdrg21rmg2sdgdfg165erg1dfg32}
      expiration: ${JWT_EXPIRATION:86400000} # 24 horas em milissegundos
```

## Próximos Passos

1. Implementar o microserviço `auth-service`
2. Configurar a comunicação entre os serviços
3. Remover a implementação temporária de autenticação do `user-service`
4. Atualizar o `api-gateway` para rotear corretamente as requisições de autenticação
