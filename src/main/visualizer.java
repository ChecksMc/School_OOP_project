package main;

import javax.swing.*;
import java.awt.*;
import dataclass.sort_array;
import sorter.*;

public class visualizer extends JPanel {
    private JTextField inputField;
    private JComboBox<String> algorithmSelector;
    private JButton instantButton, stepButton, resetButton, nextButton, playButton;
    private JPanel visualPanel;
    private JLabel statusLabel;
    
    private int[] originalArray;
    private int[] currentArray;
    private int stepIndex = 0;
    private java.util.List<int[]> sortSteps;
    private Timer playTimer;
    
    public visualizer() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Array input
        JLabel inputLabel = new JLabel("Array (comma-separated):");
        inputField = new JTextField("5,3,8,1,9,2,7,4,6", 20);
        controlPanel.add(inputLabel);
        controlPanel.add(inputField);
        
        // Algorithm selector
        JLabel algoLabel = new JLabel("Algorithm:");
        String[] algorithms = {"Bubble Sort", "Insertion Sort", "Selection Sort", 
                               "Merge Sort", "Tree Sort"};
        algorithmSelector = new JComboBox<>(algorithms);
        controlPanel.add(algoLabel);
        controlPanel.add(algorithmSelector);
        
        // Buttons
        instantButton = new JButton("Instant Sort");
        stepButton = new JButton("Step Mode");
        resetButton = new JButton("Reset");
        nextButton = new JButton("Next Step");
        playButton = new JButton("Play");
        
        nextButton.setEnabled(false);
        playButton.setEnabled(false);
        
