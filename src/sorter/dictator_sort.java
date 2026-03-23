package sorter;

import dataclass.sort_array;

public class dictator_sort extends sorter {

    public dictator_sort(sort_array arr) {
        super(arr);
    }

    @Override
    public int[] solve() {
        int[] array = arr.get_array();

        // Dictator sort: the first element decides all values.
        if (array.length == 0) {
            return array;
        }

        int dictatorValue = array[0];
        for (int i = 1; i < array.length; i++) {
            array[i] = dictatorValue;
        }

        return array;
    }
}
