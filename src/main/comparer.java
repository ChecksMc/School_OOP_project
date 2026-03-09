package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import dataclass.sort_array;
import sorter.*;

public class comparer extends JPanel {
    private JTextField inputField;
    private JTextField arraySizeField;
    private JButton compareButton, randomArrayButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JTextArea statsArea;
    
    private String[] algorithms = {"Bubble Sort", "Insertion Sort", "Selection Sort", 
                                   "Merge Sort", "Tree Sort"};
    
    public comparer() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Array input
        JLabel inputLabel = new JLabel("Array (comma-separated):");
        inputField = new JTextField("5,3,8,1,9,2,7,4,6,10", 25);
        controlPanel.add(inputLabel);
        controlPanel.add(inputField);
        
        // Random array generator
        JLabel sizeLabel = new JLabel("Random Size:");
        arraySizeField = new JTextField("100", 5);
        randomArrayButton = new JButton("Generate Random");
        controlPanel.add(sizeLabel);
        controlPanel.add(arraySizeField);
        controlPanel.add(randomArrayButton);
        
        // Compare button
        compareButton = new JButton("Compare All Algorithms");
        compareButton.setFont(new Font("Arial", Font.BOLD, 12));
        controlPanel.add(compareButton);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Results table
        String[] columnNames = {"Algorithm", "Time (ms)", "Comparisons Est.", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultTable.setRowHeight(25);
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Performance Comparison"));
        
        // Statistics area
        statsArea = new JTextArea(10, 40);
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statsArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane statsScrollPane = new JScrollPane(statsArea);
        statsScrollPane.setBorder(BorderFactory.createTitledBorder("Statistics"));
        
        // Split pane for table and stats
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
                                               tableScrollPane, statsScrollPane);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);
        
        // Button listeners
        compareButton.addActionListener(e -> compareAlgorithms());
        randomArrayButton.addActionListener(e -> generateRandomArray());
        
        // Initial message
        statsArea.setText("Enter an array or generate a random one, then click 'Compare All Algorithms'\n" +
                         "to see performance metrics for different sorting algorithms.\n\n" +
                         "Metrics include:\n" +
                         "- Execution time in milliseconds\n" +
                         "- Estimated number of comparisons\n" +
                         "- Success status");
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
                if (i > 0) sb.append(",");
                sb.append((int)(Math.random() * 1000));
            }
            inputField.setText(sb.toString());
            
            statsArea.setText("Generated random array of size " + size + "\n" +
                            "Click 'Compare All Algorithms' to test performance.");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid number", 
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void compareAlgorithms() {
        // Parse input
        int[] originalArray = parseInput();
        if (originalArray == null) return;
        
        // Clear previous results
        tableModel.setRowCount(0);
        statsArea.setText("Running comparisons...\n\n");
        
        // Store results
        double[] times = new double[algorithms.length];
        double minTime = Double.MAX_VALUE;
        double maxTime = 0;
        String fastestAlgo = "";
        String slowestAlgo = "";
        
        // Test each algorithm
        for (int i = 0; i < algorithms.length; i++) {
            String algo = algorithms[i];
            int[] testArray = originalArray.clone();
            
            try {
                // Warm up JVM
                sort_array warmup = new sort_array(testArray.clone());
                createSorter(algo, warmup).solve();
                
                // Actual test with multiple runs for better accuracy
                int runs = testArray.length < 100 ? 10 : 3;
                long totalTime = 0;
                
                for (int run = 0; run < runs; run++) {
                    int[] runArray = originalArray.clone();
                    sort_array sortArray = new sort_array(runArray);
                    sorter sorter = createSorter(algo, sortArray);
                    
                    long startTime = System.nanoTime();
                    sorter.solve();
                    long endTime = System.nanoTime();
                    
                    totalTime += (endTime - startTime);
                }
                
                double avgTimeMs = (totalTime / runs) / 1_000_000.0;
                times[i] = avgTimeMs;
                
                // Track min/max
                if (avgTimeMs < minTime) {
                    minTime = avgTimeMs;
                    fastestAlgo = algo;
                }
                if (avgTimeMs > maxTime) {
                    maxTime = avgTimeMs;
                    slowestAlgo = algo;
                }
                
                // Estimate comparisons
                int n = testArray.length;
                String comparisons = estimateComparisons(algo, n);
                
                // Add row to table
                tableModel.addRow(new Object[]{
                    algo,
                    String.format("%.4f", avgTimeMs),
                    comparisons,
                    "✓ Success"
                });
                
            } catch (Exception e) {
                tableModel.addRow(new Object[]{
                    algo,
                    "N/A",
                    "N/A",
                    "✗ Error"
                });
            }
        }
        
        // Display statistics
        StringBuilder stats = new StringBuilder();
        stats.append("COMPARISON RESULTS\n");
        stats.append("=".repeat(50)).append("\n\n");
        stats.append("Array Size: ").append(originalArray.length).append(" elements\n\n");
        
        stats.append("Fastest Algorithm: ").append(fastestAlgo).append("\n");
        stats.append("Time: ").append(String.format("%.4f", minTime)).append(" ms\n\n");
        
        stats.append("Slowest Algorithm: ").append(slowestAlgo).append("\n");
        stats.append("Time: ").append(String.format("%.4f", maxTime)).append(" ms\n\n");
        
        if (maxTime > 0) {
            double speedup = maxTime / minTime;
            stats.append("Speed Improvement: ").append(String.format("%.2fx", speedup)).append(" faster\n\n");
        }
        
        stats.append("ALGORITHM COMPLEXITY:\n");
        stats.append("-".repeat(50)).append("\n");
        stats.append("Bubble Sort:     O(n²) average and worst\n");
        stats.append("Insertion Sort:  O(n²) average and worst\n");
        stats.append("Selection Sort:  O(n²) average and worst\n");
        stats.append("Merge Sort:      O(n log n) all cases\n");
        stats.append("Tree Sort:       O(n log n) average, O(n²) worst\n");
        
        statsArea.setText(stats.toString());
    }
    
    private int[] parseInput() {
        try {
            String input = inputField.getText().trim();
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
                return String.format("~%d (n²)", n * n / 2);
            case "Insertion Sort":
                return String.format("~%d (n²)", n * n / 4);
            case "Selection Sort":
                return String.format("~%d (n²)", n * (n - 1) / 2);
            case "Merge Sort":
                return String.format("~%d (n log n)", (int)(n * Math.log(n) / Math.log(2)));
            case "Tree Sort":
                return String.format("~%d (n log n)", (int)(n * Math.log(n) / Math.log(2)));
            default:
                return "Unknown";
        }
    }
}
