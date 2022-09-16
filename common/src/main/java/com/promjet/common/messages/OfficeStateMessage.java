package com.promjet.common.messages;

import com.promjet.common.bean.Source;
import com.promjet.common.bean.Type;

public class OfficeStateMessage extends Message {

    public OfficeStateMessage() {
        this.source = Source.OFFICE;
        this.type = Type.ROUTE;
    }
}
