package com.kuzetech.bigdata.picocli.subcommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Locale;

@Command(name = "country",
        description = "Resolves ISO country codes (ISO-3166-1)")
class CountryResolver implements Runnable {

    @Parameters(arity = "1..*", paramLabel = "<countryCode>", description = "country code(s) to be resolved")
    private String[] countryCodes;

    @Override
    public void run() {
        for (String code : countryCodes) {
            System.out.printf("%s: %s",
                    code.toUpperCase(), new Locale("", code).getDisplayCountry());
        }
    }
}
