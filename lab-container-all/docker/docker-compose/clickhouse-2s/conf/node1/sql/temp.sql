SELECT
  *
FROM
  (
    SELECT
      *,
      row_number() over(
        partition by event_index
        order by
          total_amount desc
      ) as row_number
    FROM
      (
        SELECT
          event_index,
          total_amount,
          COUNT(1) over(partition by event_index) as group_num,
          cast(
            (groupArray(date_time), groupArray(amount)) AS Map(Datetime('Etc/GMT-8'), Float64)
          ) AS data_map
        FROM
          (
            SELECT
              date_time,
              event_index,
              if(amount = inf, 0, amount) as amount,
              SUM(amount) over(partition by event_index) as total_amount
            FROM
              (
                SELECT
                  date_trunc(
                    'day',
                    fromUnixTimestamp64Milli(toInt64(`e_#time`), 'Etc/GMT-8')
                  ) AS date_time,
                  'event_0' AS event_index,
                  round(toFloat64(coalesce(COUNT(1), 0)), 2) AS amount
                FROM
                  (
                    SELECT
                      `event` AS `e_#event`,
                      `time` AS `e_#time`,
                      `screen_height` AS `e_#screen_height`,
                      `dt` AS `e_#dt`
                    FROM
                      events
                  )
                WHERE
                  (
                    `e_#dt` BETWEEN '2022-05-01'
                    AND '2022-05-09'
                  )
                  AND (`e_#event` IN ('#account_login'))
                GROUP BY
                  date_trunc(
                    'day',
                    fromUnixTimestamp64Milli(toInt64(`e_#time`), 'Etc/GMT-8')
                  )
              )
            GROUP BY
              date_time,
              event_index,
              amount
          )
        GROUP BY
          event_index,
          total_amount
      )
  )
WHERE
  row_number < 1000
ORDER BY
  event_index,
  row_number
LIMIT 1000
settings allow_experimental_window_functions = 1, distributed_group_by_no_merge = 1;
