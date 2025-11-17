CREATE TABLE users (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    whatsapp VARCHAR(20) NOT NULL,
    validated_user BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    UNIQUE(whatsapp)
) ENGINE=InnoDB;

CREATE TABLE user_roles (
    user_id BINARY(16) NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role);

CREATE TABLE pool (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    keyword VARCHAR(50) NOT NULL,
    lottery_type VARCHAR(50),
    end_date DATETIME,
    draw_date DATETIME,
    min_value_per_share DECIMAL(10,2),
    max_value_per_share DECIMAL(10,2),
    user_id BINARY(16),
    finalized BOOLEAN DEFAULT FALSE,
    created_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_pool_lottery_type CHECK (
        lottery_type IN ('MEGASENA', 'QUINA')
    )
) ENGINE=InnoDB;

CREATE TABLE pool_participant (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    pool_id BINARY(16) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    max_value_to_bet DECIMAL(10,2),
    joined_at DATETIME NOT NULL,
    CONSTRAINT fk_participant_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_participant_pool FOREIGN KEY (pool_id) REFERENCES pool(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_pool UNIQUE (user_id, pool_id),
    CONSTRAINT uq_nickname_pool UNIQUE (nickname, pool_id)
) ENGINE=InnoDB;