package com.usermgmt.usermgmt.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "user.events.exchange";
    public static final String REG_ROUTE = "user.registered";
    public static final String LOGIN_ROUTE = "user.loggedin";

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue registeredQueue() {
        return new Queue("user.registered.queue");
    }

    @Bean
    public Queue loginQueue() {
        return new Queue("user.loggedin.queue");
    }

    @Bean
    public Binding registeredBinding(Queue registeredQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(registeredQueue).to(userExchange).with(REG_ROUTE);
    }

    @Bean
    public Binding loginBinding(Queue loginQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(loginQueue).to(userExchange).with(LOGIN_ROUTE);
    }
}
