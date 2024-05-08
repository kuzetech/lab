package com.kuzetech.bigdata.picocli.subcommand;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "ISOCodeResolver",
        description = "Resolves ISO country codes (ISO-3166-1) or language codes (ISO 639-1/-2)")
public class ISOCodeProgrammaticallyResolver {

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new ISOCodeProgrammaticallyResolver())
                .addSubcommand("country", new CountryResolver())
                .addSubcommand("language", new LanguageResolver());
        String[] arguments = {"country", "cn"};
        int exitCode = commandLine.execute(arguments);
        System.exit(exitCode);
    }

}
