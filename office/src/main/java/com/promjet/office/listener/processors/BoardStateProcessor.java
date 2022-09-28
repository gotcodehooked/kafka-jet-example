package com.promjet.office.listener.processors;

import com.promjet.common.bean.AirPort;
import com.promjet.common.bean.Board;
import com.promjet.common.bean.Route;
import com.promjet.common.messages.AirPortStateMessage;
import com.promjet.common.messages.BoardStateMessage;
import com.promjet.common.processor.MessageConverter;
import com.promjet.common.processor.MessageProcessor;
import com.promjet.office.provider.AirPortsProvider;
import com.promjet.office.provider.BoardsProvider;
import com.promjet.office.service.WaitingRoutesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component("BOARD_STATE")
@RequiredArgsConstructor
public class BoardStateProcessor implements MessageProcessor<BoardStateMessage> {

    private final MessageConverter messageConverter;
    private final WaitingRoutesService waitingRoutesService;
    private final BoardsProvider boardsProvider;
    private final AirPortsProvider airPortsProvider;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void process(String jsonMessage) {

        BoardStateMessage message = messageConverter.extractMessage(jsonMessage, BoardStateMessage.class);
        Board board = message.getBoard();
        Optional<Board> previousOpt = boardsProvider.getBoard(board.getName());
        AirPort airPort = airPortsProvider.getAirPort(board.getLocation());

        boardsProvider.addBoard(board);

        if (previousOpt.isPresent() && board.hasRoute() && previousOpt.get().hasRoute()) {
            Route route = board.getRoute();
            waitingRoutesService.remove(route);
        }

        if (previousOpt.isEmpty() || !board.isBusy() && previousOpt.get().isBusy()) {
            airPort.addBoard(board.getName());
            kafkaTemplate.sendDefault(messageConverter.toJson(new AirPortStateMessage(airPort)));
        }
    }
}