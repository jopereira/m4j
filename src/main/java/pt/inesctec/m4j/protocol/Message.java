package pt.inesctec.m4j.protocol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
        @JsonProperty(required = true) String src,
        @JsonProperty(required = true) String dest,
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonProperty(required = true)
        Object body
) {
}
