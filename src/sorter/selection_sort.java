package sorter;

import dataclass.sort_array;

public class selection_sort extends sorter {

    public selection_sort(sort_array arr) {
        super(arr);
        this.storageComplexity = "O(1)";
    }

    @Override
    public int[] solve() {
        int[] array = arr.get_array();
        int n = arr.get_size();

        
        for (int i = 0; i < n - 1; i++) {
            
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }

            
            if (minIndex != i) {
                int temp = array[i];
                array[i] = array[minIndex];
                array[minIndex] = temp;
            }
        }

        return array;
    }
}
