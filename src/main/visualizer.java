package main;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import dataclass.sort_array;
import sorter.*;

public class visualizer extends JPanel {
    private static final int UI_SCALE = 4;
    private static final int AUX_EMPTY = Integer.MIN_VALUE;
    private static final String[] ALGORITHMS = {
        "Bubble Sort", "Insertion Sort", "Selection Sort", "Merge Sort", "Tree Sort"
    };

    private JTextField inputField;
    private JButton instantButton;
    private JButton stepButton;
    private JButton resetButton;
    private JButton nextButton;
    private JButton playButton;
    private JButton stepIntoButton;
    private JButton stepOverButton;
    private JButton stepOutButton;
    private JButton storageModeButton;
    private final Map<String, JToggleButton> algorithmButtons = new LinkedHashMap<>();
    private JPanel visualPanel;
    private JLabel statusLabel;

    private String selectedAlgorithm = ALGORITHMS[0];
    private boolean dualStorageMode = false;

    private int[] originalArray;
    private int[] currentArray;
    private int[] currentAuxArray;
    private int stepIndex = 0;
    private List<int[]> sortSteps;
    private List<int[]> auxSteps;
    private List<String> codeLines;
    private List<Integer> codeLineIndices;
    private List<Integer> codeDepths;
    private JTextArea codeArea;
    private Timer playTimer;

