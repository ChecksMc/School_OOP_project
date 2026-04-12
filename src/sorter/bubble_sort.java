package sorter;

import dataclass.sort_array;

public class bubble_sort extends sorter {

    public bubble_sort(sort_array arr) {
        super(arr);
        this.storageComplexity = "O(1)";
    }

    @Override
    public int[] solve() {
        int[] array = arr.get_array();
        int n = arr.get_size();

        
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }

        return array;
    }
}
