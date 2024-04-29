package com.kuze.bigdata.kstreams.funnypipe.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfUtil {

    public static Properties loadProperties(String propertyFilePath) throws IOException {
        Properties envProps = new Properties();
        try (FileInputStream input = new FileInputStream(propertyFilePath)) {
            envProps.load(input);
            return envProps;
        }
    }

}
