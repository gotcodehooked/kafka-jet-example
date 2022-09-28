package com.promjet.office.controllers;

import com.promjet.common.bean.AirPort;
import com.promjet.common.bean.Route;
import com.promjet.office.service.PathService;
import com.promjet.office.service.WaitingRoutesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/routes")
public class RouteRest {

    private final WaitingRoutesService waitingRoutesService;
    private final PathService pathService;

    @PostMapping(path = "route")
    public void addRoute(@RequestBody List<String> routeLocations){
        Route route = pathService.convertLocationsToRoute(routeLocations);
        waitingRoutesService.add(route);
    }
}
