SELECT
    u.`#user_id` as identify,
    u.`#first_chan_time` as first_chan_time,
    u.`#last_chan_time` as last_chan_time,
    COALESCE(u.`#first_chan`, '') as first_chan,
    COALESCE(u.`#last_chan`, '') as last_chan,
    e.first_active_time as e_first_chan_time,
    e.last_active_time as e_last_chan_time,
    CASE WHEN e.first_active_platform = '' THEN 'natural' ELSE e.first_active_platform END as e_first_chan,
    CASE WHEN e.last_active_platform = '' THEN 'natural' ELSE e.last_active_platform END as e_last_chan,
    e.first_advertiser_id as e_first_advertiser_id,
    e.last_advertiser_id as e_last_advertiser_id,
    e.first_ad_id as e_first_ad_id,
    e.last_ad_id as e_last_ad_id,
    e.first_campaign_id as e_first_campaign_id,
    e.last_campaign_id as e_last_campaign_id,
    e.first_creative_id as e_first_creative_id,
    e.last_creative_id as e_last_creative_id,
    e.first_creative_name as e_first_creative_name,
    e.last_creative_name as e_last_creative_name
FROM
    (
        select
            coalesce(nullIf(`#open_id`, ''), account) AS account,
            `#user_id`,
            `#first_chan`,
            `#last_chan`,
            `#first_chan_time`,
            `#last_chan_time`
        FROM users_v
        WHERE
            (`#first_chan` is null or `#last_chan` is null)
            AND (`#user_create_time` BETWEEN 1778688000000 AND 1778774400000)
    ) AS u
    INNER JOIN
    (
        select
            coalesce(nullIf(`#open_id`, ''), account) AS account,
            min(turbo_active_time) AS first_active_time,
            max(turbo_active_time) AS last_active_time,
            argMin(coalesce(first_chan, ad_platform), turbo_active_time) AS first_active_platform,
            argMax(coalesce(last_chan, ad_platform), turbo_active_time) AS last_active_platform,
            argMin(coalesce(first_funnyads_advertiser_id, advertiser_id), turbo_active_time) AS first_advertiser_id,
            argMax(coalesce(last_funnyads_advertiser_id, advertiser_id), turbo_active_time) AS last_advertiser_id,
            argMin(coalesce(first_funnyads_ad_id, ad_id), turbo_active_time) AS first_ad_id,
            argMax(coalesce(last_funnyads_ad_id, ad_id), turbo_active_time) AS last_ad_id,
            argMin(coalesce(first_funnyads_campaign_id, campaign_id), turbo_active_time) AS first_campaign_id,
            argMax(coalesce(last_funnyads_campaign_id, campaign_id), turbo_active_time) AS last_campaign_id,
            argMin(creative_id, turbo_active_time) AS first_creative_id,
            argMax(creative_id, turbo_active_time) AS last_creative_id,
            argMin(creative_name, turbo_active_time) AS first_creative_name,
            argMax(creative_name, turbo_active_time) AS last_creative_name
        from events
        where `#event` IN ('account_create_by_yinli', 'account_create_by_ads')
        AND`#dt` >= '2026-05-14'
        GROUP BY account
    ) AS e
    ON u.account = e.account
