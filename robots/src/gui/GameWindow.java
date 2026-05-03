package gui;

import java.awt.BorderLayout;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;
    private final RobotModel model;
    private PacmanControlPanel controlPanel = null;

    public GameWindow(RobotModel model)
    {
        super("Игровое поле", true, true, true, true);
        this.model = model;

        m_visualizer = new GameVisualizer(model);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
    }

    public void setPacmanMode(boolean enabled, PacmanControlPanel panel) {
        if (enabled && panel != null) {
            this.controlPanel = panel;
            getContentPane().add(controlPanel, BorderLayout.NORTH);
            setSize(815, 670);
        } else if (controlPanel != null) {
            getContentPane().remove(controlPanel);
            controlPanel = null;
            setSize(800, 800);
        }
        revalidate();
        repaint();
    }
}