package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel implements RobotListener
{
    private final Timer m_timer = new Timer("events generator", true);

    private final RobotModel model;

    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;

    public GameVisualizer(RobotModel model)
    {
        this.model = model;
        model.addListener(this);

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
                model.setTarget(e.getX(), e.getY());
            }
        });

        setDoubleBuffered(true);
    }

    @Override
    public void onRobotMoved(double x, double y)
    {
        m_robotPositionX = x;
        m_robotPositionY = y;
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
        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), model.getDirection());
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
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