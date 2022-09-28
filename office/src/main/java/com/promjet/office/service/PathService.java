package com.promjet.office.service;

import com.promjet.common.bean.Route;
import com.promjet.common.bean.RoutePath;
import com.promjet.common.bean.RoutePoint;
import com.promjet.office.provider.AirPortsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PathService {
    private final AirPortsProvider airPortsProvider;

    public RoutePath makePath(String from, String to) {
        return new RoutePath(airPortsProvider.getRoutePoint(from), airPortsProvider.getRoutePoint(to), 0);
    }

    public Route convertLocationsToRoute(List<String> locations) {

        log.error("LOCATIONS" + locations.size());
        Route route = new Route();
        List<RoutePath> routePaths = new ArrayList<>();
        List<RoutePoint> routePoints = new ArrayList<>();
        locations.forEach(location -> {
            airPortsProvider.getPorts().stream().filter(airPort -> airPort.getName().equals(location))
                    .findFirst()
                    .ifPresent(airPort -> {
                        routePoints.add(new RoutePoint(airPort));
                    });
        });

        for (int i = 0; i < routePoints.size() - 1; i++) {
            routePaths.add(new RoutePath());
        }

        routePaths.forEach(row -> {
            int index = routePaths.indexOf(row);
            if (row.getFrom() == null) {
                row.setFrom(routePoints.get(index));
                if (routePoints.size() > index + 1) {
                    row.setTo(routePoints.get(index + 1));
                } else {
                    row.setTo(routePoints.get(index));
                }
            }
        });

        route.setRoutePaths(routePaths);
        return route;
    }
}
