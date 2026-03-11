package com.kuzetech.bigdata.lab;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;

import java.net.InetAddress;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        DatabaseReader reader = new DatabaseReader.Builder(App.class.getResourceAsStream("/maxmind-geoip2-city.mmdb")).build();
        DatabaseReader reader2 = new DatabaseReader.Builder(App.class.getResourceAsStream("/maxmind-geolite2-country_2026-03-05.mmdb")).build();
        InetAddress ipAddress = InetAddress.getByName("91.198.174.192");

        CityResponse response = reader.city(ipAddress);

        Continent continent = response.continent();
        System.out.println(continent);

        Country country = response.country();
        System.out.println(country.name());

        List<Subdivision> subdivisions = response.subdivisions();
        if (subdivisions != null && !subdivisions.isEmpty()) {
            Subdivision subdivision = subdivisions.get(0);
            System.out.println(subdivision.name());
        }

        City city = response.city();
        System.out.println(city.name());

        CountryResponse countryResponse = reader2.country(ipAddress);
        System.out.println(countryResponse.continent());
    }
}
