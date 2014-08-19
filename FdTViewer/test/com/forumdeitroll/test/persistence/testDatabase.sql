insert into authors (nick, messages, password, salt, hash) values ('admin', 2, '', 'e0342c65575601d6', 'c27a20319df29b1ab0e9046defbc192534e5a2e373b08045');
insert into authors (nick, messages, password, salt, hash) values ('Sfigato', 1, '', 'd60ef1248b73b2fb', '2ae5b31efbac8029e3ef2b668fe85a2434a838380d5d538');

insert into quotes (id, nick, content) values (1, 'Sfigato', 'Che la fortuna sia con me !');
insert into quotes (id, nick, content) values (2, 'Sfigato', 'Un quadrifoglio esplosivo ...');
insert into quotes (id, nick, content) values (3, 'admin', 'Il mio forum, il mio tessssoro !');

insert into preferences (nick, `key`, `value`) values ('admin', 'super', 'yes');
insert into preferences (nick, `key`, `value`) values ('Sfigato', 'theme', 'Classico');
insert into preferences (nick, `key`, `value`) values ('Sfigato', 'sidebarStatus', 'hide');
insert into preferences (nick, `key`, `value`) values ('admin', 'hideBannerone', 'checked');
insert into preferences (nick, `key`, `value`) values ('admin', 'hideForum.Procura Svizzera', 'Procura Svizzera');
insert into preferences (nick, `key`, `value`) values ('admin', 'theme', 'Classico');
insert into preferences (nick, `key`, `value`) values ('admin', 'sidebarStatus', 'hide');

insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (1, 'qui comando io $cool', '2010-05-13 14:30:15', 'benvenuto nel fdt !', 1, 1, 'admin', 'Forum iniziale', 'true', 0);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (2, 'Proot !', '2010-05-13 14:35:03', 'benvenuto nel fdt !', 1, 1, null, 'Forum iniziale', 'true', 0);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (3, 'Scritto da: <BR>&gt; Proot !<BR><BR>:@ !', '2010-05-13 14:55:45', 'benvenuto nel fdt !', 1, 2, 'admin', 'Forum iniziale', 'true', 0);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (4, 'Blah Banf montanari orologio a cucu', '2010-05-13 18:03:12', 'Primo !', 4, 4, null, 'Procura Svizzera', 'true', 0);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (5, ':(', '2010-05-14 09:15:54', 'Secondo !', 4, 4, null, 'Procura Svizzera', 'true', 0);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (6, 'Nel Forum Principale', '2010-05-14 12:23:13', 'Nel Forum Principale', 6, 6, null, null, 'true', 0);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (7, 'Ho incontrato yoda. Che ragazzo fortunato :( ...', '2014-08-16 14:54:11', 'Ieri', 7, 7, 'Sfigato', null, 'true', 1);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (8, 'Scritto da: Sfigato<BR>&gt; Ho incontrato yoda. Che ragazzo fortunato :( ...<BR><BR>Mi trovi un lavoro ?<BR><BR>- idyoda -',  '2014-08-16 14:54:46', 'Re: Ieri', 7, 7, null, null, 'true', 0);
insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (9, 'Scritto da: <BR>&gt; Scritto da: Sfigato<BR>&gt; &gt; Ho incontrato yoda. Che ragazzo fortunato :( ...<BR>&gt; <BR>&gt; Mi trovi un lavoro ?<BR>&gt; <BR>&gt; - idyoda -<BR><BR>(rotfl)(rotfl)', '2014-08-16 14:55:14', 'Re: Ieri', 7, 8, 'admin', null, 'true', 0);

insert into tagnames(t_id, `value`) values (1, 'cazzata');
insert into tagnames(t_id, `value`) values (2, 'idyoda');

insert into tags_bind(t_id, m_id, author) values (1, 1,'Sfigato');
insert into tags_bind(t_id, m_id, author) values (2, 9, 'admin');
insert into tags_bind(t_id, m_id, author) values (1, 4, 'admin');

insert into bookmarks(nick, msgid, subject) values ('admin', 1, 'Primissimo messaggio');
insert into bookmarks(nick, msgid, subject) values ('Sfigato', 4, 'Stupido reply');
insert into bookmarks(nick, msgid, subject) values ('Sfigato', 7, 'Mio messaggio');

insert into ads (id, title, visurl, content) values (1, 'Saune Gay‎', 'www.luino.it/stazione/', 'Trova la sauna gay ideale vicino a casa tua !');
insert into ads (id, title, visurl, content) values (2, 'Dildi Giganti', 'www.megadildo.it', 'Regala un sorriso al tuo bucio del cuxo !');
insert into ads (id, title, visurl, content) values (3, 'Suore per tutti‎‎', 'www.suoregratis.it', 'La tua Suora personalizzata, a prezzi imbattibili !');
insert into ads (id, title, visurl, content) values (4, 'M5S Blah Banf‎', 'www.m5sprot.it', 'Generatore automatico di fango da gettare sul M5S');
insert into ads (id, title, visurl, content) values (5, 'Foto pazzesche !', 'www.nuncipossocredere.it', 'Foto hot scattate dalla Sonda Yutsu');
insert into ads (id, title, visurl, content) values (6, 'MILF Svizzere di qualit&agrave;', 'www.milffornothing.it', 'MILF di qualit&agrave; svizzera, prezzo tailandese !');
insert into ads (id, title, visurl, content) values (7, 'Amuleti anti Wakko‎‎', 'www.amuletiwakkosi.it', 'Tieni lontani i wakki da questo forum, garantiti al 100%');
insert into ads (id, title, visurl, content) values (8, 'Cydonia', 'www.sitovuoto.it', 'Esplora l''incantanto mondo di Cydonia a bordo dell''Argent !');
insert into ads (id, title, visurl, content) values (9, 'Lozioni per capelli Yoda', 'www.lozionimiracolose.it', 'Per una pelata lucida e splendente !');

insert into sysinfo (`key`, `value`) values ('messages.total', '9');
insert into sysinfo (`key`, `value`) values ('threads.total', '4');

insert into sysinfo (`key`, `value`) values ('messages.forum', '1');
insert into sysinfo (`key`, `value`) values ('threads.forum', '1');

insert into sysinfo (`key`, `value`) values ('messages.forum.Forum iniziale', '3');
insert into sysinfo (`key`, `value`) values ('threads.forum.Forum iniziale', '1');

insert into sysinfo (`key`, `value`) values ('messages.forum.Procura Svizzera', '2');
insert into sysinfo (`key`, `value`) values ('threads.forum.Procura Svizzera', '2');

insert into threads (threadid, lastid) values (1, 3);
insert into threads (threadid, lastid) values (4, 5);
insert into threads (threadid, lastid) values (6, 6);
insert into threads (threadid, lastid) values (7, 9);