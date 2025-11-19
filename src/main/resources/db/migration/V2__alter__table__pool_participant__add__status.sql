ALTER TABLE pool_participant
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

ALTER TABLE pool_participant
ADD CONSTRAINT chk_participant_status
CHECK (status IN('PENDING', 'CONFIRMED', 'EXPIRED', 'REFUNDED'));