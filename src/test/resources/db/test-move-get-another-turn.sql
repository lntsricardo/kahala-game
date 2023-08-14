INSERT INTO kahala.match(id) VALUES (3000);

INSERT INTO kahala.player(id, name, turn, match_id) VALUES (3001, 'First', true, 3000);
INSERT INTO kahala.player(id, name, turn, match_id) VALUES (3002, 'Second', false, 3000);

INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3001, 6, 1, 3001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3002, 6, 2, 3001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3003, 6, 3, 3001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3004, 6, 4, 3001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3005, 6, 5, 3001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3006, 6, 6, 3001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3007, 0, 7, 3001);

INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3008, 6, 1, 3002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3009, 6, 2, 3002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3010, 6, 3, 3002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3011, 6, 4, 3002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3012, 6, 5, 3002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3013, 6, 6, 3002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (3014, 0, 7, 3002);