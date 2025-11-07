CREATE TABLE organizer (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    whatsapp VARCHAR(20) UNIQUE NOT NULL,
    keyword VARCHAR(50) NOT NULL,
    created_at DATETIME
);
