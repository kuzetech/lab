SET 'execution.checkpointing.interval' = '4s';

CREATE FUNCTION ip2Address AS 'com.xmfunny.flink.IPToAddressFunction';

SELECT ip2Address('218.107.213.115', 'country') as country;