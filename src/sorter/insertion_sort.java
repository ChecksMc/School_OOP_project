package sorter;
import dataclass.sort_array;

public class insertion_sort extends sorter {

    public insertion_sort(sort_array arr) {
        super(arr);
    }

    @Override
    public int[] solve() {
        int[] array = arr.get_array();
        int n = arr.get_size();
        
        // Insertion sort implementation
        for (int i = 1; i < n; i++) {
            int key = array[i];
            int j = i - 1;
            
            // Move elements greater than key one position ahead
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = key;
        }
        
        return array;
    }
}
