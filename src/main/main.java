package main;

import javax.swing.*;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sorting Algorithm Visualizer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);
            
            // Create tabbed pane for different modes
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // Add visualizer tab
            visualizer visualizerPanel = new visualizer();
            tabbedPane.addTab("Visualizer", visualizerPanel);
            
            // Add comparer tab
            comparer comparerPanel = new comparer();
            tabbedPane.addTab("Comparer", comparerPanel);
            
            frame.add(tabbedPane);
            frame.setVisible(true);
        });
    }
}