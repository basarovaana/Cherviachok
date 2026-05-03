package gui;

import java.awt.*;

public class PacmanMap {
    private boolean[][] walls;
    private int width, height;
    private int spawnX = 100, spawnY = 100;
    private static final int CELL_SIZE = 40;

    public void loadFromStrings(String[] maze) {
        height = maze.length;

        width = 0;
        for (int i = 0; i < height; i++) {
            width = maze[i].length();
        }

        walls = new boolean[height][width];

        for (int y = 0; y < height; y++) {
            String line = maze[y];
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                walls[y][x] = (c == '#');
            }
        }

        boolean found = false;
        for (int y = 1; y < height - 1 && !found; y++) {
            for (int x = 1; x < width - 1 && !found; x++) {
                if (!walls[y][x]) {
                    spawnX = x * CELL_SIZE + CELL_SIZE/2;
                    spawnY = y * CELL_SIZE + CELL_SIZE/2;
                    found = true;
                }
            }
        }
    }

    public boolean isCollision(double x, double y, int size) {
        int left = (int)(x - size/2) / CELL_SIZE;
        int right = (int)(x + size/2) / CELL_SIZE;
        int top = (int)(y - size/2) / CELL_SIZE;
        int bottom = (int)(y + size/2) / CELL_SIZE;

        for (int i = top; i <= bottom; i++) {
            for (int j = left; j <= right; j++) {
                if (i >= 0 && i < height && j >= 0 && j < width) {
                    if (walls[i][j]) return true;
                }
            }
        }
        return false;
    }

    public boolean isWalkable(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        return !walls[y][x];
    }

    public boolean isSpawnPoint(int x, int y) {
        return (x * CELL_SIZE + CELL_SIZE/2 == spawnX &&
                y * CELL_SIZE + CELL_SIZE/2 == spawnY);
    }

    public void drawWall(Graphics2D g, int offsetX, int offsetY) {
        g.setColor(Color.BLACK);
        g.fillRect(offsetX, offsetY, width * CELL_SIZE, height * CELL_SIZE);

        g.setColor(Color.BLUE);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (walls[y][x]) {
                    g.fillRect(offsetX + x * CELL_SIZE,
                            offsetY + y * CELL_SIZE,
                            CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.CYAN);
                    g.drawRect(offsetX + x * CELL_SIZE,
                            offsetY + y * CELL_SIZE,
                            CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.BLUE);
                }
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getSpawnX() { return spawnX; }
    public int getSpawnY() { return spawnY; }
}