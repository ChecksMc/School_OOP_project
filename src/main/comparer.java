package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import dataclass.sort_array;
import sorter.*;

public class comparer extends JPanel {
    private static final int UI_SCALE = 4;
    private static final String[] ALGORITHMS = {
        "Bubble Sort", "Insertion Sort", "Selection Sort", "Merge Sort", "Tree Sort"
    };

    private JTextField inputField;
    private JTextField arraySizeField;
    private JButton instantCompareButton;
    private JButton randomArrayButton;
    private JButton startStepButton;
    private JButton nextStepButton;
    private JButton playPauseButton;
    private JButton resetStepButton;
    private JButton selectAllButton;
    private JButton clearSelectionButton;

    private final Map<String, JToggleButton> algorithmCards = new LinkedHashMap<>();

    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JTextArea statsArea;

    private final List<ComparisonResult> completedResults = new ArrayList<>();
    private List<String> stepAlgorithms = new ArrayList<>();
    private int[] stepSourceArray;
    private int stepIndex = 0;
    private Timer stepTimer;

    private static int s(int base) {
        return Math.max(1, base * UI_SCALE);
    }

    private static int f(int base) {
        return Math.max(1, base * UI_SCALE);
    }

    public comparer() {
        setLayout(new BorderLayout(s(10), s(10)));
        setBorder(BorderFactory.createEmptyBorder(s(10), s(10), s(10), s(10)));

        JPanel topPanel = new JPanel(new BorderLayout(s(8), s(8)));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, s(6), s(3)));
        JLabel inputLabel = new JLabel("Array (comma-separated):");
        inputLabel.setFont(new Font("SansSerif", Font.BOLD, f(12)));
        inputField = new JTextField("5,3,8,1,9,2,7,4,6,10", 25);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, f(12)));

        JLabel sizeLabel = new JLabel("Random Size:");
        sizeLabel.setFont(new Font("SansSerif", Font.BOLD, f(12)));
        arraySizeField = new JTextField("100", 5);
        arraySizeField.setFont(new Font("Monospaced", Font.PLAIN, f(12)));
        randomArrayButton = new JButton("Generate Random");
        styleButton(randomArrayButton, new Color(50, 112, 201));

        inputPanel.add(inputLabel);
        inputPanel.add(inputField);
        inputPanel.add(sizeLabel);
        inputPanel.add(arraySizeField);
        inputPanel.add(randomArrayButton);

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, s(4), s(2)));
        instantCompareButton = new JButton("Compare Instant");
        startStepButton = new JButton("Start Step Compare");
        nextStepButton = new JButton("Next Step");
        playPauseButton = new JButton("Play");
        resetStepButton = new JButton("Reset Step Mode");

        styleButton(instantCompareButton, new Color(0, 132, 89));
        styleButton(startStepButton, new Color(122, 78, 185));
        styleButton(nextStepButton, new Color(211, 144, 23));
        styleButton(playPauseButton, new Color(157, 95, 255));
        styleButton(resetStepButton, new Color(189, 44, 44));

        nextStepButton.setEnabled(false);
        playPauseButton.setEnabled(false);

        modePanel.add(instantCompareButton);
        modePanel.add(startStepButton);
        modePanel.add(nextStepButton);
        modePanel.add(playPauseButton);
        modePanel.add(resetStepButton);

        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(modePanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        add(createAlgorithmSelectionPanel(), BorderLayout.WEST);

        String[] columnNames = {"Algorithm", "Time (ms)", "Comparisons Est.", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Monospaced", Font.PLAIN, f(12)));
        resultTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, f(12)));
        resultTable.setRowHeight(s(25));
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(s(170));
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(s(120));
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(s(170));
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(s(120));

        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), s(1)),
            "Performance Comparison"
        ));

        statsArea = new JTextArea(10, 40);
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, f(12)));
        statsArea.setBorder(BorderFactory.createEmptyBorder(s(5), s(5), s(5), s(5)));
        JScrollPane statsScrollPane = new JScrollPane(statsArea);
        statsScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), s(1)),
            "Statistics"
        ));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, statsScrollPane);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(s(3));
        add(splitPane, BorderLayout.CENTER);

        randomArrayButton.addActionListener(e -> generateRandomArray());
        instantCompareButton.addActionListener(e -> runInstantComparison());
        startStepButton.addActionListener(e -> startStepComparison());
        nextStepButton.addActionListener(e -> runNextStep());
        playPauseButton.addActionListener(e -> toggleStepPlay());
        resetStepButton.addActionListener(e -> resetStepMode());

        stepTimer = new Timer(s(125), e -> {
            if (stepIndex < stepAlgorithms.size()) {
                runNextStep();
            } else {
                finishStepMode();
            }
        });

        statsArea.setText(
            "Choose algorithms from the selection panel, then use:\n" +
            "- Compare Instant: benchmark all selected algorithms immediately\n" +
            "- Start Step Compare: benchmark one algorithm at a time using Next Step or Play\n"
        );
    }

    private JPanel createAlgorithmSelectionPanel() {
        JPanel shell = new JPanel(new BorderLayout(s(4), s(4)));
        shell.setPreferredSize(new Dimension(s(130), s(230)));

        JLabel title = new JLabel("Algorithm Selection");
        title.setFont(new Font("SansSerif", Font.BOLD, f(14)));

        JPanel cardsPanel = new JPanel(new GridLayout(0, 1, s(3), s(3)));

        addAlgorithmCard(cardsPanel, "Bubble Sort", "Simple O(n^2), stable");
        addAlgorithmCard(cardsPanel, "Insertion Sort", "Strong for nearly sorted");
        addAlgorithmCard(cardsPanel, "Selection Sort", "Low swaps, O(n^2)");
        addAlgorithmCard(cardsPanel, "Merge Sort", "Reliable O(n log n)");
        addAlgorithmCard(cardsPanel, "Tree Sort", "BST in-order traversal");

        for (JToggleButton card : algorithmCards.values()) {
            card.setSelected(true);
        }
        updateAlgorithmCardStyles();

        JPanel footer = new JPanel(new GridLayout(1, 2, s(2), s(2)));
        selectAllButton = new JButton("Select All");
        clearSelectionButton = new JButton("Clear");
        styleButton(selectAllButton, new Color(22, 118, 177));
        styleButton(clearSelectionButton, new Color(108, 108, 108));
        selectAllButton.addActionListener(e -> {
            for (JToggleButton card : algorithmCards.values()) {
                card.setSelected(true);
            }
            updateAlgorithmCardStyles();
        });
        clearSelectionButton.addActionListener(e -> {
            for (JToggleButton card : algorithmCards.values()) {
                card.setSelected(false);
            }
            updateAlgorithmCardStyles();
        });
        footer.add(selectAllButton);
        footer.add(clearSelectionButton);

        shell.add(title, BorderLayout.NORTH);
        shell.add(cardsPanel, BorderLayout.CENTER);
        shell.add(footer, BorderLayout.SOUTH);
        return shell;
    }

    private void addAlgorithmCard(JPanel parent, String algorithm, String description) {
        String text = "<html><div style='padding:" + s(2) + "px'><b>" + algorithm + "</b><br/>" + description + "</div></html>";
        JToggleButton card = new JToggleButton(text);
        card.setHorizontalAlignment(SwingConstants.LEFT);
        card.setFocusPainted(false);
        card.setFont(new Font("SansSerif", Font.PLAIN, f(11)));
        card.setMargin(new Insets(s(2), s(3), s(2), s(3)));
        card.addActionListener(e -> updateAlgorithmCardStyles());
        algorithmCards.put(algorithm, card);
        parent.add(card);
    }

    private void updateAlgorithmCardStyles() {
        for (JToggleButton card : algorithmCards.values()) {
            boolean selected = card.isSelected();
            card.setBackground(selected ? new Color(28, 122, 255) : new Color(240, 240, 240));
            card.setForeground(selected ? Color.WHITE : new Color(30, 30, 30));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(selected ? new Color(15, 86, 190) : new Color(188, 188, 188), s(1)),
                BorderFactory.createEmptyBorder(s(1), s(1), s(1), s(1))
            ));
        }
    }

    private void styleButton(JButton button, Color bg) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, f(12)));
        button.setForeground(Color.WHITE);
        button.setBackground(bg);
        button.setMargin(new Insets(s(2), s(3), s(2), s(3)));
    }

    private List<String> getSelectedAlgorithms() {
        List<String> selected = new ArrayList<>();
        for (String algorithm : ALGORITHMS) {
            JToggleButton card = algorithmCards.get(algorithm);
            if (card != null && card.isSelected()) {
                selected.add(algorithm);
            }
        }
        return selected;
    }

    private void generateRandomArray() {
        try {
            int size = Integer.parseInt(arraySizeField.getText().trim());
            if (size < 1 || size > 10000) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a size between 1 and 10000",
                    "Invalid Size", JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append((int) (Math.random() * 1000));
            }
            inputField.setText(sb.toString());

            statsArea.setText("Generated random array of size " + size + "\n" +
                "Use instant or step mode to compare selected algorithms.");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid number",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runInstantComparison() {
        int[] originalArray = parseInput();
        if (originalArray == null) {
            return;
        }

        List<String> selected = getSelectedAlgorithms();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Select at least one algorithm from the selection screen.",
                "No Algorithm Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        stopStepTimer();
        tableModel.setRowCount(0);
        completedResults.clear();

        statsArea.setText("Running instant comparison for " + selected.size() + " algorithm(s)...\n");

        for (String algorithm : selected) {
            ComparisonResult result = benchmarkAlgorithm(algorithm, originalArray);
            completedResults.add(result);
            appendResultRow(result);
        }

        updateSummaryStats(originalArray.length, "Instant Mode");
        nextStepButton.setEnabled(false);
        playPauseButton.setEnabled(false);
        playPauseButton.setText("Play");
    }

    private void startStepComparison() {
        int[] originalArray = parseInput();
        if (originalArray == null) {
            return;
        }

        List<String> selected = getSelectedAlgorithms();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Select at least one algorithm from the selection screen.",
                "No Algorithm Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        stopStepTimer();
        stepAlgorithms = new ArrayList<>(selected);
        stepSourceArray = originalArray.clone();
        stepIndex = 0;
        tableModel.setRowCount(0);
        completedResults.clear();

        nextStepButton.setEnabled(true);
        playPauseButton.setEnabled(true);
        playPauseButton.setText("Play");

        statsArea.setText(
            "Step Mode started.\n" +
            "Selected algorithms: " + String.join(", ", stepAlgorithms) + "\n" +
            "Use Next Step or Play to benchmark one algorithm at a time.\n"
        );
    }

    private void runNextStep() {
        if (stepAlgorithms.isEmpty() || stepSourceArray == null) {
            return;
        }

        if (stepIndex >= stepAlgorithms.size()) {
            finishStepMode();
            return;
        }

        String algorithm = stepAlgorithms.get(stepIndex);
        ComparisonResult result = benchmarkAlgorithm(algorithm, stepSourceArray);
        completedResults.add(result);
        appendResultRow(result);
        stepIndex++;

        if (stepIndex >= stepAlgorithms.size()) {
            finishStepMode();
        } else {
            statsArea.setText(
                "Step Mode progress: " + stepIndex + " / " + stepAlgorithms.size() + " complete\n" +
                "Last: " + result.algorithm + " in " + result.timeText + " ms\n" +
                "Next: " + stepAlgorithms.get(stepIndex) + "\n"
            );
        }
    }

    private void toggleStepPlay() {
        if (stepTimer.isRunning()) {
            stopStepTimer();
            playPauseButton.setText("Play");
        } else {
            if (stepAlgorithms.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Start step comparison first.",
                    "Step Mode Not Started", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            playPauseButton.setText("Pause");
            stepTimer.start();
        }
    }

    private void finishStepMode() {
        stopStepTimer();
        nextStepButton.setEnabled(false);
        playPauseButton.setEnabled(false);
        playPauseButton.setText("Play");
        if (stepSourceArray != null) {
            updateSummaryStats(stepSourceArray.length, "Step Mode");
        }
    }

    private void resetStepMode() {
        stopStepTimer();
        stepAlgorithms.clear();
        stepSourceArray = null;
        stepIndex = 0;
        nextStepButton.setEnabled(false);
        playPauseButton.setEnabled(false);
        playPauseButton.setText("Play");
        tableModel.setRowCount(0);
        completedResults.clear();
        statsArea.setText("Step mode reset. Choose algorithms and start a new comparison.");
    }

    private void stopStepTimer() {
        if (stepTimer != null && stepTimer.isRunning()) {
            stepTimer.stop();
        }
    }

    private ComparisonResult benchmarkAlgorithm(String algorithm, int[] sourceArray) {
        try {
            sort_array warmup = new sort_array(sourceArray.clone());
            createSorter(algorithm, warmup).solve();

            int runs = sourceArray.length < 100 ? 10 : 3;
            long totalTime = 0;

            for (int run = 0; run < runs; run++) {
                int[] runArray = sourceArray.clone();
                sort_array sortArray = new sort_array(runArray);
                sorter sorter = createSorter(algorithm, sortArray);

                long startTime = System.nanoTime();
                sorter.solve();
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime);
            }

            double avgTimeMs = (totalTime / (double) runs) / 1_000_000.0;
            String comparisons = estimateComparisons(algorithm, sourceArray.length);
            return new ComparisonResult(algorithm, avgTimeMs, comparisons, "SUCCESS", "-");
        } catch (Exception ex) {
            return new ComparisonResult(algorithm, -1, "N/A", "ERROR", ex.getClass().getSimpleName());
        }
    }

    private void appendResultRow(ComparisonResult result) {
        tableModel.addRow(new Object[] {
            result.algorithm,
            result.timeText,
            result.comparisons,
            result.status
        });
    }

    private void updateSummaryStats(int arraySize, String modeLabel) {
        List<ComparisonResult> successful = new ArrayList<>();
        for (ComparisonResult result : completedResults) {
            if ("SUCCESS".equals(result.status)) {
                successful.add(result);
            }
        }

        StringBuilder stats = new StringBuilder();
        stats.append("COMPARISON RESULTS (" + modeLabel + ")\n");
        stats.append("=".repeat(50)).append("\n");
        stats.append("Array Size: ").append(arraySize).append("\n");
        stats.append("Algorithms Tested: ").append(completedResults.size()).append("\n\n");

        if (successful.isEmpty()) {
            stats.append("No successful runs were completed.\n");
            statsArea.setText(stats.toString());
            return;
        }

        ComparisonResult fastest = successful.get(0);
        ComparisonResult slowest = successful.get(0);
        for (ComparisonResult result : successful) {
            if (result.timeMs < fastest.timeMs) {
                fastest = result;
            }
            if (result.timeMs > slowest.timeMs) {
                slowest = result;
            }
        }

        stats.append("Fastest: ").append(fastest.algorithm).append(" (").append(fastest.timeText).append(" ms)\n");
        stats.append("Slowest: ").append(slowest.algorithm).append(" (").append(slowest.timeText).append(" ms)\n");

        if (fastest.timeMs > 0) {
            double speedup = slowest.timeMs / fastest.timeMs;
            stats.append("Relative Speedup: ").append(String.format("%.2fx", speedup)).append("\n");
        }

        stats.append("\nALGORITHM COMPLEXITY:\n");
        stats.append("-".repeat(50)).append("\n");
        stats.append("Bubble Sort:     O(n^2) average and worst\n");
        stats.append("Insertion Sort:  O(n^2) average and worst\n");
        stats.append("Selection Sort:  O(n^2) average and worst\n");
        stats.append("Merge Sort:      O(n log n) all cases\n");
        stats.append("Tree Sort:       O(n log n) average, O(n^2) worst\n");

        if (completedResults.size() > successful.size()) {
            stats.append("\nErrors:\n");
            for (ComparisonResult result : completedResults) {
                if (!"SUCCESS".equals(result.status)) {
                    stats.append("- ").append(result.algorithm).append(": ").append(result.detail).append("\n");
                }
            }
        }

        statsArea.setText(stats.toString());
    }

    private int[] parseInput() {
        try {
            String input = inputField.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Enter at least one number.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            String[] parts = input.split(",");
            int[] array = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                array[i] = Integer.parseInt(parts[i].trim());
            }
            return array;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Invalid input. Use comma-separated integers.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private sorter createSorter(String algorithm, sort_array sortArray) {
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

    private String estimateComparisons(String algorithm, int n) {
        switch (algorithm) {
            case "Bubble Sort":
                return String.format("~%d (n^2)", n * n / 2);
            case "Insertion Sort":
                return String.format("~%d (n^2)", n * n / 4);
            case "Selection Sort":
                return String.format("~%d (n^2)", n * (n - 1) / 2);
            case "Merge Sort":
                return String.format("~%d (n log n)", (int) (n * Math.log(n) / Math.log(2)));
            case "Tree Sort":
                return String.format("~%d (n log n)", (int) (n * Math.log(n) / Math.log(2)));
            default:
                return "Unknown";
        }
    }

    private static class ComparisonResult {
        private final String algorithm;
        private final double timeMs;
        private final String timeText;
        private final String comparisons;
        private final String status;
        private final String detail;

        private ComparisonResult(String algorithm, double timeMs, String comparisons, String status, String detail) {
            this.algorithm = algorithm;
            this.timeMs = timeMs;
            this.timeText = timeMs >= 0 ? String.format("%.4f", timeMs) : "N/A";
            this.comparisons = comparisons;
            this.status = status;
            this.detail = detail;
        }
    }
}
