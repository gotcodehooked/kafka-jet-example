package com.promjet.common.messages;

import com.promjet.common.bean.AirPort;
import com.promjet.common.bean.Board;
import com.promjet.common.bean.Source;
import com.promjet.common.bean.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardStateMessage extends Message {

    private Board board;

    public BoardStateMessage() {

        this.source = Source.BOARD;
        this.type = Type.STATE;
    }

    public BoardStateMessage(Board board) {
        this();
        this.board = board;
    }
}
