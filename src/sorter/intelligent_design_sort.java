package sorter;

import dataclass.sort_array;

public class intelligent_design_sort extends sorter {

    public intelligent_design_sort(sort_array arr) {
        super(arr);
    }

    @Override
    public int[] solve() {
        int[] array = arr.get_array();

        for (int i = 0; i < array.length - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[minIdx]) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                int tmp = array[i];
                array[i] = array[minIdx];
                array[minIdx] = tmp;
            }
        }

        return array;
    }
}
