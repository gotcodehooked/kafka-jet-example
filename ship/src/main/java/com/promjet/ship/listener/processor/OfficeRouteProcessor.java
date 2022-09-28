package com.promjet.ship.listener.processor;

import com.promjet.common.bean.Route;
import com.promjet.common.messages.OfficeRouteMessage;
import com.promjet.common.processor.MessageConverter;
import com.promjet.common.processor.MessageProcessor;
import com.promjet.ship.provider.BoardProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component("OFFICE_ROUTE")
@RequiredArgsConstructor
public class OfficeRouteProcessor implements MessageProcessor<OfficeRouteMessage> {

    private final MessageConverter messageConverter;
    private final BoardProvider boardProvider;

    @Override
    public void process(String jsonMessage) {
            OfficeRouteMessage message = messageConverter.extractMessage(jsonMessage,OfficeRouteMessage.class);

            Route route = message.getRoute();
            boardProvider.getBoards().stream()
                    .filter(board -> board.noBusy() && route.getBoardName().equals(board.getName()))
                    .findFirst()
                    .ifPresent(board -> {
                board.setRoute(route);
                board.setBusy(true);
                board.setLocation(null);
            });
    }
}
