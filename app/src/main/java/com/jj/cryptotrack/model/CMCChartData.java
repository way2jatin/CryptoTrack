package com.jj.cryptotrack.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CMCChartData {

    @JsonProperty("price_btc")
    List<List<Float>> priceBTC;
    @JsonProperty("price_usd")
    List<List<Float>> priceUSD;


    public List<List<Float>> getPriceBTC() {
        return priceBTC;
    }

    public List<List<Float>> getPriceUSD() {
        return priceUSD;
    }

    public void setPriceBTC(List<List<Float>> priceBTC) {
        this.priceBTC = priceBTC;
    }

    public void setPriceUSD(List<List<Float>> priceUSD) {
        this.priceUSD = priceUSD;
    }
}
