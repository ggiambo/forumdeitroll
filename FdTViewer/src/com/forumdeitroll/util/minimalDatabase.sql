insert into authors (nick, messages, password, salt, hash) values ('admin', 1, '', 'e0342c65575601d6', 'c27a20319df29b1ab0e9046defbc192534e5a2e373b08045');

insert into preferences (nick, `key`, `value`) values ('admin', 'super', 'yes');

insert into messages (id, text, date, subject, threadid, parentid, author, forum, visible, rank) values (1, 'qui comando io $cool', sysdate, 'benvenuto nel fdt !', 1, 1, 'admin', 'Forum iniziale', 'true', 0);

insert into sysinfo (`key`, `value`) values ('messages.total', '1');

insert into sysinfo (`key`, `value`) values ('threads.total', '1');

insert into sysinfo (`key`, `value`) values ('messages.forum.Forum iniziale', '1');

insert into sysinfo (`key`, `value`) values ('threads.forum.Forum iniziale', '1');