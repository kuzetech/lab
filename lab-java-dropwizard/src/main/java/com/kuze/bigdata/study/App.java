package com.kuze.bigdata.study;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.health.check.http.HttpHealthCheck;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App extends Application<Configuration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @Override
    public void initialize(Bootstrap<Configuration> b) {
    }

    @Override
    public void run(Configuration c, Environment e) throws Exception {
        LOGGER.info("Registering REST resources");
        e.jersey().register(new EmployeeRESTController(e.getValidator()));
        DefaultServerFactory serverFactory = (DefaultServerFactory) c.getServerFactory();
        HttpConnectorFactory connectorFactory = (HttpConnectorFactory) serverFactory.getApplicationConnectors().get(0);
        int httpPort = connectorFactory.getPort();
        LOGGER.info("HttpHealthCheck Port is {}", httpPort);
        e.healthChecks().register("http-check", new HttpHealthCheck("http://localhost:" + httpPort + "/employees/1"));
    }

    public static void main(String[] args) throws Exception {
        // 基本上只会使用 server 模式
        List<String> configs = new ArrayList<>();
        configs.add("server");
        configs.addAll(Arrays.asList(args));
        new App().run(configs.toArray(new String[0]));
        // 下面的语句并不会打印，说明主线程被阻塞了
        System.out.println("------------------------------------");
    }
}
