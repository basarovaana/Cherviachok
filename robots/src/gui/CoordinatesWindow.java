package gui;

import javax.swing.*;
import java.awt.*;

public class CoordinatesWindow extends JInternalFrame implements RobotListener {

    private final JLabel label;

    public CoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);

        label = new JLabel("X: 0 Y: 0");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.CENTER);

        getContentPane().add(panel);
        setSize(200, 100);

        model.addListener(this);
    }

    @Override
    public void onRobotMoved(double x, double y) {
        SwingUtilities.invokeLater(() ->
                label.setText("X: " + (int) x + "  Y: " + (int) y)
        );
    }
}