package gui;

import java.util.ArrayList;
import java.util.List;

public class RobotModel {
    private double x = 100;
    private double y = 100;
    private double direction = 0;

    private int targetX = 150;
    private int targetY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    private final List<RobotListener> listeners = new ArrayList<>();

    public void addListener(RobotListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (RobotListener listener : listeners) {
            listener.onRobotMoved(x, y);
        }
    }

    public void setTarget(int x, int y) {
        targetX = x;
        targetY = y;
    }

    public void update() {
        double distance = distance(targetX, targetY, x, y);
        if (distance < 0.5) {
            return;
        }

        double velocity = maxVelocity;
        double angleToTarget = angleTo(x, y, targetX, targetY);

        double angularVelocity = 0;
        if (angleToTarget > direction) {
            angularVelocity = maxAngularVelocity;
        }
        if (angleToTarget < direction) {
            angularVelocity = -maxAngularVelocity;
        }

        moveRobot(velocity, angularVelocity, 10);
        notifyListeners();
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        double newX = x + velocity * duration * Math.cos(direction);
        double newY = y + velocity * duration * Math.sin(direction);

        x = newX;
        y = newY;
        direction += angularVelocity * duration;
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        return Math.atan2(toY - fromY, toX - fromX);
    }

    public double getDirection() {
        return direction;
    }
}