    private static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int v) {
            val = v;
        }
    }

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

        codeArea = new JTextArea();
        codeArea.setFont(new Font("Monospaced", Font.PLAIN, f(12)));
        codeArea.setEditable(false);
        codeArea.setBorder(BorderFactory.createEmptyBorder(s(6), s(6), s(6), s(6)));
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setPreferredSize(new Dimension(s(300), s(220)));

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

        add(codeScroll, BorderLayout.EAST);

        statusLabel = new JLabel("Enter an array, pick an algorithm card, then sort.");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, f(14)));
        add(statusLabel, BorderLayout.SOUTH);

        updateStorageModeButton();

        playTimer = new Timer(s(125), e -> {
            if (sortSteps != null && stepIndex < sortSteps.size() - 1) {
                nextStep();
            } else {
                stopPlay();
            }
        });
    }

    public void loadScenario(String algorithm, int[] sourceArray, boolean startInStepMode, boolean preferDualStorageView) {
        if (sourceArray == null || sourceArray.length == 0) {
            return;
        }

        if (algorithm != null && algorithmButtons.containsKey(algorithm)) {
            selectedAlgorithm = algorithm;
            JToggleButton button = algorithmButtons.get(algorithm);
            if (button != null) {
                button.setSelected(true);
            }
            updateAlgorithmCardStyles();
            updateCodeArea();
        }

        dualStorageMode = preferDualStorageView;
        updateStorageModeButton();

        inputField.setText(toCsv(sourceArray));

        if (startInStepMode) {
            startStepMode();
        } else if (parseInput()) {
            statusLabel.setText("Loaded " + selectedAlgorithm + " with " + sourceArray.length + " values.");
            visualPanel.repaint();
        }
    }

    private String toCsv(int[] sourceArray) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sourceArray.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(sourceArray[i]);
        }
        return sb.toString();
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
        stepIntoButton = new JButton("Step Into");
        stepOverButton = new JButton("Step Over");
        stepOutButton = new JButton("Step Out");
        playButton = new JButton("Play");
        storageModeButton = new JButton();

        styleActionButton(instantButton, new Color(45, 124, 246));
        styleActionButton(stepButton, new Color(0, 144, 115));
        styleActionButton(nextButton, new Color(238, 149, 0));
        styleActionButton(playButton, new Color(157, 95, 255));
        styleActionButton(stepIntoButton, new Color(34, 139, 34));
        styleActionButton(stepOverButton, new Color(85, 107, 47));
        styleActionButton(stepOutButton, new Color(184, 134, 11));
        styleActionButton(storageModeButton, new Color(73, 108, 188));
        styleActionButton(resetButton, new Color(189, 44, 44));

        nextButton.setEnabled(false);
        stepIntoButton.setEnabled(false);
        stepOverButton.setEnabled(false);
        stepOutButton.setEnabled(false);
        playButton.setEnabled(false);

        modePanel.add(instantButton);
        modePanel.add(stepButton);
        modePanel.add(nextButton);
        modePanel.add(stepIntoButton);
        modePanel.add(stepOverButton);
        modePanel.add(stepOutButton);
        modePanel.add(playButton);
        modePanel.add(storageModeButton);
        modePanel.add(resetButton);

        instantButton.addActionListener(e -> instantSort());
        stepButton.addActionListener(e -> startStepMode());
        nextButton.addActionListener(e -> nextStep());
        stepIntoButton.addActionListener(e -> stepInto());
        stepOverButton.addActionListener(e -> stepOver());
        stepOutButton.addActionListener(e -> stepOut());
        playButton.addActionListener(e -> togglePlay());
        storageModeButton.addActionListener(e -> toggleStorageMode());
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
        updateCodeArea();

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
            updateCodeArea();
            if (originalArray != null) {
                currentAuxArray = requiresAuxStorage(selectedAlgorithm) ? createEmptyAuxArray(originalArray.length) : null;
            }
            visualPanel.repaint();
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

    private void toggleStorageMode() {
        dualStorageMode = !dualStorageMode;
        updateStorageModeButton();

        if (dualStorageMode) {
            statusLabel.setText("Dual storage view enabled (shows primary + auxiliary arrays).");
        } else {
            statusLabel.setText("Single array view enabled.");
        }

        visualPanel.repaint();
    }

    private void updateStorageModeButton() {
        if (storageModeButton != null) {
            storageModeButton.setText(dualStorageMode ? "Storage View: Dual" : "Storage View: Single");
        }
    }

    private void instantSort() {
        if (!parseInput()) {
            return;
        }

        long startTime = System.nanoTime();
        sorter selectedSorter = createSorter(selectedAlgorithm, currentArray);
        currentArray = selectedSorter.solve();
        long endTime = System.nanoTime();

        if (requiresAuxStorage(selectedAlgorithm)) {
            currentAuxArray = originalArray.clone();
        } else {
            currentAuxArray = null;
        }

        double timeMs = (endTime - startTime) / 1_000_000.0;
        statusLabel.setText("Sorted using " + selectedAlgorithm + " in " + String.format("%.3f", timeMs) + " ms");

        nextButton.setEnabled(false);
        stepIntoButton.setEnabled(false);
        stepOverButton.setEnabled(false);
        stepOutButton.setEnabled(false);
        playButton.setEnabled(false);
        stopPlay();
        codeArea.select(0, 0);
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
        currentAuxArray = getAuxStep(stepIndex);
        statusLabel.setText("Step 1 of " + sortSteps.size() + " - " + selectedAlgorithm);

        nextButton.setEnabled(true);
        stepIntoButton.setEnabled(true);
        stepOverButton.setEnabled(true);
        stepOutButton.setEnabled(true);
        playButton.setEnabled(true);
        playButton.setText("Play");
        stopPlay();
        updateCodeArea();
        if (codeLineIndices != null && !codeLineIndices.isEmpty()) {
            highlightCodeLine(codeLineIndices.get(0));
        }
        visualPanel.repaint();
    }

    private int[] getAuxStep(int index) {
        if (auxSteps == null || index < 0 || index >= auxSteps.size()) {
            return null;
        }
        int[] aux = auxSteps.get(index);
        return aux == null ? null : aux.clone();
    }

    private void nextStep() {
        if (sortSteps == null || stepIndex >= sortSteps.size() - 1) {
            return;
        }

        stepIndex++;
        currentArray = sortSteps.get(stepIndex).clone();
        currentAuxArray = getAuxStep(stepIndex);

        if (codeLineIndices != null && stepIndex < codeLineIndices.size()) {
            highlightCodeLine(codeLineIndices.get(stepIndex));
        }
        statusLabel.setText("Step " + (stepIndex + 1) + " of " + sortSteps.size() + " - " + selectedAlgorithm);
        visualPanel.repaint();

        if (stepIndex >= sortSteps.size() - 1) {
            stopPlay();
            nextButton.setEnabled(false);
            stepIntoButton.setEnabled(false);
            stepOverButton.setEnabled(false);
            stepOutButton.setEnabled(false);
            statusLabel.setText("Sorting complete! Final array is shown.");
        }
    }

    private void stepInto() {
        nextStep();
    }

    private void stepOver() {
        if (sortSteps == null || stepIndex >= sortSteps.size() - 1) {
            return;
        }
        if (codeDepths == null || codeDepths.size() <= stepIndex) {
            nextStep();
            return;
        }

        int currentDepth = codeDepths.get(stepIndex);
        int j = stepIndex + 1;

        if (j < codeDepths.size() && codeDepths.get(j) <= currentDepth) {
            nextStep();
            return;
        }

        while (j < codeDepths.size() && codeDepths.get(j) > currentDepth) {
            j++;
        }

        if (j >= sortSteps.size()) {
            j = sortSteps.size() - 1;
        }

        stepIndex = j;
        currentArray = sortSteps.get(stepIndex).clone();
        currentAuxArray = getAuxStep(stepIndex);

        if (codeLineIndices != null && stepIndex < codeLineIndices.size()) {
            highlightCodeLine(codeLineIndices.get(stepIndex));
        }
        statusLabel.setText("Step " + (stepIndex + 1) + " of " + sortSteps.size() + " - " + selectedAlgorithm);
        visualPanel.repaint();

        if (stepIndex >= sortSteps.size() - 1) {
            stopPlay();
            nextButton.setEnabled(false);
            stepIntoButton.setEnabled(false);
            stepOverButton.setEnabled(false);
            stepOutButton.setEnabled(false);
            statusLabel.setText("Sorting complete! Final array is shown.");
        }
    }

    private void stepOut() {
        if (sortSteps == null || stepIndex >= sortSteps.size() - 1) {
            return;
        }
        if (codeDepths == null || codeDepths.size() <= stepIndex) {
            nextStep();
            return;
        }

        int currentDepth = codeDepths.get(stepIndex);
        if (currentDepth <= 0) {
            nextStep();
            return;
        }

        int j = stepIndex + 1;
        while (j < codeDepths.size() && codeDepths.get(j) >= currentDepth) {
            j++;
        }
        if (j >= sortSteps.size()) {
            j = sortSteps.size() - 1;
        }

        stepIndex = j;
        currentArray = sortSteps.get(stepIndex).clone();
        currentAuxArray = getAuxStep(stepIndex);

        if (codeLineIndices != null && stepIndex < codeLineIndices.size()) {
            highlightCodeLine(codeLineIndices.get(stepIndex));
        }
        statusLabel.setText("Step " + (stepIndex + 1) + " of " + sortSteps.size() + " - " + selectedAlgorithm);
        visualPanel.repaint();

        if (stepIndex >= sortSteps.size() - 1) {
            stopPlay();
            nextButton.setEnabled(false);
            stepIntoButton.setEnabled(false);
            stepOverButton.setEnabled(false);
            stepOutButton.setEnabled(false);
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
        auxSteps = null;
        codeLines = null;
        codeLineIndices = null;
        codeDepths = null;
        currentArray = originalArray != null ? originalArray.clone() : null;
        currentAuxArray = originalArray != null && requiresAuxStorage(selectedAlgorithm)
            ? createEmptyAuxArray(originalArray.length)
            : null;

        nextButton.setEnabled(false);
        stepIntoButton.setEnabled(false);
        stepOverButton.setEnabled(false);
        stepOutButton.setEnabled(false);
        playButton.setEnabled(false);
        playTimer.stop();
        playButton.setText("Play");
        statusLabel.setText("Reset complete. Pick an algorithm and sort again.");
        codeArea.select(0, 0);
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

            currentAuxArray = requiresAuxStorage(selectedAlgorithm)
                ? createEmptyAuxArray(parts.length)
                : null;

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

    private boolean requiresAuxStorage(String algorithm) {
        return "Merge Sort".equals(algorithm) || "Tree Sort".equals(algorithm);
    }

    private List<int[]> generateSortSteps(String algorithm) {
        List<int[]> steps = new ArrayList<>();
        auxSteps = new ArrayList<>();
        codeLines = getCodeLinesForAlgorithm(algorithm);
        codeLineIndices = new ArrayList<>();
        codeDepths = new ArrayList<>();

        int[] array = originalArray.clone();
        int[] initialAux = requiresAuxStorage(algorithm) ? createEmptyAuxArray(array.length) : null;
        recordStep(steps, array, initialAux, 0, 0);

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
            case "Merge Sort":
                generateMergeSortSteps(array, steps);
                break;
            case "Tree Sort":
                generateTreeSortSteps(array, steps);
                break;
            default:
                sorter s = createSorter(algorithm, array);
                int[] sorted = s.solve();
                recordStep(steps, sorted, null, 0, 0);
                break;
        }

        return steps;
    }

    private void recordStep(List<int[]> steps, int[] primary, int[] secondary, int codeLineIndex, int depth) {
        steps.add(primary.clone());
        auxSteps.add(secondary == null ? null : secondary.clone());
        codeLineIndices.add(codeLineIndex);
        codeDepths.add(depth);
    }

    private int[] createEmptyAuxArray(int size) {
        int[] aux = new int[size];
        Arrays.fill(aux, AUX_EMPTY);
        return aux;
    }

    private void generateBubbleSortSteps(int[] array, List<int[]> steps) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            recordStep(steps, array, null, 1, 0);
            for (int j = 0; j < n - i - 1; j++) {
                recordStep(steps, array, null, 2, 0);
                recordStep(steps, array, null, 3, 0);
                if (array[j] > array[j + 1]) {
                    recordStep(steps, array, null, 4, 0);
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    recordStep(steps, array, null, 6, 0);
                }
            }
        }
    }

    private void generateInsertionSortSteps(int[] array, List<int[]> steps) {
        int n = array.length;
        for (int i = 1; i < n; i++) {
            recordStep(steps, array, null, 1, 0);
            int key = array[i];
            recordStep(steps, array, null, 2, 0);
            int j = i - 1;
            recordStep(steps, array, null, 3, 0);
            while (j >= 0 && array[j] > key) {
                recordStep(steps, array, null, 4, 0);
                array[j + 1] = array[j];
                recordStep(steps, array, null, 5, 0);
                j = j - 1;
                recordStep(steps, array, null, 6, 0);
            }
            array[j + 1] = key;
            recordStep(steps, array, null, 8, 0);
        }
    }

    private void generateSelectionSortSteps(int[] array, List<int[]> steps) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            recordStep(steps, array, null, 1, 0);
            int minIndex = i;
            recordStep(steps, array, null, 2, 0);
            for (int j = i + 1; j < n; j++) {
                recordStep(steps, array, null, 3, 0);
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                    recordStep(steps, array, null, 5, 0);
                }
            }
            if (minIndex != i) {
                recordStep(steps, array, null, 8, 0);
                int temp = array[i];
                array[i] = array[minIndex];
                array[minIndex] = temp;
                recordStep(steps, array, null, 11, 0);
            }
        }
    }

    private void generateMergeSortSteps(int[] array, List<int[]> steps) {
        if (array.length <= 1) {
            recordStep(steps, array, createEmptyAuxArray(array.length), 0, 0);
            return;
        }
        mergeSortRec(array, 0, array.length - 1, steps, 0);
    }

    private void mergeSortRec(int[] array, int left, int right, List<int[]> steps, int depth) {
        recordStep(steps, array, createEmptyAuxArray(array.length), 1, depth);
        if (left < right) {
            int mid = left + (right - left) / 2;

            recordStep(steps, array, createEmptyAuxArray(array.length), 3, depth);
            mergeSortRec(array, left, mid, steps, depth + 1);

            recordStep(steps, array, createEmptyAuxArray(array.length), 4, depth);
            mergeSortRec(array, mid + 1, right, steps, depth + 1);

            mergeWithRecording(array, left, mid, right, steps, depth);
            recordStep(steps, array, createEmptyAuxArray(array.length), 6, depth);
        } else {
            recordStep(steps, array, createEmptyAuxArray(array.length), 0, depth);
        }
    }

    private void mergeWithRecording(int[] array, int left, int mid, int right, List<int[]> steps, int depth) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] leftArr = new int[n1];
        int[] rightArr = new int[n2];
        for (int i = 0; i < n1; i++) {
            leftArr[i] = array[left + i];
        }
        for (int j = 0; j < n2; j++) {
            rightArr[j] = array[mid + 1 + j];
        }

        int[] auxSnapshot = createEmptyAuxArray(array.length);
        for (int i = 0; i < n1; i++) {
            auxSnapshot[left + i] = leftArr[i];
        }
        for (int j = 0; j < n2; j++) {
            auxSnapshot[mid + 1 + j] = rightArr[j];
        }

        int i = 0;
        int j = 0;
        int k = left;

        recordStep(steps, array, auxSnapshot, 8, depth);

        while (i < n1 && j < n2) {
            if (leftArr[i] <= rightArr[j]) {
                array[k] = leftArr[i];
                auxSnapshot[left + i] = AUX_EMPTY;
                i++;
            } else {
                array[k] = rightArr[j];
                auxSnapshot[mid + 1 + j] = AUX_EMPTY;
                j++;
            }
            k++;
            recordStep(steps, array, auxSnapshot, 8, depth);
        }

        while (i < n1) {
            array[k] = leftArr[i];
            auxSnapshot[left + i] = AUX_EMPTY;
            i++;
            k++;
            recordStep(steps, array, auxSnapshot, 8, depth);
        }

        while (j < n2) {
            array[k] = rightArr[j];
            auxSnapshot[mid + 1 + j] = AUX_EMPTY;
            j++;
            k++;
            recordStep(steps, array, auxSnapshot, 8, depth);
        }
    }

    private void generateTreeSortSteps(int[] array, List<int[]> steps) {
        TreeNode root = null;
        int[] sorted = createEmptyAuxArray(array.length);

        for (int v : array) {
            root = insertNode(root, v);
            recordStep(steps, array, sorted, 1, 0);
        }

        int[] idx = new int[] {0};
        inOrderRecord(root, sorted, idx, steps, array);

        int[] auxWorking = sorted.clone();
        for (int i = 0; i < array.length; i++) {
            if (auxWorking[i] == AUX_EMPTY) {
                continue;
            }
            array[i] = auxWorking[i];
            auxWorking[i] = AUX_EMPTY;
            recordStep(steps, array, auxWorking, 3, 0);
        }
    }

    private TreeNode insertNode(TreeNode node, int value) {
        if (node == null) {
            return new TreeNode(value);
        }
        if (value < node.val) {
            node.left = insertNode(node.left, value);
        } else {
            node.right = insertNode(node.right, value);
        }
        return node;
    }

    private void inOrderRecord(TreeNode node, int[] sorted, int[] idx, List<int[]> steps, int[] primaryArray) {
        if (node == null) {
            return;
        }
        inOrderRecord(node.left, sorted, idx, steps, primaryArray);
        sorted[idx[0]] = node.val;
        idx[0]++;
        recordStep(steps, primaryArray, sorted, 2, 0);
        inOrderRecord(node.right, sorted, idx, steps, primaryArray);
    }

    private void updateCodeArea() {
        codeLines = getCodeLinesForAlgorithm(selectedAlgorithm);
        StringBuilder sb = new StringBuilder();
        for (String line : codeLines) {
            sb.append(line).append("\n");
        }
        codeArea.setText(sb.toString());
        codeArea.setCaretPosition(0);
        codeArea.select(0, 0);
    }

    private List<String> getCodeLinesForAlgorithm(String algorithm) {
        List<String> lines = new ArrayList<>();
        switch (algorithm) {
            case "Bubble Sort":
                lines.add("int n = array.length;");
                lines.add("for (int i = 0; i < n - 1; i++) {");
                lines.add("  for (int j = 0; j < n - i - 1; j++) {");
                lines.add("    if (array[j] > array[j + 1]) {");
                lines.add("      int temp = array[j];");
                lines.add("      array[j] = array[j + 1];");
                lines.add("      array[j + 1] = temp;");
                lines.add("    }");
                lines.add("  }");
                lines.add("}");
                break;
            case "Insertion Sort":
                lines.add("int n = array.length;");
                lines.add("for (int i = 1; i < n; i++) {");
                lines.add("  int key = array[i];");
                lines.add("  int j = i - 1;");
                lines.add("  while (j >= 0 && array[j] > key) {");
                lines.add("    array[j + 1] = array[j];");
                lines.add("    j = j - 1;");
                lines.add("  }");
                lines.add("  array[j + 1] = key;");
                lines.add("}");
                break;
            case "Selection Sort":
                lines.add("int n = array.length;");
                lines.add("for (int i = 0; i < n - 1; i++) {");
                lines.add("  int minIndex = i;");
                lines.add("  for (int j = i + 1; j < n; j++) {");
                lines.add("    if (array[j] < array[minIndex]) {");
                lines.add("      minIndex = j;");
                lines.add("    }");
                lines.add("  }");
                lines.add("  if (minIndex != i) {");
                lines.add("    int temp = array[i];");
                lines.add("    array[i] = array[minIndex];");
                lines.add("    array[minIndex] = temp;");
                lines.add("  }");
                lines.add("}");
                break;
            case "Merge Sort":
                lines.add("if (left >= right) return;");
                lines.add("int mid = left + (right - left) / 2;");
                lines.add("mergeSort(left, mid);");
                lines.add("mergeSort(left, mid);");
                lines.add("mergeSort(mid + 1, right);");
                lines.add("merge(left, mid, right);");
                lines.add("return;");
                lines.add("copy ranges into aux arrays;");
                lines.add("array[k] = next value from aux;");
                break;
            case "Tree Sort":
                lines.add("TreeNode root = null;");
                lines.add("root = insert(root, value);");
                lines.add("sorted[idx++] = node.val; // in-order");
                lines.add("array[i] = sorted[i];");
                break;
            default:
                lines.add("(No line-level pseudocode available for this algorithm)");
                break;
        }
        return lines;
    }

    private void highlightCodeLine(int lineIndex) {
        if (codeArea == null || codeLines == null || lineIndex < 0 || lineIndex >= codeLines.size()) {
            return;
        }
        try {
            int start = codeArea.getLineStartOffset(lineIndex);
            int end = codeArea.getLineEndOffset(lineIndex);
            codeArea.requestFocusInWindow();
            codeArea.select(start, end);
        } catch (BadLocationException e) {
            // Ignore selection glitches from rapid timer-driven updates.
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

        if (dualStorageMode && requiresAuxStorage(selectedAlgorithm)) {
            drawDualArrays(g2d, width, height);
        } else {
            drawArrayTrack(g2d, currentArray, s(8), Math.max(s(20), height - s(16)), new Color(70, 130, 180), "Primary Array");
        }
    }

    private void drawDualArrays(Graphics2D g2d, int width, int height) {
        int gap = s(18);
        int available = Math.max(s(40), height - s(24));
        int eachTrackHeight = Math.max(s(20), (available - gap) / 2);
        int topY = s(8);
        int bottomY = topY + eachTrackHeight + gap;

        int[] auxToDraw = currentAuxArray != null ? currentAuxArray : createEmptyAuxArray(currentArray.length);

        drawArrayTrack(g2d, currentArray, topY, eachTrackHeight, new Color(70, 130, 180), "Primary Array");
        drawArrayTrack(g2d, auxToDraw, bottomY, eachTrackHeight, new Color(230, 125, 50), "Auxiliary Array (O(2n) storage)");

        g2d.setColor(new Color(80, 80, 80));
        g2d.drawLine(s(6), bottomY - (gap / 2), width - s(6), bottomY - (gap / 2));
    }

    private void drawArrayTrack(Graphics2D g2d, int[] values, int yStart, int trackHeight, Color fillColor, String label) {
        int width = visualPanel.getWidth();
        int labelSpace = s(14);
        int valuesY = yStart + labelSpace;
        int valuesHeight = Math.max(s(10), trackHeight - s(24));

        g2d.setColor(new Color(32, 32, 32));
        g2d.setFont(new Font("SansSerif", Font.BOLD, f(10)));
        g2d.drawString(label, s(8), yStart + s(10));

        int barWidth = Math.max(s(10), (width - s(40)) / Math.max(1, values.length));
        int startX = (width - (barWidth * values.length)) / 2;
        int maxValue = getMaxValue(values);

        for (int i = 0; i < values.length; i++) {
            int x = startX + i * barWidth;
            int slotWidth = Math.max(1, barWidth - s(2));
            int val = values[i];

            if (val == AUX_EMPTY) {
                int slotHeight = Math.max(s(3), valuesHeight / 8);
                int y = valuesY + valuesHeight - slotHeight;
                g2d.setColor(new Color(229, 229, 229));
                g2d.fillRect(x, y, slotWidth, slotHeight);
                g2d.setColor(new Color(180, 180, 180));
                g2d.drawRect(x, y, slotWidth, slotHeight);
                continue;
            }

            int barHeight = (int) ((Math.abs(val) / (double) Math.max(1, maxValue)) * valuesHeight);
            barHeight = Math.max(s(1), barHeight);
            int y = valuesY + valuesHeight - barHeight;

            g2d.setColor(fillColor);
            g2d.fillRect(x, y, slotWidth, barHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, slotWidth, barHeight);

            if (values.length <= 24) {
                g2d.setFont(new Font("SansSerif", Font.PLAIN, f(9)));
                String valueText = String.valueOf(val);
                int textWidth = g2d.getFontMetrics().stringWidth(valueText);
                int tx = x + Math.max(0, (slotWidth - textWidth) / 2);
                int ty = valuesY + valuesHeight + s(10);
                g2d.drawString(valueText, tx, ty);
            }
        }
    }

    private int getMaxValue(int[] array) {
        int max = 1;
        for (int val : array) {
            if (val == AUX_EMPTY) {
                continue;
            }
            max = Math.max(max, Math.abs(val));
        }
        return max;
    }
}
