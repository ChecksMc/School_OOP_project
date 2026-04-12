# Algorithm Visualizer
This is a school project for OOP.

## Project Requirements
The goal is to implement at least:
- 5 classes
- 1 array
- 1 inheritance relationship
- 1 1...1 relationship
- 1 1...* relationship
- encapsulation
- implementation of an appealing project idea

## Current Implementation
The current project implements these requirements as follows:
- 5+ classes: `sorter`, `bubble_sort`, `selection_sort`, `merge_sort`, `insertion_sort`, `tree_sort`, `sort_array`, `visualizer`, plus additional sorters: `miracle_sort`, `bogosort`, `dictator_sort`, `thanos_sort`
- 1 array: `int[] array`
- 1 inheritance relationship: `sorter` is the parent class of all sorting algorithm classes
- 1 1...1 relationship: `sort_array` has a 1...1 relationship with `sorter`
- 1 1...* relationship: `visualizer` has a 1...* relationship with `sorter`
- encapsulation: The classes encapsulate their data and provide public methods for interaction
- implementation of an appealing project idea: The project visualizes the sorting process of different algorithms, making it an engaging way to learn about sorting algorithms.
- sound feedback: The visualizer plays a pop sound (Pop.mp3) every time an element is moved during sorting, for an enhanced interactive experience.


## Sound Effects
The visualizer plays a pop sound effect every time an element is moved (swapped or overwritten) during sorting. To enable this feature:

1. Place a `Pop.mp3` file in the `src/main/` directory (or the root directory).
2. The file must be named exactly `Pop.mp3` (case-sensitive on some systems).
3. The sound will play automatically during sorting steps if the file is present.

If `Pop.mp3` is missing, the program will fall back to a system beep.

## Current TODOs
The implementation is still in progress, and the following tasks are yet to be completed:
- Make the `visualizer` class also have a "select" or "regarding" state.
- Add support for more interesting visualization styles or algorithm comparisons.


## requirements.txt
```
# No external libraries are required for this project as it is implemented using standard Java libraries.
```

## How to run
To run the project, follow these steps:
1. Ensure you have Java installed on your system.
2. Compile the Java files in the `src` directory.
3. (Optional) Add a `pop.mp3` file to the project root or resources folder for sound effects.
4. Run the `main` class to start the visualizer.

## Project structure
```
src/
├── main/
│   ├── main.java
│   ├── visualizer.java
│   └── Pop.mp3
├── dataclass/
│   └── sort_array.java
└── sorter/
    ├── bubble_sort.java
    ├── insertion_sort.java
    ├── merge_sort.java
    ├── selection_sort.java
    ├── sorter.java
    ├── tree_sort.java
    ├── miracle_sort.java
    ├── bogosort.java
    ├── dictator_sort.java
    ├── thanos_sort.java
```

## Class Diagram
```mermaid
classDiagram
direction TB

    class visualizer {
        -int UI_SCALE
        -String[] ALGORITHMS
        -JTextField inputField
        -JButton instantButton, stepButton, resetButton, nextButton, playButton
        -Map~String,JToggleButton~ algorithmButtons
        -JPanel visualPanel
        -JLabel statusLabel
        -String selectedAlgorithm
        -int[] originalArray
        -int[] currentArray
        -int stepIndex
        -List~int[]~ sortSteps
        -List~String~ codeLines
        -List~Integer~ codeLineIndices
        -List~Integer~ codeDepths
        -JTextArea codeArea
        -Timer playTimer
        +visualizer()
        +void instantSort()
        +void startStepMode()
        +void nextStep()
        +void togglePlay()
        +void reset()
        +sorter createSorter(String algorithm, int[] sourceArray)
        +List~int[]~ generateSortSteps(String algorithm)
        +void generateBubbleSortSteps(int[] array, List~int[]~ steps)
        +void generateInsertionSortSteps(int[] array, List~int[]~ steps)
        +void generateSelectionSortSteps(int[] array, List~int[]~ steps)
        +void generateMergeSortSteps(int[] array, List~int[]~ steps)
        +void generateTreeSortSteps(int[] array, List~int[]~ steps)
        +void generateMiracleSortSteps(int[] array, List~int[]~ steps)
        +void generateBogoSortSteps(int[] array, List~int[]~ steps)
        +void generateDictatorSortSteps(int[] array, List~int[]~ steps)
        +void generateThanosSortSteps(int[] array, List~int[]~ steps)
        +void drawArray(Graphics g)
    }

    class sort_array {
        -int[] array
        -int size
        -boolean is_sorted
        +sort_array(int[] array)
        +int[] get_array()
        +int get_size()
        +boolean get_is_sorted()
    }

    class sorter {
        -sort_array arr
        +sorter(sort_array arr)
        +abstract int[] solve()
    }

    class bubble_sort {
        +bubble_sort(sort_array arr)
        +int[] solve()
    }

    class selection_sort {
        +selection_sort(sort_array arr)
        +int[] solve()
    }

    class merge_sort {
        +merge_sort(sort_array arr)
        +int[] solve()
        -void mergeSort(int[] array, int left, int right)
        -void merge(int[] array, int left, int mid, int right)
    }

    class insertion_sort {
        +insertion_sort(sort_array arr)
        +int[] solve()
    }

    class tree_sort {
        +tree_sort(sort_array arr)
        +int[] solve()
        -class TreeNode
        -TreeNode root
        -TreeNode insert(TreeNode node, int value)
        -void inOrderTraversal(TreeNode node, int[] array, int[] index)
    }

    sorter <|-- bubble_sort : extends
    sorter <|-- selection_sort : extends
    sorter <|-- merge_sort : extends
    sorter <|-- insertion_sort : extends
    sorter <|-- tree_sort : extends
    sorter *-- sort_array : composes
    bubble_sort *.. sort_array : 1 ... 1
    selection_sort *.. sort_array : 1 ... 1
    merge_sort *.. sort_array : 1 ... 2
    insertion_sort *.. sort_array : 1 ... 1
    tree_sort *.. sort_array : 1 ... 2
    visualizer *.. sorter : 1...*
    visualizer *.. dataclass.sort_array : 1...*
```




