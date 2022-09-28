package com.promjet.office.listener;

import com.github.benmanes.caffeine.cache.Cache;
import com.promjet.common.messages.Message;
import com.promjet.common.processor.MessageConverter;
import com.promjet.common.processor.MessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final MessageConverter messageConverter;
    private final Cache<String, WebSocketSession> sessionCache;

    @Autowired
    private Map<String, MessageProcessor<? extends Message>> processors = new HashMap<>();

    @KafkaListener(id = "OfficeGroupId", topics = "officeRoutes")
    private void kafkaListen(String data) {
        sendKafkaMessageToSocket(data);

        String code = messageConverter.exctractCode(data);

       // log.error(" PROC SIZE" +  processors.size());


       processors.forEach((k, v) -> log.error("Key : " + k + ", Value : " + v));

        try {
            processors.get(code).process(data);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private void sendKafkaMessageToSocket(String data) {
        sessionCache.asMap().values().forEach(webSocketSession -> {
            if (webSocketSession.isOpen()) {
                try {
                    webSocketSession.sendMessage(new TextMessage(data));
                } catch (IOException e) {
                    log.error(e.getLocalizedMessage());
                }
            }
        });
    }
}
