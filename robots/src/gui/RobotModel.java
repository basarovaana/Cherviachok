package gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RobotModel {
    private double x = 100;
    private double y = 100;
    private double direction = 0;

    private int targetX = 150;
    private int targetY = 100;

    private static final double maxVelocity = 5;
    private static final double maxAngularVelocity = 0.7;

    private final List<RobotListener> listeners = new ArrayList<>();

    private List<int[]> allRoutePoints = new ArrayList<>();
    private Queue<int[]> pendingRoutePoints = new LinkedList<>();

    private boolean routeMode = false;
    private boolean movingAlongRoute = false;
    private final List<RouteListener> routeListeners = new ArrayList<>();

    public void addListener(RobotListener listener) {
        listeners.add(listener);
    }

    public void addRouteListener(RouteListener listener) {
        routeListeners.add(listener);
    }

    private void notifyListeners() {
        for (RobotListener l : listeners) {
            l.onRobotMoved(x, y);
        }
    }

    private void notifyRouteChanged() {
        for (RouteListener l : routeListeners) {
            l.onRouteChanged(new ArrayList<>(allRoutePoints));
        }
    }

    private void notifyCurrentTargetChanged() {
        for (RouteListener l : routeListeners) {
            l.onCurrentTargetChanged(targetX, targetY);
        }
    }

    public void setTargetPosition(int x, int y) {
        this.targetX = x;
        this.targetY = y;
    }

    public void addRoutePoint(int x, int y) {
        if (routeMode) {
            int[] point = new int[]{x, y};
            allRoutePoints.add(point);
            pendingRoutePoints.add(point);
            notifyRouteChanged();
        }
    }

    public void clearRoute() {
        allRoutePoints.clear();
        pendingRoutePoints.clear();
        routeMode = false;
        movingAlongRoute = false;
        notifyRouteChanged();
    }

    public void startDrawingRoute() {
        routeMode = true;
        movingAlongRoute = false;
        allRoutePoints.clear();
        pendingRoutePoints.clear();
        notifyRouteChanged();
    }

    public void finishDrawingRoute() {
        routeMode = false;
        if (!pendingRoutePoints.isEmpty()) {
            int[] firstPoint = pendingRoutePoints.poll();
            targetX = firstPoint[0];
            targetY = firstPoint[1];
            notifyCurrentTargetChanged();
            notifyRouteChanged();
        }
    }

    public void startMovingAlongRoute() {
        if (!pendingRoutePoints.isEmpty()) {
            movingAlongRoute = true;
        }
    }

    private void checkAndAdvanceToNextPoint() {
        if (!movingAlongRoute) return;

        double dx = targetX - x;
        double dy = targetY - y;
        if (Math.hypot(dx, dy) < 5) {
            if (!pendingRoutePoints.isEmpty()) {
                int[] nextPoint = pendingRoutePoints.poll();
                targetX = nextPoint[0];
                targetY = nextPoint[1];
                notifyCurrentTargetChanged();
                notifyRouteChanged();
            } else {
                movingAlongRoute = false;
                notifyRouteChanged();
            }
        }
    }

    public boolean isRouteMode() {
        return routeMode;
    }

    public boolean isMovingAlongRoute() {
        return movingAlongRoute;
    }


    public void update() {
        double dx = targetX - x;
        double dy = targetY - y;

        double distance = Math.hypot(dx, dy);
        if (distance < 5) {
            x = targetX;
            y = targetY;
            checkAndAdvanceToNextPoint();
            notifyListeners();
            return;
        }

        double angleToTarget = Math.atan2(dy, dx);
        double angleDiff = angleToTarget - direction;

        while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;
        while (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;

        double angularVelocity = Math.max(-maxAngularVelocity,
                Math.min(maxAngularVelocity, angleDiff));

        x += maxVelocity * Math.cos(direction);
        y += maxVelocity * Math.sin(direction);

        direction += angularVelocity;

        notifyListeners();
    }

    public double getDirection() {
        return direction;
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }
}