# README DE
Dies ist ein Schulprojekt für OOP.
## Projektanforderungen
Das Ziel ist es, mindestens zu implementieren:
- 5 Klassen
- 1 Array
- 1 Vererbungsbeziehung
- 1 1...1 Beziehung
- 1 1...* Beziehung
- Datenkapselung
- Umsetzung einer ansprechenden Projektidee
## Aktuelle Implementierung
Die aktuelle Implementierung erfüllt diese Anforderungen wie folgt:
- 5 Klassen: `sorter`, `bubble_sort`, `selection_sort`, `merge_sort`, `insertion_sort`, `tree_sort`, `sort_array`, `visualizer`
- 1 Array: `int[] array`
- 1 Vererbungsbeziehung: `sorter` ist die Elternklasse von `bubble_sort`, `selection_sort`, `merge_sort`, `insertion_sort` und `tree_sort`
- 1 1...1 Beziehung: `sort_array` hat eine 1...1 Beziehung mit `sorter`
- 1 1...* Beziehung: `visualizer` hat eine 1...* Beziehung mit `sorter`
- Datenkapselung: Die Klassen kapseln ihre Daten und bieten öffentliche Methoden für die Interaktion
- Umsetzung einer ansprechenden Projektidee: Das Projekt visualisiert den Sortierprozess verschiedener Algorithmen, was eine ansprechende Möglichkeit bietet, mehr über Sortieralgorithmen zu lernen.
## Aktuelle TODOs
Die Implementierung ist noch in Arbeit, und die folgenden Aufgaben müssen noch erledigt werden:
- Die `visualizer` Klasse soll auch einen "select" oder "regarding" Zustand haben.
- Implementieren weiterer Sortieralgorithmen, wie Miracle Sort, Bogosort, Dictator Sort und Thanos Sort
## requirements.txt
```
# Für dieses Projekt sind keine externen Bibliotheken erforderlich, da es mit den Standard-Java-Bibliotheken implementiert ist.
```
## Anwendungsanleitung
Um das Projekt auszuführen, folgen Sie diesen Schritten:
1. Stellen Sie sicher, dass Java auf Ihrem System installiert ist.
2. Kompilieren Sie die Java-Dateien im `src` Verzeichnis.
3. Führen Sie die `main` Klasse aus, um den Visualizer zu starten.
## Projektstruktur
```src/
├── main/
│   ├── main.java
│   ├── visualizer.java
│   └── Pop.mp3
├── dataclass/
│   └── sort_array.java
└── sorter/
    ├── bubble_sort.java
    ├── insertion_sort.java
    ├── merge_sort.java
    ├── selection_sort.java
    ├── sorter.java
    ├── tree_sort.java
    ├── miracle_sort.java
    ├── bogosort.java
    ├── dictator_sort.java
    └── thanos_sort.java
```