        controlPanel.add(instantButton);
        controlPanel.add(stepButton);
        controlPanel.add(nextButton);
        controlPanel.add(playButton);
        controlPanel.add(resetButton);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Visualization panel
        visualPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawArray(g);
            }
        };
        visualPanel.setBackground(Color.WHITE);
        visualPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(visualPanel, BorderLayout.CENTER);
        
        // Status label
        statusLabel = new JLabel("Enter an array and select a sorting algorithm");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(statusLabel, BorderLayout.SOUTH);
        
        // Button listeners
        instantButton.addActionListener(e -> instantSort());
        stepButton.addActionListener(e -> startStepMode());
        nextButton.addActionListener(e -> nextStep());
        playButton.addActionListener(e -> togglePlay());
        resetButton.addActionListener(e -> reset());
        
        // Play timer
        playTimer = new Timer(500, e -> {
            if (stepIndex < sortSteps.size() - 1) {
                nextStep();
            } else {
                stopPlay();
            }
        });
    }
    
    private void instantSort() {
        if (!parseInput()) return;
        
        String selectedAlgo = (String) algorithmSelector.getSelectedItem();
        
        long startTime = System.nanoTime();
        sorter sorter = createSorter(selectedAlgo);
        currentArray = sorter.solve();
        long endTime = System.nanoTime();
        
        double timeMs = (endTime - startTime) / 1_000_000.0;
        statusLabel.setText("Sorted using " + selectedAlgo + " in " + 
                          String.format("%.3f", timeMs) + " ms");
        visualPanel.repaint();
    }
    
    private void startStepMode() {
        if (!parseInput()) return;
        
        stepIndex = 0;
        String selectedAlgo = (String) algorithmSelector.getSelectedItem();
        
        // Generate all sorting steps
        sortSteps = generateSortSteps(selectedAlgo);
        
        if (sortSteps.isEmpty()) {
            statusLabel.setText("Error generating steps");
            return;
        }
        
        currentArray = sortSteps.get(0).clone();
        statusLabel.setText("Step 1 of " + sortSteps.size() + " - " + selectedAlgo);
        
        nextButton.setEnabled(true);
        playButton.setEnabled(true);
        visualPanel.repaint();
    }
    
    private void nextStep() {
        if (sortSteps == null || stepIndex >= sortSteps.size() - 1) return;
        
        stepIndex++;
        currentArray = sortSteps.get(stepIndex).clone();
        statusLabel.setText("Step " + (stepIndex + 1) + " of " + sortSteps.size() + 
                          " - " + algorithmSelector.getSelectedItem());
        visualPanel.repaint();
        
        if (stepIndex >= sortSteps.size() - 1) {
            stopPlay();
            statusLabel.setText("Sorting complete! " + statusLabel.getText());
        }
    }
    
    private void togglePlay() {
        if (playTimer.isRunning()) {
            playTimer.stop();
            playButton.setText("Play");
        } else {
            playTimer.start();
            playButton.setText("Pause");
        }
    }
    
    private void stopPlay() {
        playTimer.stop();
        playButton.setText("Play");
        playButton.setEnabled(false);
    }
    
    private void reset() {
        stepIndex = 0;
        sortSteps = null;
        currentArray = originalArray != null ? originalArray.clone() : null;
        nextButton.setEnabled(false);
        playButton.setEnabled(false);
        playTimer.stop();
        playButton.setText("Play");
        statusLabel.setText("Reset - Enter an array and select a sorting algorithm");
        visualPanel.repaint();
    }
    
    private boolean parseInput() {
        try {
            String input = inputField.getText().trim();
            String[] parts = input.split(",");
            originalArray = new int[parts.length];
            currentArray = new int[parts.length];
            
            for (int i = 0; i < parts.length; i++) {
                originalArray[i] = Integer.parseInt(parts[i].trim());
                currentArray[i] = originalArray[i];
            }
            return true;
        } catch (Exception e) {
            statusLabel.setText("Error: Invalid input. Use comma-separated integers.");
            return false;
        }
    }
    
    private sorter createSorter(String algorithm) {
        sort_array sortArray = new sort_array(currentArray.clone());
        
        switch (algorithm) {
            case "Bubble Sort":
                return new bubble_sort(sortArray);
            case "Insertion Sort":
                return new insertion_sort(sortArray);
            case "Selection Sort":
                return new selection_sort(sortArray);
            case "Merge Sort":
                return new merge_sort(sortArray);
            case "Tree Sort":
                return new tree_sort(sortArray);
            default:
                return new bubble_sort(sortArray);
        }
    }
    
    private java.util.List<int[]> generateSortSteps(String algorithm) {
        java.util.List<int[]> steps = new java.util.ArrayList<>();
        int[] array = originalArray.clone();
        steps.add(array.clone());
        
        // Generate steps based on algorithm
        switch (algorithm) {
            case "Bubble Sort":
                generateBubbleSortSteps(array, steps);
                break;
            case "Insertion Sort":
                generateInsertionSortSteps(array, steps);
                break;
            case "Selection Sort":
                generateSelectionSortSteps(array, steps);
                break;
            default:
                // For complex algorithms, just show start and end
                sorter s = createSorter(algorithm);
                int[] sorted = s.solve();
                steps.add(sorted.clone());
        }
        
        return steps;
    }
    
    private void generateBubbleSortSteps(int[] array, java.util.List<int[]> steps) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    steps.add(array.clone());
                }
            }
        }
    }
    
    private void generateInsertionSortSteps(int[] array, java.util.List<int[]> steps) {
        int n = array.length;
        for (int i = 1; i < n; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j = j - 1;
                steps.add(array.clone());
            }
            array[j + 1] = key;
            steps.add(array.clone());
        }
    }
    
    private void generateSelectionSortSteps(int[] array, java.util.List<int[]> steps) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                int temp = array[i];
                array[i] = array[minIndex];
                array[minIndex] = temp;
                steps.add(array.clone());
            }
        }
    }
    
    private void drawArray(Graphics g) {
        if (currentArray == null || currentArray.length == 0) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = visualPanel.getWidth();
        int height = visualPanel.getHeight();
        int barWidth = Math.max(10, (width - 40) / currentArray.length);
        int maxValue = getMaxValue(currentArray);
        
        int startX = (width - (barWidth * currentArray.length)) / 2;
        
        for (int i = 0; i < currentArray.length; i++) {
            int barHeight = (int) ((currentArray[i] / (double) maxValue) * (height - 60));
            int x = startX + i * barWidth;
            int y = height - barHeight - 30;
            
            // Draw bar
            g2d.setColor(new Color(70, 130, 180));
            g2d.fillRect(x, y, barWidth - 2, barHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, barWidth - 2, barHeight);
            
            // Draw value
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String value = String.valueOf(currentArray[i]);
            int textWidth = g2d.getFontMetrics().stringWidth(value);
            g2d.drawString(value, x + (barWidth - textWidth) / 2 - 1, height - 10);
        }
    }
    
    private int getMaxValue(int[] array) {
        int max = array[0];
        for (int val : array) {
            if (val > max) max = val;
        }
        return max;
    }
}
