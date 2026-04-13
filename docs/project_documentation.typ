#import "@preview/fletcher:0.5.8" as fletcher: diagram, node, edge
#import fletcher.shapes: diamond

#set page(margin: (x: 20mm, y: 18mm))
#set text(lang: "de")
#set heading(numbering: "1.")

= Algorithmus-Visualizer
#align(center)[
  *Ausführliche Projektdokumentation zum OOP-Projekt*
]

#v(6mm)
*Repository:* School_OOP_project  
*Datum:* 13.04.2026

#pagebreak()

= Projektzusammenfassung

Dieses Projekt implementiert eine Java-Swing-Anwendung, die Sortieralgorithmen visuell darstellt. Die Anwendung bietet zwei Hauptmodi:
- Sofortmodus: Der gewählte Algorithmus sortiert das Array unmittelbar.
- Schrittmodus: Der Sortiervorgang wird Schritt für Schritt wiedergegeben, inklusive Hervorhebung der jeweils aktiven Codezeile und optionaler Anzeige von Primär- und Hilfsarray.

Die Anwendung kombiniert klassische Algorithmen (Bubble, Insertion, Selection, Merge, Tree) mit bewusst spielerischen Varianten (Miracle, Bogosort, Dictator, Thanos), um Unterschiede und Eigenschaften zu illustrieren.

Hauptlernziele:
- Algorithmenverhalten anhand visueller Rückmeldung verstehen
- Laufzeit- und Speicherverhalten vergleichen
- OOP-Design in Java (Vererbung, Datenkapselung, Paketstruktur) praktisch anwenden

= Anforderungen und Umsetzung

Die Dokumentation beschreibt, wie die Anwendung typische Anforderungen an ein OOP-Projekt umsetzt:
- Mindestens fünf Klassen sind vorhanden (Kern- und Hilfsklassen sowie mehrere Sortierer)
- Zentrale Verwendung von Arrays (`int[]`) als Datenstruktur
- Vererbungsstruktur: `sorter` ist Basisklasse aller konkreten Sortierer
- Vorhandene 1:1- und 1:n-Beziehungen: jede Sortierinstanz arbeitet mit genau einem `sort_array`, die `visualizer`-Instanz kann mehrere Sortierer erzeugen/verwenden
- Mehrere anspruchsvolle Methoden sind implementiert (z. B. rekursive Merge-Implementierung, Baumoperationen, Schritt-Generatoren)
- Datenkapselung: wichtigste Daten werden gekapselt (z. B. private Felder in `sort_array`)

Hinweis: Das Feld `is_sorted` in `sort_array` wird derzeit von den Sortierklassen nicht aktiv gesetzt.

= Architekturübersicht

Pakete:
- `main`: Startklasse und GUI-Visualizer
- `dataclass`: Datenträgerklasse (`sort_array`)
- `sorter`: abstrakte Basisklasse und konkrete Sortieralgorithmen

Laufzeitablauf (Kurzfassung):
1. `main.main` startet die Swing-Oberfläche.
2. `visualizer` liest Benutzereingabe und Algorithmus-Auswahl ein.
3. `visualizer` erzeugt eine passende `sorter`-Instanz (Fabrik/Switch).
4. `sorter.solve()` bearbeitet eine Kopie des Arrays und gibt das Ergebnis zurück.
5. `visualizer` zeichnet Primär-/Hilfsarray und kann die generierten Schritte abspielen.

= Kernklassen

== `main.main`
Aufgabe:
- Erzeugung des Hauptfensters, Skalierung und Start des `visualizer` auf der EDT.

Besonderheiten:
- Dynamische Fenstergröße abhängig von Bildschirmauflösung
- Bei zu kleinen Bildschirmen automatische Anpassung / Maximierung

== `main.visualizer`
Aufgabe:
- UI-Controller, Rendering und Engine für Schrittwiedergabe

Teilsysteme:
- Eingabe (CSV-Parser, Zufallsarray)
- Modi (Sofort, Schritt, Play/Pause, Step-Into/Over/Out)
- Algorithmus-Subsystem (Sorter-Fabrik, Step-Generatoren)
- Rendering (ein-/zweispurige Balkendarstellung)
- Pseudocode-Anzeige mit aktiver Zeilenhervorhebung
- Feedback (pop-Sound bei Datenänderungen)

