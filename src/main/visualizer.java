package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import dataclass.sort_array;
import sorter.*;

public class visualizer extends JPanel {
    private static final int UI_SCALE = 4;
    private static final String[] ALGORITHMS = {
        "Bubble Sort", "Insertion Sort", "Selection Sort", "Merge Sort", "Tree Sort"
    };

    private JTextField inputField;
    private JButton instantButton, stepButton, resetButton, nextButton, playButton;
    private final Map<String, JToggleButton> algorithmButtons = new LinkedHashMap<>();
    private JPanel visualPanel;
    private JLabel statusLabel;

    private String selectedAlgorithm = ALGORITHMS[0];
    private int[] originalArray;
    private int[] currentArray;
    private int stepIndex = 0;
    private List<int[]> sortSteps;
    private Timer playTimer;

    private static int s(int base) {
        return Math.max(1, base * UI_SCALE);
    }

    private static int f(int base) {
        return Math.max(1, base * UI_SCALE);
    }

    public visualizer() {
        setLayout(new BorderLayout(s(10), s(10)));
        setBorder(BorderFactory.createEmptyBorder(s(10), s(10), s(10), s(10)));

        JPanel topPanel = new JPanel(new BorderLayout(s(8), s(8)));
        topPanel.add(createInputPanel(), BorderLayout.NORTH);
        topPanel.add(createModePanel(), BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        add(createAlgorithmSelectionPanel(), BorderLayout.WEST);

        visualPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawArray(g);
            }
        };
        visualPanel.setBackground(Color.WHITE);
        visualPanel.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 45), s(1)));
        add(visualPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Enter an array, pick an algorithm card, then sort.");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, f(14)));
        add(statusLabel, BorderLayout.SOUTH);

        playTimer = new Timer(s(125), e -> {
            if (sortSteps != null && stepIndex < sortSteps.size() - 1) {
                nextStep();
            } else {
                stopPlay();
            }
        });
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, s(8), s(4)));

        JLabel inputLabel = new JLabel("Array (comma-separated):");
        inputLabel.setFont(new Font("SansSerif", Font.BOLD, f(12)));

        inputField = new JTextField("5,3,8,1,9,2,7,4,6", 32);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, f(12)));

        inputPanel.add(inputLabel);
        inputPanel.add(inputField);
        return inputPanel;
    }

    private JPanel createModePanel() {
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, s(6), s(4)));

        instantButton = new JButton("Instant Sort");
        stepButton = new JButton("Step Mode");
        resetButton = new JButton("Reset");
        nextButton = new JButton("Next Step");
        playButton = new JButton("Play");

        styleActionButton(instantButton, new Color(45, 124, 246));
        styleActionButton(stepButton, new Color(0, 144, 115));
        styleActionButton(nextButton, new Color(238, 149, 0));
        styleActionButton(playButton, new Color(157, 95, 255));
        styleActionButton(resetButton, new Color(189, 44, 44));

        nextButton.setEnabled(false);
        playButton.setEnabled(false);

        modePanel.add(instantButton);
        modePanel.add(stepButton);
        modePanel.add(nextButton);
        modePanel.add(playButton);
        modePanel.add(resetButton);

        instantButton.addActionListener(e -> instantSort());
        stepButton.addActionListener(e -> startStepMode());
        nextButton.addActionListener(e -> nextStep());
        playButton.addActionListener(e -> togglePlay());
        resetButton.addActionListener(e -> reset());

        return modePanel;
    }

    private JPanel createAlgorithmSelectionPanel() {
        JPanel shell = new JPanel(new BorderLayout(s(4), s(4)));
        shell.setPreferredSize(new Dimension(s(120), s(220)));

        JLabel title = new JLabel("Algorithm Selection");
        title.setFont(new Font("SansSerif", Font.BOLD, f(14)));

        JPanel cardPanel = new JPanel(new GridLayout(0, 1, s(3), s(3)));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(s(2), 0, 0, 0));

        ButtonGroup group = new ButtonGroup();
        addAlgorithmCard(cardPanel, group, "Bubble Sort", "Stable, simple O(n^2)");
        addAlgorithmCard(cardPanel, group, "Insertion Sort", "Great for nearly sorted input");
        addAlgorithmCard(cardPanel, group, "Selection Sort", "Few swaps, O(n^2)");
        addAlgorithmCard(cardPanel, group, "Merge Sort", "Consistent O(n log n)");
        addAlgorithmCard(cardPanel, group, "Tree Sort", "BST-based ordering");

        JToggleButton defaultButton = algorithmButtons.get(selectedAlgorithm);
        if (defaultButton != null) {
            defaultButton.setSelected(true);
        }
        updateAlgorithmCardStyles();

        shell.add(title, BorderLayout.NORTH);
        shell.add(cardPanel, BorderLayout.CENTER);
        return shell;
    }

    private void addAlgorithmCard(JPanel panel, ButtonGroup group, String algorithm, String description) {
        String text = "<html><div style='padding:" + s(2) + "px'><b>" + algorithm + "</b><br/>" + description + "</div></html>";
        JToggleButton card = new JToggleButton(text);
        card.setHorizontalAlignment(SwingConstants.LEFT);
        card.setFocusPainted(false);
        card.setFont(new Font("SansSerif", Font.PLAIN, f(11)));
        card.setMargin(new Insets(s(2), s(3), s(2), s(3)));
        card.addActionListener(e -> {
            selectedAlgorithm = algorithm;
            updateAlgorithmCardStyles();
        });

        group.add(card);
        panel.add(card);
        algorithmButtons.put(algorithm, card);
    }

    private void updateAlgorithmCardStyles() {
        for (Map.Entry<String, JToggleButton> entry : algorithmButtons.entrySet()) {
            boolean selected = entry.getValue().isSelected();
            entry.getValue().setBackground(selected ? new Color(28, 122, 255) : new Color(240, 240, 240));
            entry.getValue().setForeground(selected ? Color.WHITE : new Color(30, 30, 30));
            entry.getValue().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(selected ? new Color(15, 86, 190) : new Color(188, 188, 188), s(1)),
                BorderFactory.createEmptyBorder(s(1), s(1), s(1), s(1))
            ));
        }
    }

    private void styleActionButton(JButton button, Color bg) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, f(12)));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setMargin(new Insets(s(2), s(3), s(2), s(3)));
    }

    private void instantSort() {
        if (!parseInput()) {
            return;
        }

        long startTime = System.nanoTime();
        sorter selectedSorter = createSorter(selectedAlgorithm, currentArray);
        currentArray = selectedSorter.solve();
        long endTime = System.nanoTime();

        double timeMs = (endTime - startTime) / 1_000_000.0;
        statusLabel.setText("Sorted using " + selectedAlgorithm + " in " + String.format("%.3f", timeMs) + " ms");

        nextButton.setEnabled(false);
        playButton.setEnabled(false);
        stopPlay();
        visualPanel.repaint();
    }

    private void startStepMode() {
        if (!parseInput()) {
            return;
        }

        stepIndex = 0;
        sortSteps = generateSortSteps(selectedAlgorithm);
        if (sortSteps.isEmpty()) {
            statusLabel.setText("Error generating steps");
            return;
        }

        currentArray = sortSteps.get(0).clone();
        statusLabel.setText("Step 1 of " + sortSteps.size() + " - " + selectedAlgorithm);

        nextButton.setEnabled(true);
        playButton.setEnabled(true);
        playButton.setText("Play");
        stopPlay();
        visualPanel.repaint();
    }

    private void nextStep() {
        if (sortSteps == null || stepIndex >= sortSteps.size() - 1) {
            return;
        }

        stepIndex++;
        currentArray = sortSteps.get(stepIndex).clone();
        statusLabel.setText("Step " + (stepIndex + 1) + " of " + sortSteps.size() + " - " + selectedAlgorithm);
        visualPanel.repaint();

        if (stepIndex >= sortSteps.size() - 1) {
            stopPlay();
            nextButton.setEnabled(false);
            statusLabel.setText("Sorting complete! Final array is shown.");
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
    }

    private void reset() {
        stepIndex = 0;
        sortSteps = null;
        currentArray = originalArray != null ? originalArray.clone() : null;
        nextButton.setEnabled(false);
        playButton.setEnabled(false);
        playTimer.stop();
        playButton.setText("Play");
        statusLabel.setText("Reset complete. Pick an algorithm and sort again.");
        visualPanel.repaint();
    }

    private boolean parseInput() {
        try {
            String input = inputField.getText().trim();
            if (input.isEmpty()) {
                statusLabel.setText("Error: Enter at least one number.");
                return false;
            }

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

    private sorter createSorter(String algorithm, int[] sourceArray) {
        sort_array sortArray = new sort_array(sourceArray.clone());

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

    private List<int[]> generateSortSteps(String algorithm) {
        List<int[]> steps = new ArrayList<>();
        int[] array = originalArray.clone();
        steps.add(array.clone());

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
                sorter s = createSorter(algorithm, array);
                int[] sorted = s.solve();
                steps.add(sorted.clone());
        }

        return steps;
    }

    private void generateBubbleSortSteps(int[] array, List<int[]> steps) {
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

    private void generateInsertionSortSteps(int[] array, List<int[]> steps) {
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

    private void generateSelectionSortSteps(int[] array, List<int[]> steps) {
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
        if (currentArray == null || currentArray.length == 0) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = visualPanel.getWidth();
        int height = visualPanel.getHeight();
        int barWidth = Math.max(s(10), (width - s(40)) / currentArray.length);
        int maxValue = getMaxValue(currentArray);
        int drawHeight = Math.max(s(20), height - s(60));
        int startX = (width - (barWidth * currentArray.length)) / 2;

        for (int i = 0; i < currentArray.length; i++) {
            int barHeight = (int) ((Math.abs(currentArray[i]) / (double) Math.max(1, maxValue)) * drawHeight);
            int x = startX + i * barWidth;
            int y = height - barHeight - s(30);

            g2d.setColor(new Color(70, 130, 180));
            g2d.fillRect(x, y, barWidth - s(2), barHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, barWidth - s(2), barHeight);

            g2d.setFont(new Font("SansSerif", Font.PLAIN, f(10)));
            String value = String.valueOf(currentArray[i]);
            int textWidth = g2d.getFontMetrics().stringWidth(value);
            g2d.drawString(value, x + (barWidth - textWidth) / 2 - 1, height - s(8));
        }
    }

    private int getMaxValue(int[] array) {
        int max = Math.abs(array[0]);
        for (int val : array) {
            max = Math.max(max, Math.abs(val));
        }
        return max;
    }
}
