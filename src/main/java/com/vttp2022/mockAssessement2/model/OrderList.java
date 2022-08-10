package com.vttp2022.mockAssessement2.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

public class OrderList {
    private static final Logger logger = LoggerFactory.getLogger(OrderList.class);

    private List<Crypto> cryptoList = new ArrayList<>();
    private String name;
    private String id;
    private String email;
    private String fsyms;
    private String tsyms;
    private Map<String,Double> totalPrice=new HashMap<>();

    public OrderList(){
        this.id=generateId(8);    
    }

    private synchronized String generateId(int numchars) {
        String generatedId = UUID.randomUUID()
                            .toString()
                            .substring(0,numchars);
        return generatedId;
    }
    
    public Map<String, Double> getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Map<String, Double> totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getFsyms() {
        return fsyms;
    }

    public void setFsyms(String fsyms) {
        this.fsyms = fsyms;
    }

    public String getTsyms() {
        return tsyms;
    }

    public void setTsyms(String tsyms) {
        this.tsyms = tsyms;
    }

    public List<Crypto> getCryptoList() {
        return cryptoList;
    }

    public void setCryptoList(List<Crypto> cryptoList) {
        this.cryptoList = cryptoList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    
    private void addCrypto(Crypto crypto) {
        this.cryptoList.add(crypto);
    }
    
    private void generateFsyms(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Crypto crypto :cryptoList){
            stringBuilder.append(crypto.getSymbol());
            stringBuilder.append(","); 
        }
        String result = stringBuilder.substring(0, stringBuilder.length()-1).replace("\"", "");
        this.fsyms = result;

    }

    public static OrderList createOrderList(String json) throws IOException {
        //{"name":"abc", "email":123@mail.com, "tsyms":"SGD", "cryptoList"=[{"crypto":"symbol", "quantity":"123"},{"crypto":"symbol", "quantity":"123"}...]}
        OrderList orderList = new OrderList();
        try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
            JsonReader reader = Json.createReader(is);
            JsonObject o = reader.readObject();

            orderList.setName(o.getString("name"));
            orderList.setEmail(o.getString("email"));
            orderList.setTsyms(o.getString("tsyms"));

            JsonArray jsonArr = o.getJsonArray("cryptoList");
            for (JsonValue value:jsonArr){
                JsonObject listObject = value.asJsonObject();
                Crypto crypto = Crypto.createCrypto(listObject);
                orderList.addCrypto(crypto);
            }

            orderList.generateFsyms();

            return orderList;
        }
    }

    public void setPrices(String json) throws IOException {

        try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
            JsonReader reader = Json.createReader(is);
            JsonObject o = reader.readObject();
            logger.info("JsonObject >>>>>> " +o);
            for (Crypto crypto: cryptoList){
                logger.info("crpyto name >>>>>>  " + crypto.getSymbol());
                String crpytoSymbol = crypto.getSymbol();
                JsonObject cryptoObj =  o.getJsonObject(crpytoSymbol);
                logger.info(cryptoObj.toString());
                Set<String> cryptoSym = cryptoObj.keySet();
                Map<String,Double> symbolPrice = new HashMap<>();
                for (String sym: cryptoSym){
                    symbolPrice.put(sym, cryptoObj.getJsonNumber(sym).doubleValue());
                }
                crypto.setPrice(symbolPrice);
            }
        }
    }

    public void calculateTotal(){
        Set<String> currencies = cryptoList.get(0).getPrice().keySet();
        logger.info("currency list >>>>>>>> " + currencies);
        for (String currency:currencies){
            Double eachCurrencyPrice= 0d;
            for (Crypto crypto:cryptoList){
                Map<String,Double> cryptoPrices = crypto.getPrice();
                Double price = cryptoPrices.get(currency);
                Double orderPrice = price * crypto.getQuantity().doubleValue();
                eachCurrencyPrice += orderPrice;
            }
            this.totalPrice.put(currency, eachCurrencyPrice);
        }
    }



                    // .stream()
                    // .map(v -> (JsonObject) v)
                    // .forEach((JsonObject v) -> {
                    //     Item item = new Item();
                    //     item.setItemName(v.getString("item"));
                    //     item.setQuantity(v.getInt("quantity"));
                    //     itemsQuote.add(v.getString("item"));
                    //     itemsList.add(item);
                    // }
                    // )

}
    
        
        





    