Wichtige Zustände:
- `originalArray`, `currentArray`, `currentAuxArray`
- `sortSteps`, `auxSteps`, `selectedPrimarySteps`, `selectedAuxSteps`
- `codeLineIndices`, `codeDepths` für debuggerartige Sprünge

Implementierungsnotiz:
- Detailreiche Schritt-Generatoren existieren für Bubble, Insertion, Selection, Merge und Tree.

== `dataclass.sort_array`
Aufgabe:
- Kapselung eines `int[]` mit Metadaten

Attribute:
- `private int[] array`
- `private int size`
- `private boolean is_sorted`

Methoden:
- `get_array()`, `get_size()`, `get_is_sorted()`

== `sorter.sorter` (abstrakt)
Aufgabe:
- Gemeinsame Basis für alle Sortieralgorithmen

Mitglieder:
- `public sort_array arr`
- `protected String storageComplexity`
- `abstract int[] solve()`

Jede Unterklasse legt `storageComplexity` fest.

= Implementierte Sortieralgorithmen

== 1) Bubble Sort (`sorter.bubble_sort`)
Kurz: Wiederholte Paarvergleiche und ggf. Tausch benachbarter Elemente.
Komplexität: Laufzeit O(n^2), Speicher O(1).

== 2) Insertion Sort (`sorter.insertion_sort`)
Kurz: Aufbau einer sortierten Präfix-Region, Einfügen durch Verschieben.
Komplexität: Laufzeit O(n^2) (besser bei fast sortierten Daten), Speicher O(1).

== 3) Selection Sort (`sorter.selection_sort`)
Kurz: Auswahl des Minimums im unsortierten Bereich und Tausch mit dem aktuellen Index.
Komplexität: Laufzeit O(n^2), Speicher O(1).

== 4) Merge Sort (`sorter.merge_sort`)
Kurz: Teiler & Herrsche – rekursive Aufteilung und anschließendes Mergen mit Hilfsarrays.
Komplexität: Laufzeit O(n log n), Speicher O(n).

== 5) Tree Sort (`sorter.tree_sort`)
Kurz: Einfügen in einen binären Suchbaum, anschließender In-Order-Durchlauf zur Ausgabe.
Komplexität: Erwartet O(n log n), worst-case O(n^2) bei entartetem Baum, Speicher O(n).

== 6) Miracle Sort (`sorter.miracle_sort`)
Kurz: Wiederholte Überprüfung, ob bereits sortiert – wartet und wiederholt (pädagogisch/experimentell).

== 7) Bogosort (`sorter.bogo_sort`)
Kurz: Zufälliges Mischen bis Sorte erreicht; Implementierung enthält eine harte Obergrenze für Versuche.

== 8) Dictator Sort (`sorter.dictator_sort`)
Kurz: Kopiert den ersten Wert in alle anderen Positionen (transformierend, kein klassischer Sortieralgorithmus).

== 9) Thanos Sort (`sorter.thanos_sort`)
Kurz: Reduziert das Array iterativ auf die erste Hälfte, bis Sortierung erreicht oder Länge <= 1; Rest wird mit Nullen aufgefüllt.

= Fletcher-Flussdiagramme

Die folgenden Flussdiagramme sind mit dem Fletcher-Paket erzeugt. Die Node-Beschriftungen sind auf Deutsch formuliert.

== Bubble Sort (Ablauf)
#diagram(
  node((0,0), [Start], corner-radius: 2pt),
  edge("-|>"),
  node((0,1), [Array einlesen und n bestimmen], corner-radius: 2pt),
  edge("-|>"),
  node((0,2), [Äußere Schleife i = 0..n-2], corner-radius: 2pt),
  edge("-|>"),
  node((0,3), [Innere Schleife j = 0..n-i-2], corner-radius: 2pt),
  edge("-|>"),
  node((0,4), [a[j] > a[j+1]?], shape: diamond),
  edge("-|>"),
  node((0,5), [Bei Bedarf Nachbarn tauschen], corner-radius: 2pt),
  edge("-|>"),
  node((0,6), [Wiederholen bis fertig], corner-radius: 2pt),
  edge("-|>"),
  node((0,7), [Ende], corner-radius: 2pt),
)

