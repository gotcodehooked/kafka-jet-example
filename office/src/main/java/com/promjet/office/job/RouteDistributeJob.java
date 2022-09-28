package com.promjet.office.job;

import com.promjet.common.bean.AirPort;
import com.promjet.common.bean.Board;
import com.promjet.common.bean.Route;
import com.promjet.common.bean.RoutePath;
import com.promjet.common.messages.AirPortStateMessage;
import com.promjet.common.messages.OfficeRouteMessage;
import com.promjet.common.processor.MessageConverter;
import com.promjet.office.provider.AirPortsProvider;
import com.promjet.office.provider.BoardsProvider;
import com.promjet.office.service.PathService;
import com.promjet.office.service.WaitingRoutesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class RouteDistributeJob {

    private final PathService pathService;
    private final BoardsProvider boardsProvider;
    private final WaitingRoutesService waitingRoutesService;
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final MessageConverter messageConverter;
    private final AirPortsProvider airPortsProvider;

    @Scheduled(initialDelay = 500,fixedDelay = 2500)
    private void distribute(){
        waitingRoutesService
                .routeList()
                .stream()
                .filter(Route::notAssigned)
                .forEach(route -> {

            String startLocation = route.getRoutePaths().get(0).getFrom().getName();

            boardsProvider
                    .getBoards()
                    .stream()
                    .filter(board -> startLocation.equals(board.getLocation()) && board.noBusy())
                    .findFirst()
                    .ifPresent(board -> sendBoardRoute(route,board));

            if(route.notAssigned()){
                boardsProvider.getBoards()
                        .stream()
                        .filter(Board::noBusy)
                        .findFirst()
                        .ifPresent(board -> {
                    String currentLocation = board.getLocation();

                    if(!currentLocation.equals(startLocation)){
                        RoutePath routePath = pathService.makePath(currentLocation,startLocation);
                        route.getRoutePaths().add(0,routePath);
                    }
                    sendBoardRoute(route,board);
                    log.info("ROUTE" + route);
                    log.error("ROUTE" + route);
                });
            }
        });
    }

    private void sendBoardRoute(Route route, Board board){
        route.setBoardName(board.getName());
        AirPort airPort = airPortsProvider.findAirPortAndRemovePort(board.getName());
        board.setLocation(null);
        kafkaTemplate.sendDefault(messageConverter.toJson(new OfficeRouteMessage(route)));
        kafkaTemplate.sendDefault(messageConverter.toJson(new AirPortStateMessage(airPort)));
    }
}
