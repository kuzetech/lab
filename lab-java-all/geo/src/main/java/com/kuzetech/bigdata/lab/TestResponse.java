package com.kuzetech.bigdata.lab;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maxmind.db.MaxMindDbConstructor;
import com.maxmind.db.MaxMindDbParameter;
import com.maxmind.geoip2.model.AbstractCityResponse;
import com.maxmind.geoip2.record.*;

import java.util.ArrayList;

public final class TestResponse extends AbstractCityResponse {
    @MaxMindDbConstructor
    public TestResponse(
            @JsonProperty("city") @MaxMindDbParameter(name = "city") City city,
            @JsonProperty("continent") @MaxMindDbParameter(name = "continent") Continent continent,
            @JsonProperty("country") @MaxMindDbParameter(name = "country") Country country,
            @JsonProperty("maxmind") @MaxMindDbParameter(name = "maxmind") MaxMind maxmind,
            @JsonProperty("postal") @MaxMindDbParameter(name = "postal") Postal postal,
            @JsonProperty("registered_country") @MaxMindDbParameter(name = "registered_country") Country registeredCountry,
            @JsonProperty("represented_country") @MaxMindDbParameter(name = "represented_country") RepresentedCountry representedCountry,
            @JsonProperty("subdivisions") @MaxMindDbParameter(name = "subdivisions") ArrayList<Subdivision> subdivisions,
            @JacksonInject("traits") @JsonProperty("traits") @MaxMindDbParameter(name = "traits") Traits traits
    ) {
        super(city, continent, country, null, maxmind, postal, registeredCountry,
                representedCountry, subdivisions, traits);
    }
}
