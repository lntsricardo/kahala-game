INSERT INTO kahala.match(id) VALUES (5000);

INSERT INTO kahala.player(id, name, turn, match_id) VALUES (5001, 'First', true, 5000);
INSERT INTO kahala.player(id, name, turn, match_id) VALUES (5002, 'Second', false, 5000);

INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5001, 6, 1, 5001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5002, 6, 2, 5001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5003, 6, 3, 5001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5004, 6, 4, 5001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5005, 6, 5, 5001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5006, 6, 6, 5001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5007, 0, 7, 5001);

INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5008, 6, 1, 5002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5009, 6, 2, 5002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5010, 6, 3, 5002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5011, 6, 4, 5002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5012, 6, 5, 5002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5013, 6, 6, 5002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (5014, 0, 7, 5002);