SELECT *
from (SELECT u.`#user_id`                                                        as identify,
             u.`#first_chan_time`                                                as first_chan_time,
             u.`#last_chan_time`                                                 as last_chan_time,
             COALESCE(u.`#first_chan`, '')                                       as first_chan,
             COALESCE(u.`#last_chan`, '')                                        as last_chan,
             COALESCE(e.first_active_time, u.last_account_first_chan_time)       as e_first_chan_time,
             COALESCE(e.last_active_time, u.last_account_last_chan_time)         as e_last_chan_time,
             COALESCE(e.first_active_platform, u.last_account_first_chan)        as e_first_chan,
             COALESCE(e.last_active_platform, u.last_account_last_chan)          as e_last_chan,
             COALESCE(e.first_advertiser_id, u.last_account_first_advertiser_id) as e_first_advertiser_id,
             COALESCE(e.last_advertiser_id, u.last_account_last_advertiser_id)   as e_last_advertiser_id,
             COALESCE(e.first_ad_id, u.last_account_first_ad_id)                 as e_first_ad_id,
             COALESCE(e.last_ad_id, u.last_account_last_ad_id)                   as e_last_ad_id,
             COALESCE(e.first_campaign_id, u.last_account_first_campaign_id)     as e_first_campaign_id,
             COALESCE(e.last_campaign_id, u.last_account_last_campaign_id)       as e_last_campaign_id,
             COALESCE(e.first_creative_id, u.last_account_first_creative_id)     as e_first_creative_id,
             COALESCE(e.last_creative_id, u.last_account_last_creative_id)       as e_last_creative_id,
             COALESCE(e.first_creative_name, u.last_account_first_creative_name) as e_first_creative_name,
             COALESCE(e.last_creative_name, u.last_account_last_creative_name)   as e_last_creative_name
      FROM (select account,
                   `#user_id`,
                   `#first_chan`,
                   `#last_chan`,
                   `#first_chan_time`,
                   `#last_chan_time`,
                   last_account_first_chan,
                   last_account_last_chan,
                   last_account_first_chan_time,
                   last_account_last_chan_time,
                   last_account_first_advertiser_id,
                   last_account_last_advertiser_id,
                   last_account_first_ad_id,
                   last_account_last_ad_id,
                   last_account_first_campaign_id,
                   last_account_last_campaign_id,
                   last_account_first_creative_id,
                   last_account_last_creative_id,
                   last_account_first_creative_name,
                   last_account_last_creative_name
            from (select coalesce(nullIf(`#open_id`, ''), account)                                 AS account,
                         `#user_id`,
                         `#user_create_time`,
                         `#first_chan`,
                         `#last_chan`,
                         `#first_chan_time`,
                         `#last_chan_time`,
                         first_value(`#first_chan`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_first_chan,
                         first_value(`#last_chan`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_last_chan,
                         first_value(`#first_chan_time`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_first_chan_time,
                         first_value(`#last_chan_time`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_last_chan_time,
                         first_value(`#first_advertiser_id`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_first_advertiser_id,
                         first_value(`#last_advertiser_id`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_last_advertiser_id,
                         first_value(`#first_ad_id`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_first_ad_id,
                         first_value(`#last_ad_id`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_last_ad_id,
                         first_value(`#first_campaign_id`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_first_campaign_id,
                         first_value(`#last_campaign_id`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_last_campaign_id,
                         first_value(`#first_creative_id`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_first_creative_id,
                         first_value(`#last_creative_id`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_last_creative_id,
                         first_value(`#first_creative_name`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_first_creative_name,
                         first_value(`#last_creative_name`)
                                     OVER (PARTITION BY account ORDER BY `#user_create_time` DESC) AS last_account_last_creative_name
                  FROM users_v) as tu
            where (`#first_chan` is null or `#last_chan` is null)
              AND (`#user_create_time` BETWEEN 1777564800000 AND 1780243200000)) AS u
               left JOIN
           (select coalesce(nullIf(`#open_id`, ''), account)           AS account,
                   min(turbo_active_time)                              AS first_active_time,
                   max(turbo_active_time)                              AS last_active_time,
                   CASE
                       WHEN argMin(ad_platform, turbo_active_time) = '' THEN 'natural'
                       ELSE argMin(ad_platform, turbo_active_time) END AS first_active_platform,
                   CASE
                       WHEN argMax(ad_platform, turbo_active_time) = '' THEN 'natural'
                       ELSE argMax(ad_platform, turbo_active_time) END AS last_active_platform,
                   argMin(advertiser_id, turbo_active_time)            AS first_advertiser_id,
                   argMax(advertiser_id, turbo_active_time)            AS last_advertiser_id,
                   argMin(ad_id, turbo_active_time)                    AS first_ad_id,
                   argMax(ad_id, turbo_active_time)                    AS last_ad_id,
                   argMin(campaign_id, turbo_active_time)              AS first_campaign_id,
                   argMax(campaign_id, turbo_active_time)              AS last_campaign_id,
                   argMin(creative_id, turbo_active_time)              AS first_creative_id,
                   argMax(creative_id, turbo_active_time)              AS last_creative_id,
                   argMin(creative_name, turbo_active_time)            AS first_creative_name,
                   argMax(creative_name, turbo_active_time)            AS last_creative_name
            from events
            where `#event` = 'account_create_by_yinli'
              AND `#dt` >= '2026-05-01'
            GROUP BY account) AS e
           ON u.account = e.account) as r
where e_last_chan is not null