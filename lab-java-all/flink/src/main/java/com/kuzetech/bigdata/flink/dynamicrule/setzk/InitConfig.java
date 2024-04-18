package com.kuzetech.bigdata.flink.dynamicrule.setzk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;

public class InitConfig {

    private static String test = "public class SausageEvaluable implements com.kuzetech.bigdata.flink.dynamicrule.Evaluable {\n @Override\n public Boolean eval(InputMessage inputMessage) {\n return true;\n }\n \n @Override\n public void process(InputMessage inputMessage) {\n \n }\n }";

    public static void main(String[] args) throws Exception {

        String path = "/kafka-config";
        CuratorFramework zkClient = CuratorFrameworkFactory.newClient("localhost:2181", new RetryOneTime(1000));
        zkClient.start();

        zkClient.create().forPath(path, ("").getBytes());

    }
}
