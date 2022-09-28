package com.promjet.office.listener.processors;

import com.promjet.common.messages.AirPortStateMessage;
import com.promjet.common.messages.OfficeStateMessage;
import com.promjet.common.processor.MessageConverter;
import com.promjet.common.processor.MessageProcessor;
import com.promjet.office.provider.AirPortsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("OFFICE_STATE")
@RequiredArgsConstructor
public class OfficeStateProcessor implements MessageProcessor<OfficeStateMessage> {

    private final MessageConverter messageConverter;
    private final AirPortsProvider airPortsProvider;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Override
    public void process(String jsonMessage) {
        airPortsProvider.getPorts().forEach(airPort -> {
            kafkaTemplate.sendDefault(messageConverter.toJson(new AirPortStateMessage(airPort)));
        });
    }
}
