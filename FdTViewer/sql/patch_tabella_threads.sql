create table threads (threadId int(11), lastId int(11));

insert into threads  select threadid, max(id) from messages group by threadid;

create index idx_threads_threadId on threads(threadId);

create index idx_threads_lastId on threads(lastId);