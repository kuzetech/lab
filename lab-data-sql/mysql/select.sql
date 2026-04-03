SELECT 'test' as constant_str_column, 123 as constant_number_column;

SELECT * from heros;


SELECT DISTINCT player_id, player_name, count(*) as num #顺序5
FROM player JOIN team ON player.team_id = team.team_id #顺序1
WHERE height > 1.80 #顺序2
GROUP BY player.team_id #顺序3
HAVING num > 2 #顺序4
ORDER BY num DESC #顺序6
LIMIT 2; #顺序7

## %代表0个或多个字符，_则必须存在一个字符
SELECT name FROM heros WHERE name LIKE '%太%';
SELECT name FROM heros WHERE name LIKE '_%太%';



