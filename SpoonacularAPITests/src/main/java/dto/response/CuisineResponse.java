package dto.response;

import java.util.HashMap;
import java.util.List;
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
        "cuisine",
        "cuisines",
        "confidence"
})
@Generated("jsonschema2pojo")
public class CuisineResponse {

    @JsonProperty("cuisine")
    private String cuisine;
    @JsonProperty("cuisines")
    private List<String> cuisines = null;
    @JsonProperty("confidence")
    private float confidence;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cuisine")
    public String getCuisine() {
        return cuisine;
    }

    @JsonProperty("cuisine")
    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    @JsonProperty("cuisines")
    public List<String> getCuisines() {
        return cuisines;
    }

    @JsonProperty("cuisines")
    public void setCuisines(List<String> cuisines) {
        this.cuisines = cuisines;
    }

    @JsonProperty("confidence")
    public float getConfidence() {
        return confidence;
    }

    @JsonProperty("confidence")
    public void setConfidence(float confidence) {
        this.confidence = confidence;
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
