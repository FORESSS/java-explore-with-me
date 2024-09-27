CREATE TABLE IF NOT EXISTS statistics (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  app VARCHAR(128) NOT NULL,        -- Убрано ограничение длины
  uri VARCHAR(128) NOT NULL,        -- Убрано ограничение длины
  ip VARCHAR(16) NOT NULL,         -- Убрано ограничение длины
  timestamp TIMESTAMP NOT NULL
);