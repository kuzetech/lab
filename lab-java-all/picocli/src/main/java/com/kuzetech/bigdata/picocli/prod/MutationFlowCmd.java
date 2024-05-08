package com.kuzetech.bigdata.picocli.prod;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.Properties;

@Slf4j
@CommandLine.Command(name = "mutation")
public class MutationFlowCmd extends BaseJobCmd implements Runnable {

    public static final String DEFAULT_TOKEN_TTL = "30m";

    @CommandLine.Option(
            names = {"--token-ttl", "-t", "--job.token-ttl"},
            defaultValue = "${sys:job.token-ttl:-${env:JOB_TOKEN-TTL}}",
            description = "token ttl, default value: " + DEFAULT_TOKEN_TTL)
    String tokenTtl;

    @Override
    public void run() {
        Properties properties = new Properties();
        properties.setProperty("kafkaBootstrapServers", this.kafkaBootstrapServers);
        properties.setProperty("tokenTtl", this.tokenTtl);

        log.info("MutationFlowCmd config: {}", properties);
    }

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new MutationFlowCmd());
        // 允许未知参数, 避免传参异常退出.
        cmd.setUnmatchedArgumentsAllowed(true);
        cmd.execute(args);
    }
}
