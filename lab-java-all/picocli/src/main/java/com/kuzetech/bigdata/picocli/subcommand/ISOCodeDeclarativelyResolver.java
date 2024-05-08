package com.kuzetech.bigdata.picocli.subcommand;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "ISOCodeResolver",
        subcommands = {
                CountryResolver.class,
                LanguageResolver.class,
                CommandLine.HelpCommand.class
        },
        description = "Resolves ISO country codes (ISO-3166-1) or language codes (ISO 639-1/-2)")
public class ISOCodeDeclarativelyResolver {

    public static void main(String[] args) {
        String[] arguments = {"country", "cn"};
        int exitCode = new CommandLine(new ISOCodeDeclarativelyResolver()).execute(arguments);
        System.exit(exitCode);
    }

}
