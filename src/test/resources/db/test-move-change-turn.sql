INSERT INTO kahala.match(id) VALUES (1000);

INSERT INTO kahala.player(id, name, turn, match_id) VALUES (1001, 'First', true, 1000);
INSERT INTO kahala.player(id, name, turn, match_id) VALUES (1002, 'Second', false, 1000);

INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1001, 6, 1, 1001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1002, 6, 2, 1001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1003, 6, 3, 1001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1004, 6, 4, 1001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1005, 6, 5, 1001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1006, 6, 6, 1001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1007, 0, 7, 1001);

INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1008, 6, 1, 1002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1009, 6, 2, 1002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1010, 6, 3, 1002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1011, 6, 4, 1002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1012, 6, 5, 1002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1013, 6, 6, 1002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (1014, 0, 7, 1002);