package gui;

import java.awt.*;

public class Ghost {
    private double x;
    private double y;

    private int dirX;
    private int dirY;

    private final Color color;

    private static final int SIZE = 24;
    private static final int SPEED = 2;

    public Ghost(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void move() {
        x += dirX * SPEED;
        y += dirY * SPEED;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setDirection(int dx, int dy) {
        dirX = dx;
        dirY = dy;
    }

    public int getDirX() {
        return dirX;
    }

    public int getDirY() {
        return dirY;
    }

    public int getSize() {
        return SIZE;
    }

    public Color getColor() {
        return color;
    }
}