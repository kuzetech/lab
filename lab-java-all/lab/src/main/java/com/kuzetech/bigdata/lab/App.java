package com.kuzetech.bigdata.lab;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        File file = new File("/Users/huangsw/code/lab/lab-java-all/lab/src/main/resources/1.json");

        MetadataSyncResponse metadataSyncResponse = objectMapper.readValue(file, MetadataSyncResponse.class);


        MetaData[] metaData = objectMapper.readValue(metadataSyncResponse.getContent(), MetaData[].class);

        String content = objectMapper.writeValueAsString(metaData);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/huangsw/code/lab/lab-java-all/lab/src/main/resources/2.json"))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
