CREATE INDEX idx_pool_status_end_date ON pool(status, end_date);

CREATE INDEX idx_pool_created_at ON pool(created_at DESC);

CREATE INDEX idx_pool_organizer_status ON pool(user_id, status);

