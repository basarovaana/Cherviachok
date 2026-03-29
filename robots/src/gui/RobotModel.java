package gui;

import java.util.ArrayList;
import java.util.List;

public class RobotModel {
    private double x = 100;
    private double y = 100;
    private double direction = 0;

    private int targetX = 150;
    private int targetY = 100;

    private static final double maxVelocity = 0.5;
    private static final double maxAngularVelocity = 0.01;

    private double maxWidth = 400;
    private double maxHeight = 400;

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

    public void setBounds(double width, double height) {
        this.maxWidth = width;
        this.maxHeight = height;
        x = clamp(x, 0, maxWidth);
        y = clamp(y, 0, maxHeight);
    }

    public void update() {
        double distance = distance(targetX, targetY, x, y);
        if (distance < 0.5) {
            return;
        }

        double velocity = maxVelocity;
        double angleToTarget = angleTo(x, y, targetX, targetY);

        double angleDiff = angleToTarget - direction;
        while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;
        while (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;

        double angularVelocity = 0;
        if (angleDiff > 0.01) {
            angularVelocity = maxAngularVelocity;
        } else if (angleDiff < -0.01) {
            angularVelocity = -maxAngularVelocity;
        }

        moveRobot(velocity, angularVelocity, 10);
        notifyListeners();
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        if (Math.abs(angularVelocity) < 0.0001) {
            x += velocity * duration * Math.cos(direction);
            y += velocity * duration * Math.sin(direction);
        } else {
            double radius = velocity / angularVelocity;
            double newDirection = direction + angularVelocity * duration;
            x += radius * (Math.sin(newDirection) - Math.sin(direction));
            y -= radius * (Math.cos(newDirection) - Math.cos(direction));
        }

        x = clamp(x, 0, maxWidth);
        y = clamp(y, 0, maxHeight);

        direction = normalizeAngle(direction + angularVelocity * duration);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    private static double normalizeAngle(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        return normalizeAngle(Math.atan2(toY - fromY, toX - fromX));
    }

    public double getDirection() { return direction; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }
}