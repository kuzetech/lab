package com.kuzetech.bigdata.study;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException {

        FileWriter writer = new FileWriter("/Users/huangsw/code/lab/lab-java-all/logback/logs/1.log");
        for(;;){
            writer.append("{\"type\":\"Event\",\"data\":{\"achievement_count\":54,\"account_chan\":\"\",\"pps_account\":285739036,\"ip_subdivision\":\"福建省\",\"#log_id\":\"4153304062072983552\",\"score\":3975,\"#device_model\":\"vivo V1938T\",\"mac\":\"\",\"online_time\":1491,\"uid\":\"j6dlmp\",\"tap_fpid\":\"202102162031017619f3b2eb2a218f507cfba17d29b44d011f309ca5f34963\",\"score1\":0,\"adult_type\":4,\"game_area\":\"CN\",\"ip_country\":\"中国\",\"cid\":1,\"#account_id\":1159569601,\"level\":106,\"action_name\":\"\",\"phone\":\"\",\"delicious_value\":57080,\"high_big_crate_open_count\":14,\"score2\":0,\"pay_sum\":19,\"last_offline_time\":1691034518,\"max_score\":4053,\"__hostname__\":\"fluent-bit-f552t\",\"friend_count\":36,\"is_show_action\":0,\"permit_type\":2,\"send_candy\":7,\"type\":1,\"#ip\":\"112.49.252.20\",\"card_id\":\"\",\"sex\":0,\"ip_city_geo_name_id\":1810821,\"ip_isp\":\"China Mobile\",\"logid\":\"1691045306-1159569601\",\"ticket\":95,\"platform_id\":0,\"total_time\":840020,\"#server_id\":1,\"ip_subdivision_geo_name_id\":1811017,\"pay_sum_recent_30\":0,\"score4\":0,\"create_time\":1657590896,\"__file__\":\"/data/docker/game_server/s193/log/stat/2023/08/03/2023-08-03.01.log\",\"fan_count\":269,\"popular_value\":10311,\"arena_score\":16,\"server_area\":\"CN\",\"is_tap\":0,\"ip_city\":\"福州市\",\"send_ticket\":164,\"#device_id\":\"c5c0d63f2af718c71ced765c1c358e29\",\"candy\":0,\"vip\":1,\"credit\":0,\"ip_country_geo_name_id\":1814991,\"chan\":\"TapTap\",\"#time\":1691045306000,\"big_crate_open_count\":306,\"club_id\":1597945,\"ispay\":1,\"nick\":\"LYL.情色\",\"guest\":0,\"account\":\"286352571\",\"is_fcm\":0,\"action_id\":0,\"ip_country_iso\":\"CN\",\"#event\":\"gameserver_login\"}}\n");
            Thread.sleep(200);
        }

    }
}
