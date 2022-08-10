package com.vttp2022.mockAssessement2.model;

import java.util.Map;

import jakarta.json.JsonObject;

public class Crypto {

    private String symbol;
    private Map<String,Double> price;
    private Double quantity;
    
    public Map<String, Double> getPrice() {
        return price;
    }
    public void setPrice(Map<String, Double> price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public Double getQuantity() {
        return quantity;
    }
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    public static Crypto createCrypto(JsonObject o){
        Crypto crypto = new Crypto();
        crypto.setSymbol(o.get("crypto").toString().replace("\"",""));
        crypto.setQuantity(o.getJsonNumber("quantity").doubleValue());
        return crypto;
    }
}
