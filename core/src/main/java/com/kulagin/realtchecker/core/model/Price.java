
package com.kulagin.realtchecker.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "amount",
    "currency",
    "converted"
})
@Getter
@Builder
public class Price {

    @JsonProperty("amount")
    public Double amount;
    @JsonProperty("currency")
    public String currency;
    @JsonProperty("converted")
    public Converted converted;

}
