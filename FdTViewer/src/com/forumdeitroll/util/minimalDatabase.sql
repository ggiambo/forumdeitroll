insert into authors (nick, messages, password, salt, hash) values ('admin', 2, '', 'e0342c65575601d6', 'c27a20319df29b1ab0e9046defbc192534e5a2e373b08045');

insert into preferences (nick, `key`, `value`) values ('admin', 'super', 'yes');

insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (1, 'qui comando io $cool', '2010-05-13 14:30:15', 'benvenuto nel fdt !', 1, 1, 'admin', 'Forum iniziale', 'true', 0);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (2, 'Proot !', '2010-05-13 14:35:03', 'benvenuto nel fdt !', 1, 1, null, 'Forum iniziale', 'true', 0);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (3, 'Scritto da: <BR>&gt; Proot !<BR><BR>:@ !', '2010-05-13 14:55:45', 'benvenuto nel fdt !', 1, 2, 'admin', 'Forum iniziale', 'true', 0);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (4, 'Blah Banf montanari orologio a cucu', '2010-05-13 18:03:12', 'Primo !', 4, 4, null, 'Procura Svizzera', 'true', 0);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (5, ':(', '2010-05-14 09:15:54', 'Secondo !', 4, 4, null, 'Procura Svizzera', 'true', 0);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (6, 'Nel Forum Principale', '2010-05-14 12:23:13', 'Nel Forum Principale', 6, 6, null, null, 'true', 0);

insert into ads (id, title, visurl, content) values (1, 'Saune Gay‎', 'www.luino.it/stazione/', 'Trova la sauna gay ideale vicino a casa tua !');
insert into ads (id, title, visurl, content) values (2, 'Dildi Giganti', 'www.megadildo.it', 'Regala un sorriso al tuo bucio del cuxo !');
insert into ads (id, title, visurl, content) values (3, 'Suore per tutti‎‎', 'www.suoregratis.it', 'La tua Suora personalizzata, a prezzi imbattibili !');
insert into ads (id, title, visurl, content) values (4, 'M5S Blah Banf‎', 'www.m5sprot.it', 'Generatore automatico di fango da gettare sul M5S');
insert into ads (id, title, visurl, content) values (5, 'Foto pazzesche !', 'www.nuncipossocredere.it', 'Foto hot scattate dalla Sonda Yutsu');
insert into ads (id, title, visurl, content) values (6, 'MILF Svizzere di qualit&agrave;', 'www.milffornothing.it', 'MILF di qualit&agrave; svizzera, prezzo tailandese !');
insert into ads (id, title, visurl, content) values (7, 'Amuleti anti Wakko‎‎', 'www.amuletiwakkosi.it', 'Tieni lontani i wakki da questo forum, garantiti al 100%');
insert into ads (id, title, visurl, content) values (8, 'Cydonia', 'www.sitovuoto.it', 'Esplora l''incantanto mondo di Cydonia a bordo dell''Argent !');
insert into ads (id, title, visurl, content) values (9, 'Lozioni per capelli Yoda', 'www.lozionimiracolose.it', 'Per una pelata lucida e splendente !');

insert into sysinfo (`key`, `value`) values ('messages.total', '6');
insert into sysinfo (`key`, `value`) values ('threads.total', '4');

insert into sysinfo (`key`, `value`) values ('messages.forum', '1');
insert into sysinfo (`key`, `value`) values ('threads.forum', '1');

insert into sysinfo (`key`, `value`) values ('messages.forum.Forum iniziale', '3');
insert into sysinfo (`key`, `value`) values ('threads.forum.Forum iniziale', '1');

insert into sysinfo (`key`, `value`) values ('messages.forum.Procura Svizzera', '2');
insert into sysinfo (`key`, `value`) values ('threads.forum.Procura Svizzera', '2');