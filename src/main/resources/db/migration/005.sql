CREATE TABLE card_requests (
                               id SERIAL PRIMARY KEY,
                               user_id INTEGER NOT NULL,
                               card_id INTEGER NULL,
                               request_type VARCHAR(20) NOT NULL,
                               status VARCHAR(20) NOT NULL,

                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                               FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE

);