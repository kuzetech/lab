package com.kuzetech.bigdata.json.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
// @JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonAnnotationObject {

    // @JsonIgnore
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;

    @JsonAnySetter
    private Map<String, Object> payload;

    @JsonAnyGetter()
    public Map<String, Object> getPayload() {
        return this.payload;
    }

}
