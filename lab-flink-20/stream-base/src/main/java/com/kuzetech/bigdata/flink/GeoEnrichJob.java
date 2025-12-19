package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.service.GeoService;
import com.maxmind.geoip2.DatabaseReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.net.InetAddress;

/**
 * 使用这种方式初始化 geo 库
 * 好处是      仅初始化一次库，并且所有的任务共同依赖，节省内存
 * 需要注意    由于 db 文件缓存在 OS Page 中，这部分的内存 flink 是不会统计的，所以初始化 TM 时需要多分配一些内存
 */

@Slf4j
public class GeoEnrichJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        env.fromData("47.115.22.115")
                .map(new GeoEnrichFunction())
                .print()
                .setParallelism(1);

        env.execute();
    }

    public static class GeoEnrichFunction extends RichMapFunction<String, String> {
        private transient DatabaseReader cityReader;

        @Override
        public void open(Configuration parameters) throws Exception {
            log.info("执行 open 方法");
            this.cityReader = GeoService.getDatabaseReader();
        }

        @Override
        public String map(String ip) {
            // 3. 使用 reader 查询 IP 信息
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                return cityReader.city(ipAddress).getCity().getName();
            } catch (Exception e) {
                log.error(e.getMessage());
                return "unknown";
            }
        }

        @Override
        public void close() throws Exception {
            // 4. 记得关闭资源
            GeoService.close();
        }
    }
}
