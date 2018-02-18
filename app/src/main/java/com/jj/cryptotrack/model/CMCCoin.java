package com.jj.cryptotrack.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CMCCoin implements Parcelable {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("rank")
    private String rank;
    @JsonProperty("price_usd")
    private String price_usd;
    @JsonProperty("price_btc")
    private String price_btc;
    @JsonProperty("24h_volume_usd")
    private String volume_usd_24h;
    @JsonProperty("market_cap_usd")
    private String market_cap_usd;
    @JsonProperty("available_supply")
    private String available_supply;
    @JsonProperty("total_supply")
    private String total_supply;
    @JsonProperty("max_supply")
    private String max_supply;
    @JsonProperty("percent_change_1h")
    private String percent_change_1h;
    @JsonProperty("percent_change_24h")
    private String percent_change_24h;
    @JsonProperty("percent_change_7d")
    private String percent_change_7d;
    @JsonProperty("last_updated")
    private String last_updated;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getRank() {
        return rank;
    }

    public String getPrice_usd() {
        return price_usd;
    }

    public String getPrice_btc() {
        return price_btc;
    }

    public String getVolume_usd_24h() {
        return volume_usd_24h;
    }

    public String getMarket_cap_usd() {
        return market_cap_usd;
    }

    public String getAvailable_supply() {
        return available_supply;
    }

    public String getTotal_supply() {
        return total_supply;
    }

    public String getMax_supply() {
        return max_supply;
    }

    public String getPercent_change_1h() {
        return percent_change_1h;
    }

    public String getPercent_change_24h() {
        return percent_change_24h;
    }

    public String getPercent_change_7d() {
        return percent_change_7d;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setPrice_usd(String price_usd) {
        this.price_usd = price_usd;
    }

    public void setPrice_btc(String price_btc) {
        this.price_btc = price_btc;
    }

    public void setVolume_usd_24h(String volume_usd_24h) {
        this.volume_usd_24h = volume_usd_24h;
    }

    public void setMarket_cap_usd(String market_cap_usd) {
        this.market_cap_usd = market_cap_usd;
    }

    public void setAvailable_supply(String available_supply) {
        this.available_supply = available_supply;
    }

    public void setTotal_supply(String total_supply) {
        this.total_supply = total_supply;
    }

    public void setMax_supply(String max_supply) {
        this.max_supply = max_supply;
    }

    public void setPercent_change_1h(String percent_change_1h) {
        this.percent_change_1h = percent_change_1h;
    }

    public void setPercent_change_24h(String percent_change_24h) {
        this.percent_change_24h = percent_change_24h;
    }

    public void setPercent_change_7d(String percent_change_7d) {
        this.percent_change_7d = percent_change_7d;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.symbol);
        dest.writeString(this.rank);
        dest.writeString(this.price_usd);
        dest.writeString(this.price_btc);
        dest.writeString(this.volume_usd_24h);
        dest.writeString(this.market_cap_usd);
        dest.writeString(this.available_supply);
        dest.writeString(this.total_supply);
        dest.writeString(this.max_supply);
        dest.writeString(this.percent_change_1h);
        dest.writeString(this.percent_change_24h);
        dest.writeString(this.percent_change_7d);
        dest.writeString(this.last_updated);
    }

    public CMCCoin() {
    }

    protected CMCCoin(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.symbol = in.readString();
        this.rank = in.readString();
        this.price_usd = in.readString();
        this.price_btc = in.readString();
        this.volume_usd_24h = in.readString();
        this.market_cap_usd = in.readString();
        this.available_supply = in.readString();
        this.total_supply = in.readString();
        this.max_supply = in.readString();
        this.percent_change_1h = in.readString();
        this.percent_change_24h = in.readString();
        this.percent_change_7d = in.readString();
        this.last_updated = in.readString();
    }

    public static final Creator<CMCCoin> CREATOR = new Creator<CMCCoin>() {
        @Override
        public CMCCoin createFromParcel(Parcel source) {
            return new CMCCoin(source);
        }

        @Override
        public CMCCoin[] newArray(int size) {
            return new CMCCoin[size];
        }
    };
}
