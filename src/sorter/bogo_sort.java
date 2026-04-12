package sorter;

import dataclass.sort_array;
import java.awt.Toolkit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class bogo_sort extends sorter {

    private static final int MAX_SHUFFLES = 10000000;
    private static final long POP_MIN_INTERVAL_NS = 25_000_000L;
    private static Clip popClip = null;
    private static long lastPopNanos = 0L;

    public bogo_sort(sort_array arr) {
        super(arr);
    }

    @Override
    public int[] solve() {
        int[] array = arr.get_array();
        if (array.length <= 1) {
            return array;
        }

        int attempts = 0;
        while (!isSorted(array)) {
            if (attempts >= MAX_SHUFFLES) {
                String message = "Bogosort failed after " + MAX_SHUFFLES
                        + " shuffles. Array was not sorted; closing program.";
                System.err.println(message);
                System.exit(1);
            }
            shuffle(array);
            attempts++;
        }

        return array;
    }

    private boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1] > array[i]) {
                return false;
            }
        }
        return true;
    }

    private void shuffle(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            if (i != j) {
                playPopSound();
            }
            int tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }

    private static void loadPopClip() {
        if (popClip != null) {
            return;
        }
        try {
            java.net.URL soundURL = bogo_sort.class.getResource("/main/Pop.wav");
            if (soundURL == null) {
                java.io.File file = new java.io.File("src/main/Pop.wav");
                if (file.exists()) {
                    soundURL = file.toURI().toURL();
                }
            }
            if (soundURL != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                popClip = AudioSystem.getClip();
                popClip.open(audioIn);
            }
        } catch (Exception e) {
            popClip = null;
        }
    }

    private static void playPopSound() {
        long now = System.nanoTime();
        if (now - lastPopNanos < POP_MIN_INTERVAL_NS) {
            return;
        }
        lastPopNanos = now;

        Thread soundThread = new Thread(() -> {
            try {
                loadPopClip();
                if (popClip != null) {
                    if (popClip.isRunning()) {
                        popClip.stop();
                    }
                    popClip.setFramePosition(0);
                    popClip.start();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            } catch (Exception ex) {
                Toolkit.getDefaultToolkit().beep();
            }
        }, "bogo-pop-sfx");
        soundThread.setDaemon(true);
        soundThread.start();
    }
}
