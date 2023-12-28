package com.kuzetech.bigdata.lab;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        Level logLevel = Level.valueOf("info");
        Configurator.setRootLevel(logLevel);

        logger.debug("11111");
        logger.info("22222");
        logger.error("333333");

    }
}
