package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.swing.*;

public class GameVisualizer extends JPanel implements RobotListener, RouteListener
{
    private final Timer m_timer = new Timer("events generator", true);
    private final RobotModel model;

    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;

    private List<int[]> routePoints = null;
    private Image winImage;
    private Image gameOverImage;

    public GameVisualizer(RobotModel model)
    {
        this.model = model;
        model.addListener(this);
        model.addRouteListener(this);
        loadWinImage();
        loadGameOverImage();

        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                model.update();
                repaint();
            }
        }, 0, 40);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                Insets insets = getInsets();
                int x = e.getX() - insets.left;
                int y = e.getY() - insets.top;

                PacmanGame pacmanGame =
                        model.getPacmanGame();

                if (model.isPacmanMode()
                        &&
                        pacmanGame.isEditMode()) {

                    int cellX = x / 40;
                    int cellY = y / 40;

                    if (SwingUtilities.isLeftMouseButton(e)) {

                        pacmanGame
                                .getMap()
                                .toggleWall(cellX, cellY);
                    }

                    if (SwingUtilities.isRightMouseButton(e)) {

                        pacmanGame
                                .getMap()
                                .removeWall(cellX, cellY);
                    }

                    repaint();

                    return;
                }

                if (model.isRouteMode()) {
                    model.addRoutePoint(x, y);
                } else if (!model.isMovingAlongRoute()) {
                    model.setTargetPosition(x, y);
                    repaint();
                }
            }
        });

        setDoubleBuffered(true);
        setFocusable(true);
    }

    private void loadWinImage() {
        try {
            java.io.File imgFile = new java.io.File("image/win.jpg");
            if (imgFile.exists()) {
                winImage = ImageIO.read(imgFile);
            } else {
                winImage = null;
            }
        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
            winImage = null;
        }
    }

    private void loadGameOverImage() {
        try {
            java.io.File imgFile = new java.io.File("image/lost.jpg");
            if (imgFile.exists()) {
                gameOverImage = ImageIO.read(imgFile);
            } else {
                gameOverImage = null;
            }
        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
            gameOverImage = null;
        }
    }

    @Override
    public void onRobotMoved(double x, double y)
    {
        m_robotPositionX = x;
        m_robotPositionY = y;
        repaint();
    }

    @Override
    public void onRouteChanged(List<int[]> routePoints) {
        this.routePoints = routePoints;
        repaint();
    }

    @Override
    public void onCurrentTargetChanged(int x, int y) {
        repaint();
    }

    private static int round(double value)
    {
        return (int)(value + 0.5);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        if (model.isPacmanMode()) {
            PacmanGame pacmanGame = model.getPacmanGame();
            if (pacmanGame.isActive()) {
                pacmanGame.drawMapCoinsPlayer(g2d, 0, 0);
            }

            if (pacmanGame.isWin()) {
                drawWinScreenFull(g2d);
            } else if (pacmanGame.isGameOver()) {
                drawGameOverScreenFull(g2d);
            }
        } else {
            drawRoute(g2d);
            drawTarget(g2d, model.getTargetX(), model.getTargetY());
            drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), model.getDirection());
        }
    }

    private void drawWinScreenFull(Graphics2D g) {
        g.drawImage(winImage, 0, 0, getWidth(), getHeight(), this);
    }

    private void drawGameOverScreenFull(Graphics2D g) {
        g.drawImage(gameOverImage, 0, 0, getWidth(), getHeight(), this);
    }

    private void drawRoute(Graphics2D g) {
        if (routePoints == null || routePoints.isEmpty()) return;

        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(2));

        int[] previous = null;
        for (int[] point : routePoints) {
            if (previous != null) {
                g.drawLine(previous[0], previous[1], point[0], point[1]);
            }
            previous = point;
        }

        for (int[] point : routePoints) {
            g.setColor(Color.GREEN);
            fillOval(g, point[0], point[1], 5, 5);
            g.setColor(Color.BLACK);
            drawOval(g, point[0], point[1], 5, 5);
        }
    }

    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);

        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);

        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int w, int h)
    {
        g.fillOval(centerX - w / 2, centerY - h / 2, w, h);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int w, int h)
    {
        g.drawOval(centerX - w / 2, centerY - h / 2, w, h);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);

        g.setColor(Color.MAGENTA);
        fillOval(g, x, y, 30, 10);

        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);

        g.setColor(Color.WHITE);
        fillOval(g, x + 10, y, 5, 5);

        g.setColor(Color.BLACK);
        drawOval(g, x + 10, y, 5, 5);
    }
}