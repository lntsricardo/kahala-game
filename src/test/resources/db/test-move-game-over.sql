INSERT INTO kahala.match(id) VALUES (4000);

INSERT INTO kahala.player(id, name, turn, match_id) VALUES (4001, 'First', true, 4000);
INSERT INTO kahala.player(id, name, turn, match_id) VALUES (4002, 'Second', false, 4000);

INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4001, 0, 1, 4001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4002, 0, 2, 4001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4003, 0, 3, 4001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4004, 0, 4, 4001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4005, 0, 5, 4001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4006, 1, 6, 4001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4007, 36, 7, 4001);

INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4008, 5, 1, 4002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4009, 6, 2, 4002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4010, 6, 3, 4002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4011, 6, 4, 4002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4012, 6, 5, 4002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4013, 6, 6, 4002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (4014, 0, 7, 4002);