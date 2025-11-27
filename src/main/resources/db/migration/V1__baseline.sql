CREATE TABLE users (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    whatsapp VARCHAR(20) NOT NULL,
    validated_user BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
    admin_fee_percentage NUMERIC(5,4) NOT NULL DEFAULT 0.05,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_pool_status CHECK (status IN ('OPEN', 'WAITING_PAYMENTS', 'FINALIZED', 'CANCELED')),
    CONSTRAINT chk_pool_lottery_type CHECK (lottery_type IN ('MEGASENA', 'QUINA', 'LIVRE')),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE pool_participant (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    pool_id BINARY(16) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    max_value_to_bet DECIMAL(10,2),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_participant_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_participant_pool FOREIGN KEY (pool_id) REFERENCES pool(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_pool UNIQUE (user_id, pool_id),
    CONSTRAINT uq_nickname_pool UNIQUE (nickname, pool_id),
    CONSTRAINT chk_participant_status CHECK (status IN('PENDING', 'CONFIRMED', 'EXPIRED', 'REFUNDED'))
) ENGINE=InnoDB;

CREATE TABLE generic_pool (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    keyword VARCHAR(50) NOT NULL,
    description TEXT,
    pool_value DECIMAL(10,2) NOT NULL,
    end_date DATETIME NOT NULL,
    draw_date DATETIME NOT NULL,
    user_id BINARY(16) NOT NULL,
    finalized BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    admin_fee_percentage NUMERIC(5,4) NOT NULL DEFAULT 0.05,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    CONSTRAINT chk_generic_pool_status CHECK (status IN ('OPEN', 'WAITING_PAYMENTS', 'FINALIZED', 'CANCELED')),
    CONSTRAINT fk_pool_generic_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE generic_option (
    id BINARY(16) PRIMARY KEY,
    generic_pool_id BINARY(16) NOT NULL,
    label VARCHAR(100) NOT NULL,
    creator_choice TINYINT(1) NULL DEFAULT NULL,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_generic_option_pool FOREIGN KEY (generic_pool_id) REFERENCES generic_pool(id) ON DELETE CASCADE,
    UNIQUE(generic_pool_id, label),
    UNIQUE(generic_pool_id, creator_choice)
) ENGINE=InnoDB;

CREATE TABLE generic_participant (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    generic_pool_id BINARY(16) NOT NULL,
    chosen_option_id BINARY(16) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT chk_generic_participant_status CHECK (status IN('PENDING', 'CONFIRMED', 'EXPIRED', 'REFUNDED', 'CANCELED')),
    CONSTRAINT fk_generic_participant_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_generic_participant_pool FOREIGN KEY (generic_pool_id) REFERENCES generic_pool(id) ON DELETE CASCADE,
    CONSTRAINT fk_generic_participant_option FOREIGN KEY (chosen_option_id) REFERENCES generic_option(id) ON DELETE CASCADE,
    UNIQUE(user_id, generic_pool_id, chosen_option_id)
) ENGINE=InnoDB;
