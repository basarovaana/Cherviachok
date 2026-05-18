package gui;

import java.awt.*;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

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

    public void createEmptyMap(int w, int h) {

        width = w;
        height = h;

        walls = new boolean[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if (x == 0 ||
                        y == 0 ||
                        x == width - 1 ||
                        y == height - 1) {

                    walls[y][x] = true;
                }
            }
        }

        spawnX = CELL_SIZE + CELL_SIZE / 2;
        spawnY = CELL_SIZE + CELL_SIZE / 2;
    }

    public void toggleWall(int cellX, int cellY) {

        if (cellX <= 0 ||
                cellY <= 0 ||
                cellX >= width - 1 ||
                cellY >= height - 1) {

            return;
        }

        walls[cellY][cellX] = !walls[cellY][cellX];
    }

    public void removeWall(int cellX, int cellY) {

        if (cellX <= 0 ||
                cellY <= 0 ||
                cellX >= width - 1 ||
                cellY >= height - 1) {

            return;
        }

        walls[cellY][cellX] = false;
    }

    public void generateRandomMap() {

        Random random = new Random();

        do {

            walls = new boolean[height][width];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    if (x == 0 ||
                            y == 0 ||
                            x == width - 1 ||
                            y == height - 1) {

                        walls[y][x] = true;
                    }
                    else {

                        walls[y][x] =
                                random.nextDouble() < 0.28;
                    }
                }
            }

            walls[1][1] = false;

        } while (!isConnected());

        spawnX = CELL_SIZE + CELL_SIZE / 2;
        spawnY = CELL_SIZE + CELL_SIZE / 2;
    }

    public boolean isConnected() {

        boolean[][] visited =
                new boolean[height][width];

        Queue<Point> queue = new LinkedList<>();

        queue.add(new Point(1, 1));

        visited[1][1] = true;

        int visitedCount = 0;
        int totalWalkable = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if (!walls[y][x]) {
                    totalWalkable++;
                }
            }
        }

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!queue.isEmpty()) {

            Point p = queue.poll();

            visitedCount++;

            for (int i = 0; i < 4; i++) {

                int nx = p.x + dx[i];
                int ny = p.y + dy[i];

                if (nx >= 0 &&
                        ny >= 0 &&
                        nx < width &&
                        ny < height &&
                        !walls[ny][nx] &&
                        !visited[ny][nx]) {

                    visited[ny][nx] = true;

                    queue.add(new Point(nx, ny));
                }
            }
        }

        return visitedCount == totalWalkable;
    }
}