package sorter;

import dataclass.sort_array;

public class bogo_sort extends sorter {

    private static final int MAX_SHUFFLES = 10_000;

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
        while (!isSorted(array) && attempts < MAX_SHUFFLES) {
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
            int tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
}
