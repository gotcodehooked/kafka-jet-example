package com.promjet.common.processor;

import com.google.gson.Gson;
import com.promjet.common.messages.Message;

public class MessageConverter {
    private final Gson gson = new Gson();


    public String exctractCode(String data){
        return gson.fromJson(data, Message.class).getCode();
    }

    public <T extends  Message> T extractMessage(String data,Class<T> clazz){
        return gson.fromJson(data,clazz);

    }

    public String toJson(Object message){
        return  gson.toJson(message);
    }
}
