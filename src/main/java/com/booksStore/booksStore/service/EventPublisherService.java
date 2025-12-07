package com.booksStore.booksStore.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventPublisherService {
    private final RabbitTemplate rabbitTemplate;
    private final String exchange = "user.events.exchange";
    private final String registrationRoutingKey = "user.registered";
    private final String loginRoutingKey = "user.loggedin";

    public EventPublisherService(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserRegistered(Map<String,Object> payload){
        rabbitTemplate.convertAndSend(exchange, registrationRoutingKey, payload);
    }

    public void publishUserLoggedIn(Map<String,Object> payload){
        rabbitTemplate.convertAndSend(exchange, loginRoutingKey, payload);
    }
}
