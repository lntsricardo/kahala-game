INSERT INTO kahala.match(id) VALUES (2001);

INSERT INTO kahala.player(id, name, turn, match_id) VALUES (2001, 'First', true, 2001);
INSERT INTO kahala.player(id, name, turn, match_id) VALUES (2002, 'Second', false, 2001);

INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2001, 5, 1, 2001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2002, 6, 2, 2001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2003, 6, 3, 2001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2004, 6, 4, 2001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2005, 6, 5, 2001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2006, 0, 6, 2001);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2007, 7, 7, 2001);

INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2008, 6, 1, 2002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2009, 6, 2, 2002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2010, 6, 3, 2002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2011, 6, 4, 2002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2012, 6, 5, 2002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2013, 6, 6, 2002);
INSERT INTO kahala.pit(id, stones, pit_order, player_id) VALUES (2014, 0, 7, 2002);