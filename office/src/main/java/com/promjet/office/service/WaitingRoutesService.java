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
}
