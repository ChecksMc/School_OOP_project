package sorter;

import dataclass.sort_array;

public class miracle_sort extends sorter {

    private static final long MAX_RUNTIME_MS = 120_000;
    private static final int MAX_CHECKS = 200;

    public miracle_sort(sort_array arr) {
        super(arr);
    }

    @Override
    public int[] solve() {
        int[] array = arr.get_array();

        long start = System.currentTimeMillis();
        int checks = 0;

        while (checks < MAX_CHECKS && (System.currentTimeMillis() - start) < MAX_RUNTIME_MS) {
            if (isSorted(array)) {
                return array;
            }

            checks++;
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
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
}
