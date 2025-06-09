CREATE TABLE tb_roles (
    id BINARY(16) NOT NULL,
    authority VARCHAR(60) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE INDEX idx_roles_authority ON tb_roles (authority);

CREATE TABLE tb_users (
    id BINARY(16) NOT NULL,
    fullname VARCHAR(160) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);

CREATE INDEX idx_users_email ON tb_users (email);

CREATE TABLE tb_users_roles (
    user_id BINARY(16) NOT NULL,
    role_id BINARY(16) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES tb_users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tb_roles(id) ON DELETE CASCADE
);