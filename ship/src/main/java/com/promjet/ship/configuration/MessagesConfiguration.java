package com.promjet.ship.configuration;

import com.promjet.common.processor.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagesConfiguration {


    @Bean
    public MessageConverter converter(){
    return new MessageConverter();
    }
}
