package com.promjet.office.service;

import com.promjet.common.bean.Route;
import com.promjet.common.bean.RoutePath;
import com.promjet.common.bean.RoutePoint;
import com.promjet.office.provider.AirPortsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class WaitingRoutesService {

    private final AirPortsProvider airPortsProvider;
    private final Lock lock = new ReentrantLock(true);
    private final List<Route> routeList = new ArrayList<>();

    public List<Route> routeList() {
        return routeList;
    }

    public void add(Route route) {

        try {
            lock.lock();
            routeList.add(route);
        } finally {
            lock.unlock();
        }
    }

    public void remove(Route route) {
        try {
            lock.lock();
            routeList.removeIf(route::equals);
        } finally {
            lock.unlock();
        }
    }

    public Route convertLocationsToRoute(List<String> locations) {

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
            if(row.getFrom() == null){
                row.setFrom(routePoints.get(index));
                if(routePoints.size() > index +1){
                    row.setTo(routePoints.get(index +1));
                }else {
                    row.setTo(routePoints.get(index));
                }
            }
        });
        return route;
    }
}
