package com.kuzetech.bigdata.flink17.utils;

import net.datafaker.Faker;
import net.datafaker.providers.base.Options;

public class FakeUtil {

    public enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    private final static Faker GLOBAL_FAKER = new Faker();
    private final static Options GLOBAL_FAKER_OPTIONS = GLOBAL_FAKER.options();

    public static String generateUserName() {
        return GLOBAL_FAKER.name().fullName();
    }

    public static String generateCountry() {
        return GLOBAL_FAKER.country().name();
    }

    public static String generateDeviceModel() {
        return GLOBAL_FAKER.device().modelName();
    }

    public static Day generateDay() {
        return GLOBAL_FAKER_OPTIONS.option(Day.class);
    }

}
