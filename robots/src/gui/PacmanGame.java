package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;

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

    private List<Ghost> ghosts = new ArrayList<>();
    private static final int CELL_SIZE = 40;

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

        ghosts.clear();

        addGhost(18, 13, Color.RED);
        addGhost(18, 1, Color.PINK);
        addGhost(1, 13, Color.CYAN);
        addGhost(9, 9, Color.ORANGE);

        score = 0;
        gameOver = false;
        win = false;
    }

    public void start() {
        active = true;
        notifyListeners();
        notifyScoreChanged();
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
        drawGhosts(g, offsetX, offsetY);
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
        score = 0;

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

    private void drawGhosts(Graphics2D g, int offsetX, int offsetY) {
        for (Ghost ghost : ghosts) {

            g.setColor(ghost.getColor());

            g.fillOval(
                    offsetX + (int) ghost.getX() - ghost.getSize() / 2,
                    offsetY + (int) ghost.getY() - ghost.getSize() / 2,
                    ghost.getSize(),
                    ghost.getSize()
            );

            g.setColor(Color.WHITE);

            g.fillOval(
                    offsetX + (int) ghost.getX() - 6,
                    offsetY + (int) ghost.getY() - 4,
                    5,
                    5
            );

            g.fillOval(
                    offsetX + (int) ghost.getX() + 1,
                    offsetY + (int) ghost.getY() - 4,
                    5,
                    5
            );
        }
    }

    public void updateGhosts() {

        for (Ghost ghost : ghosts) {

            if (Math.abs(
                    (((int) ghost.getX()) % CELL_SIZE) - CELL_SIZE / 2
            ) < 3
                    &&
                    Math.abs(
                            (((int) ghost.getY()) % CELL_SIZE) - CELL_SIZE / 2
                    ) < 3) {

                int ghostCellX = (int) ghost.getX() / CELL_SIZE;
                int ghostCellY = (int) ghost.getY() / CELL_SIZE;

                int playerCellX = (int) playerX / CELL_SIZE;
                int playerCellY = (int) playerY / CELL_SIZE;

                int randomOffsetX =
                        (int)(Math.random() * 3) - 1;

                int randomOffsetY =
                        (int)(Math.random() * 3) - 1;

                int targetX = playerCellX + randomOffsetX;
                int targetY = playerCellY + randomOffsetY;

                if (!map.isWalkable(targetX, targetY)) {

                    targetX = playerCellX;
                    targetY = playerCellY;
                }

                int[] next = findNextStep(
                        ghostCellX,
                        ghostCellY,
                        targetX,
                        targetY
                );

                if (next != null) {

                    int dx = next[0] - ghostCellX;
                    int dy = next[1] - ghostCellY;

                    if (Math.random() < 0.35) {

                        List<int[]> possible =
                                getPossibleMoves(ghostCellX, ghostCellY);

                        possible.removeIf(m ->
                                m[0] == -ghost.getDirX()
                                        &&
                                        m[1] == -ghost.getDirY());

                        if (!possible.isEmpty()) {

                            int[] randomMove =
                                    possible.get(
                                            (int)(Math.random() * possible.size())
                                    );

                            dx = randomMove[0];
                            dy = randomMove[1];
                        }
                    }

                    ghost.setDirection(dx, dy);
                }
            }

            double newX =
                    ghost.getX() + ghost.getDirX() * 2;

            double newY =
                    ghost.getY() + ghost.getDirY() * 2;

            if (!map.isCollision(newX, newY, ghost.getSize())) {
                ghost.move();
            }
        }
    }

    private List<int[]> getPossibleMoves(int x, int y) {

        List<int[]> moves = new ArrayList<>();

        if (map.isWalkable(x + 1, y)) moves.add(new int[]{1, 0});
        if (map.isWalkable(x - 1, y)) moves.add(new int[]{-1, 0});
        if (map.isWalkable(x, y + 1)) moves.add(new int[]{0, 1});
        if (map.isWalkable(x, y - 1)) moves.add(new int[]{0, -1});

        return moves;
    }

    private int[] findNextStep(int startX,
                               int startY,
                               int targetX,
                               int targetY) {

        boolean[][] visited =
                new boolean[map.getHeight()][map.getWidth()];

        Point[][] parent =
                new Point[map.getHeight()][map.getWidth()];

        Queue<Point> queue = new LinkedList<>();

        queue.add(new Point(startX, startY));

        visited[startY][startX] = true;

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!queue.isEmpty()) {

            Point p = queue.poll();

            if (p.x == targetX && p.y == targetY) {
                break;
            }

            for (int i = 0; i < 4; i++) {

                int nx = p.x + dx[i];
                int ny = p.y + dy[i];

                if (map.isWalkable(nx, ny) &&
                        !visited[ny][nx]) {

                    visited[ny][nx] = true;

                    parent[ny][nx] = p;

                    queue.add(new Point(nx, ny));
                }
            }
        }

        if (!visited[targetY][targetX]) {
            return null;
        }

        Point current = new Point(targetX, targetY);

        while (parent[current.y][current.x] != null) {

            Point prev = parent[current.y][current.x];

            if (prev.x == startX &&
                    prev.y == startY) {

                return new int[]{
                        current.x,
                        current.y
                };
            }

            current = prev;
        }

        return null;
    }

    public void checkGhostCollision() {

        for (Ghost ghost : ghosts) {

            double dist = Math.hypot(
                    playerX - ghost.getX(),
                    playerY - ghost.getY()
            );

            if (dist < 20) {

                gameOver = true;
                active = false;

                notifyListeners();

                return;
            }
        }
    }

    private void addGhost(int cellX, int cellY, Color color) {

        if (!map.isWalkable(cellX, cellY)) {
            return;
        }

        int x = cellX * CELL_SIZE + CELL_SIZE / 2;
        int y = cellY * CELL_SIZE + CELL_SIZE / 2;

        ghosts.add(new Ghost(x, y, color));
    }

}