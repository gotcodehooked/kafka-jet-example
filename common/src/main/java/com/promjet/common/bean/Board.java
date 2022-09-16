package com.promjet.common.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Board {

    private String name;
    private String location;
    private Route route;
    private boolean busy;
    private double speed;
    private double x;
    private double y;
    private double angle;

    private boolean noBusy() {
        return !busy;
    }

    private void calculatePosition(RoutePath routePath) {
        double t = routePath.getProgress() / 100;

        double toX = (1 - t) * routePath.getFrom().getX() + t * routePath.getTo().getX();
        double toY = (1 - t) * routePath.getFrom().getY() + t * routePath.getTo().getY();

        double deltaX = this.x - toX;
        double deltaY = this.y - toY;

        this.angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        if (this.angle < 0) {
            this.angle = 360 + this.angle;
        }
        this.x = toX;
        this.y = toY;
    }
}
