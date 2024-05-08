package com.kuzetech.bigdata.picocli.prod;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.Properties;

@Slf4j
@CommandLine.Command(name = "alive")
public class AliveFlowCmd extends BaseJobCmd implements Runnable {

    @CommandLine.Option(
            names = {"--job.keepalive-window"},
            defaultValue = "${sys:job.keepalive-window:-${env:JOB_KEEPALIVE-WINDOW}}")
    String keepaliveWindow;

    @Override
    public void run() {
        Properties properties = new Properties();
        properties.setProperty("kafkaBootstrapServers", this.kafkaBootstrapServers);
        properties.setProperty("keepaliveWindow", this.keepaliveWindow);

        log.info("AliveFlowCmd config: {}", properties);
    }

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new AliveFlowCmd());
        // 允许未知参数, 避免传参异常退出.
        cmd.setUnmatchedArgumentsAllowed(true);
        cmd.execute(args);
    }
}