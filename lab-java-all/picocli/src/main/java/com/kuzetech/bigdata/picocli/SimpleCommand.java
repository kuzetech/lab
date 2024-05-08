package com.kuzetech.bigdata.picocli;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;

@Slf4j
@Command(name = "checksum", mixinStandardHelpOptions = true, version = "1.0.0",
        description = "Prints the checksum (SHA-256 by default) of a file to STDOUT.")
class SimpleCommand implements Runnable {

    @Spec
    public CommandSpec spec;

    @Parameters(index = "0", description = "The file whose checksum to calculate.")
    private File file;

    @Option(names = {"-a", "--algorithm"}, description = "MD5, SHA-1, SHA-256, ...")
    private String algorithm = "SHA-256";

    @Override
    public void run() {
        try {
            byte[] fileContents = Files.readAllBytes(file.toPath());
            byte[] digest = MessageDigest.getInstance(algorithm).digest(fileContents);
            System.out.printf("%0" + (digest.length * 2) + "x%n", new BigInteger(1, digest));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String... args) {
        String[] arguments = {"-a", "MD5", "/Users/huangsw/code/lab/lab-java-all/picocli/src/main/resources/log4j2.xml"};
        int exitCode = new CommandLine(new SimpleCommand()).execute(arguments);
        System.exit(exitCode);
    }
}