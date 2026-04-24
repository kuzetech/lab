select role_main,
       name,
       attack_max
from (select role_main,
             name,
             attack_max,
             row_number() over (partition by role_main ORDER BY attack_max DESC) as rank
      from heros) as h
where rank <= 2