== Insertion Sort (Ablauf)
#diagram(
  node((0,0), [Start], corner-radius: 2pt),
  edge("-|>"),
  node((0,1), [Array einlesen und n bestimmen], corner-radius: 2pt),
  edge("-|>"),
  node((0,2), [Für i = 1..n-1], corner-radius: 2pt),
  edge("-|>"),
  node((0,3), [key = a[i], j = i-1], corner-radius: 2pt),
  edge("-|>"),
  node((0,4), [j >= 0 und a[j] > key?], shape: diamond),
  edge("-|>"),
  node((0,5), [Verschiebe a[j] nach rechts und dekrementiere j], corner-radius: 2pt),
  edge("-|>"),
  node((0,6), [Füge key bei a[j+1] ein], corner-radius: 2pt),
  edge("-|>"),
  node((0,7), [Ende], corner-radius: 2pt),
)

== Selection Sort (Ablauf)
#diagram(
  node((0,0), [Start], corner-radius: 2pt),
  edge("-|>"),
  node((0,1), [Array einlesen und n bestimmen], corner-radius: 2pt),
  edge("-|>"),
  node((0,2), [Für i = 0..n-2], corner-radius: 2pt),
  edge("-|>"),
  node((0,3), [minIndex = i], corner-radius: 2pt),
  edge("-|>"),
  node((0,4), [Scan j = i+1..n-1], corner-radius: 2pt),
  edge("-|>"),
  node((0,5), [a[j] < a[minIndex]?], shape: diamond),
  edge("-|>"),
  node((0,6), [minIndex aktualisieren], corner-radius: 2pt),
  edge("-|>"),
  node((0,7), [Tausche a[i] und a[minIndex] falls nötig], corner-radius: 2pt),
  edge("-|>"),
  node((0,8), [Ende], corner-radius: 2pt),
)

== Merge Sort (Ablauf)
#diagram(
  node((0,0), [Start], corner-radius: 2pt),
  edge("-|>"),
  node((0,1), [Rufe mergeSort(left, right) auf], corner-radius: 2pt),
  edge("-|>"),
  node((0,2), [left < right?], shape: diamond),
  edge("-|>"),
  node((0,3), [Mitte berechnen], corner-radius: 2pt),
  edge("-|>"),
  node((0,4), [Rekursiv linke Hälfte sortieren], corner-radius: 2pt),
  edge("-|>"),
  node((0,5), [Rekursiv rechte Hälfte sortieren], corner-radius: 2pt),
  edge("-|>"),
  node((0,6), [Hälften mit Hilfsarrays mergen], corner-radius: 2pt),
  edge("-|>"),
  node((0,7), [Zusammengefügtes Segment zurückgeben], corner-radius: 2pt),
  edge("-|>"),
  node((0,8), [Ende], corner-radius: 2pt),
)

== Tree Sort (Ablauf)
#diagram(
  node((0,0), [Start], corner-radius: 2pt),
  edge("-|>"),
  node((0,1), [Leeren BST-Root anlegen], corner-radius: 2pt),
  edge("-|>"),
  node((0,2), [Alle Array-Werte in den BST einfügen], corner-radius: 2pt),
  edge("-|>"),
  node((0,3), [In-Order-Durchlauf des BST], corner-radius: 2pt),
  edge("-|>"),
  node((0,4), [Traversal in sortierten Puffer schreiben], corner-radius: 2pt),
  edge("-|>"),
  node((0,5), [Sortierten Puffer zurückkopieren], corner-radius: 2pt),
  edge("-|>"),
  node((0,6), [Ende], corner-radius: 2pt),
)

