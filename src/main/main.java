package main;

import javax.swing.*;
import java.awt.*;

public class main {
    private static final int UI_SCALE = 4;

    private static int s(int base) {
        return base * UI_SCALE;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sorting Studio");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            int targetWidth = s(1200);
            int targetHeight = s(700);
            int width = Math.min(targetWidth, Math.max(640, screen.width - 40));
            int height = Math.min(targetHeight, Math.max(480, screen.height - 80));

            frame.setMinimumSize(new Dimension(Math.max(640, width / 2), Math.max(480, height / 2)));
            frame.setSize(new Dimension(width, height));
            frame.setLocationRelativeTo(null);

            if (targetWidth > screen.width || targetHeight > screen.height) {
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new Font("SansSerif", Font.BOLD, s(4)));
            tabbedPane.addTab("Visualizer", new visualizer());
            tabbedPane.addTab("Comparer", new comparer());

            frame.add(tabbedPane);
            frame.setVisible(true);
        });
    }
}