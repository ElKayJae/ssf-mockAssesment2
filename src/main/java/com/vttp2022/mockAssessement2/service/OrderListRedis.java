package com.vttp2022.mockAssessement2.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.vttp2022.mockAssessement2.model.OrderList;


@Service
public class OrderListRedis {
    private static final Logger logger = LoggerFactory.getLogger(OrderListRedis.class);

    @Autowired
    @Qualifier("orderListRedisConfig")
    RedisTemplate<String, OrderList> redisTemplate;


    public void save(final OrderList ol) {
        logger.info("Save OrderList > " +  ol.getId());
        redisTemplate.opsForValue().set(ol.getId(), ol);
    }

    public OrderList findById(final String olId) {
        logger.info("find OrderList by id> " + olId);
        OrderList result = (OrderList) redisTemplate.opsForValue().get(olId);
        return result;
    }

    public void update(final OrderList ol) {
        logger.info("Update OrderList > " + ol.getId());
            redisTemplate.opsForValue().setIfPresent(ol.getId(), ol);
           
    }


    public OrderList[] getList() {
        Set<String> allOrderListKeys =  redisTemplate.keys("*");
        List<OrderList> allOrderList = new LinkedList<>();
        for (String orderListKey:allOrderListKeys){
            OrderList result = (OrderList) redisTemplate.opsForValue().get(orderListKey);
            allOrderList.add(result); 
        }
        return allOrderList.toArray(new OrderList[allOrderList.size()]); // [sadasdqw, sdfsdfwfe3...]
    }
    
}
