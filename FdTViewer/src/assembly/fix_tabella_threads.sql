-- ogni tanto qualche thread non finisce in threads
-- quelli in freeban ad esempio
-- questa query sana la situazione
-- finche' non sistemiamo il codice
-- utile anche in altri casi

-- n.b. la tabella threads non ha constraints
-- quindi attenzione a non inserire duplicati

insert into threads
select threadId, max(id)
from messages
where not exists (select threadId from threads)
group by threadId
;

-- verificare duplicati

select threadId, count(*)
from threads
group by threadId
having count(*) > 1
;

-- se le cose sono spaccate del tutto

delete from threads;

insert into threads
select threadid, max(id)
from messages
group by threadid
;