== Miracle Sort (Ablauf)
#diagram(
  node((0,0), [Start], corner-radius: 2pt),
  edge("-|>"),
  node((0,1), [Timer und Prüfzähler setzen], corner-radius: 2pt),
  edge("-|>"),
  node((0,2), [Innerhalb der Grenzen?], shape: diamond),
  edge("-|>"),
  node((0,3), [Ist das Array bereits sortiert?], shape: diamond),
  edge("-|>"),
  node((0,4), [Falls nicht: 10 ms warten], corner-radius: 2pt),
  edge("-|>"),
  node((0,5), [Prüfzähler erhöhen], corner-radius: 2pt),
  edge("-|>"),
  node((0,6), [Bei Limit: unverändert zurückgeben], corner-radius: 2pt),
  edge("-|>"),
  node((0,7), [Ende], corner-radius: 2pt),
)

== Bogosort (Ablauf)
#diagram(
  node((0,0), [Start], corner-radius: 2pt),
  edge("-|>"),
  node((0,1), [Versuche = 0], corner-radius: 2pt),
  edge("-|>"),
  node((0,2), [Array sortiert?], shape: diamond),
  edge("-|>"),
  node((0,3), [Versuche >= MAX_SHUFFLES?], shape: diamond),
  edge("-|>"),
  node((0,4), [Array zufällig mischen], corner-radius: 2pt),
  edge("-|>"),
  node((0,5), [Versuche++], corner-radius: 2pt),
  edge("-|>"),
  node((0,6), [Prüfung wiederholen], corner-radius: 2pt),
  edge("-|>"),
  node((0,7), [Ende / Abbruch bei Limit], corner-radius: 2pt),
)

== Dictator Sort (Ablauf)
#diagram(
  node((0,0), [Start], corner-radius: 2pt),
  edge("-|>"),
  node((0,1), [Array leer?], shape: diamond),
  edge("-|>"),
  node((0,2), [Diktator = array[0]], corner-radius: 2pt),
  edge("-|>"),
  node((0,3), [Für i = 1..n-1], corner-radius: 2pt),
  edge("-|>"),
  node((0,4), [array[i] = Diktator], corner-radius: 2pt),
  edge("-|>"),
  node((0,5), [Transformiertes Array zurückgeben], corner-radius: 2pt),
  edge("-|>"),
  node((0,6), [Ende], corner-radius: 2pt),
)

== Thanos Sort (Ablauf)
#diagram(
  node((0,0), [Start], corner-radius: 2pt),
  edge("-|>"),
  node((0,1), [working = Originalarray], corner-radius: 2pt),
  edge("-|>"),
  node((0,2), [Länge(working) > 1 und nicht sortiert?], shape: diamond),
  edge("-|>"),
  node((0,3), [Erste Hälfte in neues Array übernehmen], corner-radius: 2pt),
  edge("-|>"),
  node((0,4), [working = verkleinertes Array], corner-radius: 2pt),
  edge("-|>"),
  node((0,5), [working zurückkopieren], corner-radius: 2pt),
  edge("-|>"),
  node((0,6), [Rest mit Nullen füllen], corner-radius: 2pt),
  edge("-|>"),
  node((0,7), [Ende], corner-radius: 2pt),
)

#pagebreak()

= Fletcher-Klassendiagramm (repräsentative Auswahl)

Das Diagramm zeigt eine Auswahl von Sortierklassen (nicht alle Unterklassen sind aufgelistet).

#diagram(
  node((0,0), [sort_array
- array: int[]
- size: int
- is_sorted: bool
+ get_array()
+ get_size()
+ get_is_sorted()], corner-radius: 2pt),

  node((4,0), [sorter (abstrakt)
+ arr: sort_array
+ storageComplexity: String
+ solve(): int[]], corner-radius: 2pt),

  node((8,-2), [bubble_sort
+ solve()], corner-radius: 2pt),
  node((8,0), [merge_sort
+ solve()
- mergeSort()
- merge()], corner-radius: 2pt),
  node((8,2), [tree_sort
+ solve()
- insert()
- inOrderTraversal()], corner-radius: 2pt),
  node((8,4), [bogo_sort
+ solve()
- shuffle()
- isSorted()], corner-radius: 2pt),

  node((4,4), [visualizer
+ createSorter()
+ generateSortSteps()
+ drawArray()
+ startStepMode()
+ nextStep()], corner-radius: 2pt),

  edge((4,0), (0,0), "->"),
  edge((8,-2), (4,0), "-|>"),
  edge((8,0), (4,0), "-|>"),
  edge((8,2), (4,0), "-|>"),
  edge((8,4), (4,0), "-|>"),
  edge((4,4), (4,0), "->"),
)

