CREATE TABLE pool_participant (
    id BINARY(16) PRIMARY KEY,
    player_id BINARY(16) NOT NULL,
    pool_id BINARY(16) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    max_value_to_bet DECIMAL(10,2),
    joined_at DATETIME NOT NULL,
    CONSTRAINT fk_participant_player FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE,
    CONSTRAINT fk_participant_pool FOREIGN KEY (pool_id) REFERENCES pool(id) ON DELETE CASCADE,
    CONSTRAINT uq_player_pool UNIQUE (player_id, pool_id),
    CONSTRAINT uq_nickname_poll UNIQUE (nickname, pool_id)
) ENGINE=InnoDB;
