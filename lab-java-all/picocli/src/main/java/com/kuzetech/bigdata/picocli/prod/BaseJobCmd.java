package com.kuzetech.bigdata.picocli.prod;

import picocli.CommandLine;

public class BaseJobCmd {
    @CommandLine.Spec
    public CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(
            names = {"--kafka-bootstrap-servers", "-k", "--kafka.bootstrap.servers"},
            defaultValue = "${sys:kafka.bootstrap.servers:-${env:KAFKA_BOOTSTRAP_SERVERS}}",
            description = "kafka bootstrap servers")
    public String kafkaBootstrapServers;

    @Deprecated
    @CommandLine.Option(
            names = {"--input-parallelism", "--job.input-parallelism"},
            defaultValue = "${sys:job.input-parallelism:-${env:JOB_INPUT-PARALLELISM}}",
            description = "input parallelism")
    public Integer inputParallelism;

    @Deprecated
    @CommandLine.Option(
            names = {"--output-parallelism", "--job.output-parallelism"},
            defaultValue = "${sys:job.output-parallelism:-${env:JOB_OUTPUT-PARALLELISM}}",
            description = "output parallelism")
    public Integer outputParallelism;
}
