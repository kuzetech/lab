package com.kuzetech.bigdata.picocli;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
@CommandLine.Command(name = "test", mixinStandardHelpOptions = true, version = "1.0.0",
        description = "test")
public class ParseArgsCommand implements Runnable {

    private final OptionsClass ops;

    public ParseArgsCommand(String[] arguments) {
        ops = new OptionsClass();
        new CommandLine(ops).parseArgs(arguments);
    }

    @Override
    public void run() {
        System.out.println(ops);
    }

    public static void main(String[] args) {
        String[] arguments = {"-c", "--file", "result.tar", "file1.txt", "file2.txt"};
        int exitCode = new CommandLine(new ParseArgsCommand(arguments)).execute();
        System.exit(exitCode);
    }
}
