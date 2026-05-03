package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class PacmanGame {
    private boolean active = false;
    private boolean gameOver = false;
    private boolean win = false;

    private double playerX = 100;
    private double playerY = 100;

    private PacmanMap map;
    private List<Point> coins = new ArrayList<>();
    private int score = 0;
    private int totalCoins = 0;

    private static final int PLAYER_SIZE = 24;
    private static final int COIN_SIZE = 8;

    private final List<PacmanListener> listeners = new ArrayList<>();

    public interface PacmanListener {
        void onGameStateChanged();
        void onScoreChanged(int score);
    }

    public PacmanGame() {
        map = new PacmanMap();
        loadDefaultMap();
    }

    private void loadDefaultMap() {
        String[] maze = {
                "####################",
                "#........#.........#",
                "#.###....#....###..#",
                "#...#..........#...#",
                "#...#.###..###.#...#",
                "#.........#........#",
                "#.###.#.#.#.#.###..#",
                "#.....#...#.......##",
                "#####.#.###.#.######",
                "#........#.........#",
                "#.###.#######.###..#",
                "#...#........#.....#",
                "#...#.###..###.#...#",
                "#........#.........#",
                "####################"
        };

        map.loadFromStrings(maze);

        coins.clear();
        totalCoins = 0;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (map.isWalkable(x, y) && !map.isSpawnPoint(x, y)) {
                    coins.add(new Point(x * 40 + 20, y * 40 + 20));
                    totalCoins++;
                }
            }
        }

        playerX = map.getSpawnX();
        playerY = map.getSpawnY();
    }

    public void start() {
        active = true;
        notifyListeners();
    }

    public void stop() {
        active = false;
    }

    public void movePlayer(int dx, int dy) {
        if (!active || gameOver || win) return;

        double newX = playerX + dx;
        double newY = playerY + dy;

        if (!map.isCollision(newX, newY, PLAYER_SIZE)) {
            playerX = newX;
            playerY = newY;

            checkCoinsCollection();

            if (score == totalCoins) {
                win = true;
                active = false;
                notifyListeners();
            }

            notifyListeners();
        }
    }

    private void checkCoinsCollection() {
        Iterator<Point> it = coins.iterator();
        while (it.hasNext()) {
            Point coin = it.next();
            if (Math.hypot(playerX - coin.x, playerY - coin.y) < 15) {
                it.remove();
                score++;
                notifyScoreChanged();
                break;
            }
        }
    }

    public void drawMapCoinsPlayer(Graphics2D g, int offsetX, int offsetY) {
        if (!active) return;

        map.drawWall(g, offsetX, offsetY);

        g.setColor(Color.YELLOW);
        for (Point coin : coins) {
            g.fillOval(offsetX + coin.x - COIN_SIZE/2,
                    offsetY + coin.y - COIN_SIZE/2,
                    COIN_SIZE, COIN_SIZE);
        }
        drawPlayer(g, offsetX, offsetY);
    }

    private void drawPlayer(Graphics2D g, int offsetX, int offsetY) {
        g.setColor(Color.MAGENTA);
        g.fillOval(offsetX + (int)playerX - PLAYER_SIZE/2,
                offsetY + (int)playerY - PLAYER_SIZE/2,
                PLAYER_SIZE, PLAYER_SIZE);

        g.setColor(Color.WHITE);
        g.fillOval(offsetX + (int)playerX - 3,
                offsetY + (int)playerY - 3,
                6, 6);

        g.setColor(Color.BLACK);
        g.drawOval(offsetX + (int)playerX - 3,
                offsetY + (int)playerY - 3,
                6, 6);

        g.setColor(Color.BLACK);
        g.drawOval(offsetX + (int)playerX - PLAYER_SIZE/2,
                offsetY + (int)playerY - PLAYER_SIZE/2,
                PLAYER_SIZE, PLAYER_SIZE);
    }

    public void addListener(PacmanListener listener) {
        listeners.add(listener);
    }

    public void resetGame() {
        loadDefaultMap();

        active = false;
        gameOver = false;
        win = false;

        notifyListeners();
        notifyScoreChanged();
    }

    private void notifyListeners() {
        for (PacmanListener l : listeners) {
            l.onGameStateChanged();
        }
    }

    private void notifyScoreChanged() {
        for (PacmanListener l : listeners) {
            l.onScoreChanged(score);
        }
    }

    public boolean isActive() { return active; }
    public boolean isGameOver() { return gameOver; }
    public boolean isWin() { return win; }
    public int getTotalCoins() { return totalCoins; }

}