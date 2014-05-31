create table threads (threadId int(11), lastId int(11));

insert into threads  select threadid, max(id) from messages group by threadid;
