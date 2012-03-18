-- messaggi per forum
INSERT INTO sysinfo(`key`, `value`) 
SELECT concat('messages.forum.', coalesce(forum, '')), count(id) FROM messages 
GROUP BY forum;

-- threads per forum
INSERT INTO sysinfo(`key`, `value`) 
SELECT concat('threads.forum.', coalesce(forum, '')), count(id) FROM messages 
WHERE id = threadId
GROUP BY forum;

-- messaggi totali
INSERT INTO sysinfo(`key`, `value`)
SELECT 'messages.total', count(id) FROM messages;

-- threads totali
INSERT INTO sysinfo(`key`, `value`)
SELECT 'threads.total', count(id) FROM messages
WHERE id = threadId;
