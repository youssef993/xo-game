CREATE TABLE games (
    id UUID PRIMARY KEY,
    player_x_id VARCHAR(100) NOT NULL,
    player_o_id VARCHAR(100),
    current_turn VARCHAR(1),
    status VARCHAR(30) NOT NULL,
    winner_id VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE,
    finished_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE game_moves (
    id UUID PRIMARY KEY,
    game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    player_id VARCHAR(100) NOT NULL,
    symbol VARCHAR(1) NOT NULL,
    cell_index INTEGER NOT NULL CHECK (cell_index BETWEEN 0 AND 8),
    move_number INTEGER NOT NULL,
    played_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_game_cell UNIQUE (game_id, cell_index),
    CONSTRAINT uk_game_move_number UNIQUE (game_id, move_number)
);

CREATE INDEX idx_games_status_created_at ON games(status, created_at);
CREATE INDEX idx_game_moves_game_id ON game_moves(game_id);
