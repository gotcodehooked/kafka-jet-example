package com.promjet.common.processor;

import com.promjet.common.messages.Message;

public interface MessageProcessor<T extends Message> {

    void process(String jsonMessage);
}
