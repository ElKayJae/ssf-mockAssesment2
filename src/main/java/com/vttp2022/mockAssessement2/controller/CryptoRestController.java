package com.vttp2022.mockAssessement2.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vttp2022.mockAssessement2.model.OrderList;
import com.vttp2022.mockAssessement2.service.CryptoCompareService;
import com.vttp2022.mockAssessement2.service.OrderListRedis;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

@RestController
@RequestMapping(path = "/")
public class CryptoRestController {
    private static final Logger logger = LoggerFactory.getLogger(CryptoRestController.class);

    @Autowired
    CryptoCompareService service;

    @Autowired
    OrderListRedis redisService;
    
    @PostMapping (consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<String> purchaseOrder(@RequestBody String json){

        JsonObject responseJson;
        
        try{
            OrderList orderIn = OrderList.createOrderList(json);
            logger.info("orderIn Fsyms >>>>> " + orderIn.getFsyms());
            Optional<OrderList> optOrder = service.getPrices(orderIn);
            OrderList orderOut = optOrder.get();

            if (optOrder.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            orderOut.calculateTotal();

            redisService.save(orderOut);

            Map<String,Double> totalPrice = orderOut.getTotalPrice();
            JsonObjectBuilder builder = Json.createObjectBuilder()
                                        .add("id", orderOut.getId())
                                        .add("name", orderOut.getName())
                                        .add("email", orderOut.getEmail());
            Set<String> currencyList = totalPrice.keySet();
            logger.info(currencyList.toString());

            for (String currency:currencyList){
                builder.add(currency, totalPrice.get(currency));
            }
            
            responseJson = builder.build();
            

            
        } catch (IOException e){
            JsonObject errJson = Json.createObjectBuilder()
                    .add("error", e.getMessage()).build();
                    return ResponseEntity.status(400).body(errJson.toString());
        }
        
        return ResponseEntity.ok(responseJson.toString());

    }


    @GetMapping ("/{olId}")
    public ResponseEntity<OrderList> getOrderListbyId(@PathVariable String olId){
        logger.info("get" + olId);
        OrderList ol = redisService.findById(olId);
        return ResponseEntity.ok(ol);
    }

    @GetMapping ("/list")
    public ResponseEntity<List<String>> returnList(){
        logger.info("return list");
        OrderList[] allOrderList = redisService.getList();
        List<String> allId = new ArrayList<>();
        for (OrderList orderList:allOrderList){
            allId.add(orderList.getId());
        }
        return ResponseEntity.ok(allId);
    }
}
