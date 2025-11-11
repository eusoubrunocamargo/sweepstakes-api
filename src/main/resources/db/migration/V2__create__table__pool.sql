CREATE TABLE pool (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lottery_type VARCHAR(50),
    end_date DATETIME,
    draw_date DATETIME,
    min_value_per_share DECIMAL(10,2),
    max_value_per_share DECIMAL(10,2),
    organizer_id BINARY(16),
    finalized BOOLEAN DEFAULT FALSE,
    created_at DATETIME,
    FOREIGN KEY (organizer_id) REFERENCES organizer(id),
    CONSTRAINT chk_pool_lottery_type CHECK (
        lottery_type IN ('MEGASENA', 'QUINA'))
) ENGINE=InnoDB;


