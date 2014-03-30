INSERT INTO preferences
(`nick`, `key`, `value`)
SELECT nick, 'hideForum.Proc di Catania', 'Proc di Catania'
FROM preferences
WHERE `key` = 'hideProcCatania'
AND `value` = 'checked';

DELETE FROM preferences
WHERE `key` = 'hideProcCatania';