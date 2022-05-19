package dto.response;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "original",
        "metric",
        "us"
})
@Generated("jsonschema2pojo")
public class Measures {

    @JsonProperty("original")
    private Original original;
    @JsonProperty("metric")
    private Metric metric;
    @JsonProperty("us")
    private Us us;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("original")
    public Original getOriginal() {
        return original;
    }

    @JsonProperty("original")
    public void setOriginal(Original original) {
        this.original = original;
    }

    @JsonProperty("metric")
    public Metric getMetric() {
        return metric;
    }

    @JsonProperty("metric")
    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    @JsonProperty("us")
    public Us getUs() {
        return us;
    }

    @JsonProperty("us")
    public void setUs(Us us) {
        this.us = us;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}