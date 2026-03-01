-- 1️⃣ Adiciona coluna permitindo NULL temporariamente
ALTER TABLE transactions
    ADD COLUMN idempotency_key VARCHAR(255);

-- 2️⃣ Preenche registros antigos com valor único
UPDATE transactions
SET idempotency_key = gen_random_uuid()::text
WHERE idempotency_key IS NULL;

-- 3️⃣ Agora pode tornar NOT NULL
ALTER TABLE transactions
    ALTER COLUMN idempotency_key SET NOT NULL;

-- 4️⃣ E criar constraint UNIQUE
ALTER TABLE transactions
    ADD CONSTRAINT unique_idempotency_key UNIQUE (idempotency_key);