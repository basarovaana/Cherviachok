package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class PacmanControlPanel extends JPanel {
    private PacmanGame game;
    private JLabel scoreLabel;
    private JLabel statusLabel;
    private JButton newGameButton;

    private Timer movementTimer;
    private int currentDx = 0;
    private int currentDy = 0;
    private Map<Integer, Boolean> keysPressed = new HashMap<>();
    private static final int SPEED = 5;

    public PacmanControlPanel(PacmanGame game) {
        this.game = game;
        setLayout(new FlowLayout());
        setBackground(Color.DARK_GRAY);

        scoreLabel = new JLabel("Счёт: 0 /" +  + game.getTotalCoins());
        statusLabel = new JLabel("Режим: PACMAN");

        newGameButton = new JButton("Новая игра");
        newGameButton.setBackground(Color.GREEN);
        newGameButton.setForeground(Color.BLACK);
        newGameButton.setFocusable(false);
        newGameButton.addActionListener((ActionEvent e) -> {
            resetAndRestart();
        });

        scoreLabel.setForeground(Color.WHITE);
        statusLabel.setForeground(Color.YELLOW);

        add(scoreLabel);
        add(statusLabel);
        add(newGameButton);

        game.addListener(new PacmanGame.PacmanListener() {
            @Override
            public void onGameStateChanged() {
                updateGameStatus();
            }

            @Override
            public void onScoreChanged(int score) {
                scoreLabel.setText("Счёт: " + score + " / " + game.getTotalCoins());
            }
        });

        movementTimer = new Timer(16, e -> {
            if (game.isActive() && !game.isGameOver() && (currentDx != 0 || currentDy != 0)) {
                game.movePlayer(currentDx, currentDy);
            }
        });
        movementTimer.start();
    }

    private void updateGameStatus() {
        if (game.isWin()) {
            statusLabel.setText("ПОБЕДА!");
            statusLabel.setForeground(Color.GREEN);
        } else if (game.isGameOver()) {
            statusLabel.setText("ПОРАЖЕНИЕ");
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText("Режим: PACMAN");
            statusLabel.setForeground(Color.YELLOW);
        }
    }

    private void resetAndRestart() {
        game.resetGame();
        game.start();

        currentDx = 0;
        currentDy = 0;
        keysPressed.clear();

        scoreLabel.setText("Счёт: 0 / " + game.getTotalCoins());
        updateGameStatus();
    }

    public KeyAdapter getKeyAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                keysPressed.put(key, true);
                updateDirection();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                keysPressed.put(key, false);
                updateDirection();
            }

            private void updateDirection() {
                int newDx = 0;
                int newDy = 0;

                boolean up = keysPressed.getOrDefault(KeyEvent.VK_UP, false) ||
                        keysPressed.getOrDefault(KeyEvent.VK_W, false);
                boolean down = keysPressed.getOrDefault(KeyEvent.VK_DOWN, false) ||
                        keysPressed.getOrDefault(KeyEvent.VK_S, false);
                boolean left = keysPressed.getOrDefault(KeyEvent.VK_LEFT, false) ||
                        keysPressed.getOrDefault(KeyEvent.VK_A, false);
                boolean right = keysPressed.getOrDefault(KeyEvent.VK_RIGHT, false) ||
                        keysPressed.getOrDefault(KeyEvent.VK_D, false);

                if (up) newDy = -SPEED;
                else if (down) newDy = SPEED;

                if (left) newDx = -SPEED;
                else if (right) newDx = SPEED;

                currentDx = newDx;
                currentDy = newDy;
            }
        };
    }
}