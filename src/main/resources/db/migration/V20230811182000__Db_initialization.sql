CREATE SCHEMA kahala;

CREATE TABLE kahala.match (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    status VARCHAR(15) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE kahala.player (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    match_id BIGINT NOT NULL,
    name VARCHAR(50),
    turn BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_player_match FOREIGN KEY (match_id) REFERENCES kahala.match(id)
);

CREATE TABLE kahala.pit (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    player_id BIGINT NOT NULL,
    stones SMALLINT NOT NULL,
    pit_order SMALLINT NOT NULL,
    CONSTRAINT fk_pit_player FOREIGN KEY (player_id) REFERENCES kahala.player(id)
);