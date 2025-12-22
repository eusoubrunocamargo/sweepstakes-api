ALTER TABLE pool DROP CHECK chk_pool_lottery_type;
ALTER TABLE pool ADD CONSTRAINT chk_pool_lottery_type CHECK (lottery_type IN ('MEGASENA', 'QUINA', 'LOTOFACIL'));