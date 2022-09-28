package com.promjet.ship.job;

import com.promjet.common.bean.Board;
import com.promjet.common.bean.RoutePath;
import com.promjet.common.messages.BoardStateMessage;
import com.promjet.common.processor.MessageConverter;
import com.promjet.ship.provider.BoardProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@EnableScheduling
@EnableAsync
@RequiredArgsConstructor
public class BoardsJob {

    private final BoardProvider boardProvider;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageConverter messageConverter;


    @Scheduled(initialDelay = 0, fixedDelay = 250)
    public void fly() {
        boardProvider.getBoards()
                .stream()
                .filter(Board::hasRoute)
                .forEach(board -> {
                    board.getRoute()
                            .getRoutePaths()
                            .stream()
                            .filter(RoutePath::inProgress)
                            .findFirst()
                            .ifPresent(routePath -> {
                                board.calculatePosition(routePath);
                                routePath.addProgress(board.getSpeed());

                                if(routePath.done()){
                                    board.setLocation(routePath.getTo().getName());
                                }
                            });
                });
    }
    @Async
    @Scheduled(initialDelay = 0, fixedDelay = 1000)
    public void notifyState() {
        boardProvider.getBoards().stream().filter(Board::isBusy).forEach(board -> {
            Optional<RoutePath> routePathOpt = board.getRoute().getRoutePaths().stream().filter(RoutePath::inProgress).findAny();

            if (routePathOpt.isEmpty()) {
                List<RoutePath> routePaths = board.getRoute().getRoutePaths();
                board.setLocation(routePaths.get(routePaths.size() - 1).getTo().getName());
                board.setBusy(false);
            }
            BoardStateMessage message = new BoardStateMessage(board);
            kafkaTemplate.sendDefault(messageConverter.toJson(message));
        });
    }
}