Legende:
- Pfeil zu `sorter`: Vererbung (extends)
- `visualizer -> sorter`: Laufzeitliche Erzeugung / Nutzung (1..n)
- `sorter -> sort_array`: Besitz des Datenwrappers (konzeptionell 1:1 pro Instanz)

= Anspruchsvolle Methoden (Beispiele)

Ausgewählte komplexe Implementierungen:
- `visualizer.generateMergeSortSteps`: Rekursive Schrittgenerierung mit Tiefeninformation
- `visualizer.mergeWithRecording`: Merge mit Snapshot des Hilfsarrays und Hervorhebung
- `visualizer.stepOver`: Debugger-ähnliches Step-Over mittels Code-Tiefen
- `tree_sort.insert` + `tree_sort.inOrderTraversal`: BST-Aufbau und In-Order-Ausgabe
- `merge_sort.mergeSort` + `merge_sort.merge`: Rekursives Teilen und Zusammenführen mit Hilfsarrays

Weitere komplexe Funktionen:
- `visualizer.drawDualArrays` und `drawArrayTrack` (dynamisches Rendering)
- Audio-Feedback mit Zeitabständen zur Vermeidung von Überschneidungen

= Datenkapselung

Positive Aspekte:
- `sort_array` kapselt interne Felder und stellt Getter bereit
- Viele Implementierungsdetails in `visualizer` und Sortierklassen sind privat/protected

Verbesserungshinweis:
- `sorter.arr` ist öffentlich; das Sichtbarkeitslevel auf `protected` oder private mit Getter zu reduzieren, würde die Kapselung stärken.

= Build- und Ausführungsanleitung

Voraussetzungen:
- JDK (Standard-Java- und Swing-APIs werden verwendet)

Kompilieren und Ausführen aus dem Projektverzeichnis:
```bash
mkdir -p bin
javac -d bin $(find src -name "*.java")
java -cp bin main.main
```

Hinweis zur Sounddatei:
- Für akustische Effekte legen Sie eine `Pop.wav` (oder `Pop.mp3`) im Ordner `src/main/` ab.

= Projektstärken und "Schmankerl"

Besondere Merkmale:
- GUI-basierter Visualizer mit interaktiven Karten für Algorithmen
- Debugger-ähnliche Steuerung (Step Into / Over / Out)
- Synchronisierte Pseudocode-Anzeige
- Duale Speicheransicht für Algorithmen mit Hilfsarray
- Soundeffekte zur Visualisierung von Datenbewegungen

= Bekannte Einschränkungen und Plattformhinweise

- Anzeige: Auf manchen Bildschirmkonfigurationen erscheinen Bedienelemente (insbesondere die kleinen Aktions-Buttons) nicht vollständig sichtbar; eine vollständige Darstellung wurde auf ultrabreiten Monitoren beobachtet. Auf normal- und kleineren Monitoren kann es zu abgeschnittenen Schaltflächen kommen.
- Farbgebung: Die benutzerdefinierten Farb- und Theme-Einstellungen werden unter Linux in der getesteten Umgebung erwartungsgemäß dargestellt; unter Windows wurden Inkonsistenzen beobachtet, sodass Farben teilweise nicht wie vorgesehen erscheinen.
- Sound: Die Soundwiedergabe (Pop-Effekt) ist in der aktuellen Umgebung nicht zuverlässig; in Tests zeigte sich, dass die akustischen Effekte sowohl unter Linux als auch unter Windows nicht garantiert abgespielt werden. In solchen Fällen fällt das Programm auf das System-Piepen zurück.

Diese Hinweise sollten in der Präsentation und bei Tests beachtet werden.

= Weitere Hinweise

Für eine Kurzfassung (eine DIN-A4-Seite) empfehlen wir, die Projektidee, die Architektur, die Arbeitsteilung im Team und ein oder zwei aussagekräftige Codebeispiele zusammenzufassen.

