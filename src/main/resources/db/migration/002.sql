CREATE TABLE cards (
                       id SERIAL PRIMARY KEY,
                       number VARCHAR(16) NOT NULL UNIQUE,
                       user_id INTEGER NOT NULL,
                       validity_period VARCHAR(7) NOT NULL,
                       status VARCHAR(20) NOT NULL,
                       balance DECIMAL(15, 2),

                       CONSTRAINT fk_card_user
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);