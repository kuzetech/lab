package com.kuzetech.bigdata.picocli.subcommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Locale;

@Command(name = "language",
        description = "Resolves one or more ISO language codes (ISO-639-1 or 639-2)")
class LanguageResolver implements Runnable {

    @Parameters(arity = "1..*", paramLabel = "<languageCode>", description = "language code(s)")
    private String[] languageCodes;

    @Override
    public void run() {
        for (String code : languageCodes) {
            System.out.printf("%s: %s",
                    code.toLowerCase(), new Locale(code).getDisplayLanguage());
        }
    }
}
