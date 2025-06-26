package com.kuzetech.bigdata.flink.fake;

import net.datafaker.Faker;
import net.datafaker.providers.base.Options;

public class FakeUtil {

    public enum Event {
        LOGIN, CHARGE, PLAY
    }

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

    public static Event generateEvent() {
        return GLOBAL_FAKER_OPTIONS.option(Event.class);
    }

}
