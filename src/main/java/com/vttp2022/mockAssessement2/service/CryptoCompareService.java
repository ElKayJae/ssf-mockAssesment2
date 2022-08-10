package com.vttp2022.mockAssessement2.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.vttp2022.mockAssessement2.model.OrderList;

@Service
public class CryptoCompareService {

    @Value("${crypto.compare.apikey}")
    String apiKey;

    private static final Logger logger = LoggerFactory.getLogger(CryptoCompareService.class);
    String MULTI_SYMBOL_URL ="https://min-api.cryptocompare.com/data/pricemulti";

    public Optional <OrderList> getPrices(OrderList orderList){
       
        logger.info("fsyms >>>>>" + orderList.getFsyms());
        logger.info("tsyms >>>>>" + orderList.getTsyms());

        String cryptoPriceUrl = UriComponentsBuilder.fromUriString(MULTI_SYMBOL_URL)
                                .queryParam("fsyms", orderList.getFsyms())
                                .queryParam("tsyms", orderList.getTsyms())
                                .toUriString();
        
        logger.info(cryptoPriceUrl);

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = null;

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.set("ApiKey", apiKey);
            HttpEntity request = new HttpEntity<>(headers);
            resp = template.exchange(cryptoPriceUrl, HttpMethod.GET, request, String.class);
            logger.info(resp.getBody());
            orderList.setPrices(resp.getBody());
            return Optional.of(orderList);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } return Optional.empty();
    }

     
    
}
