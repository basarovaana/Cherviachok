package gui;

import java.util.ArrayList;
import java.util.List;

public class RobotModel {
    private double x = 100;
    private double y = 100;
    private double direction = 0;

    private int targetX = 150;
    private int targetY = 100;

    private static final double maxVelocity = 5;
    private static final double maxAngularVelocity = 0.7;

    private final List<RobotListener> listeners = new ArrayList<>();

    public void addListener(RobotListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (RobotListener l : listeners) {
            l.onRobotMoved(x, y);
        }
    }

    public void setTargetPosition(int x, int y) {
        targetX = x;
        targetY = y;
    }

    public void update() {
        double dx = targetX - x;
        double dy = targetY - y;

        double distance = Math.hypot(dx, dy);
        if (distance < 5) {
            x = targetX;
            y = targetY;
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