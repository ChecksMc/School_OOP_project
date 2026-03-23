package sorter;

import dataclass.sort_array;

public class thanos_sort extends sorter {

    public thanos_sort(sort_array arr) {
        super(arr);
    }

    @Override
    public int[] solve() {
        int[] array = arr.get_array();

        // Thanos sort: repeatedly remove half until sorted or size <= 1.
        int[] working = array;
        while (working.length > 1 && !isSorted(working)) {
            int keep = Math.max(1, working.length / 2);
            int[] next = new int[keep];
            for (int i = 0; i < keep; i++) {
                next[i] = working[i];
            }
            working = next;
        }

        // Copy result back into the original array footprint.
        for (int i = 0; i < array.length; i++) {
            if (i < working.length) {
                array[i] = working[i];
            } else {
                array[i] = 0;
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
