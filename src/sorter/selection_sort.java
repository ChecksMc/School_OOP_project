package sorter;
import dataclass.sort_array;

public class selection_sort extends sorter {

    public selection_sort(sort_array arr) {
        super(arr);
    }

    @Override
    public int[] solve() {
        int[] array = arr.get_array();
        int n = arr.get_size();
        
        // Selection sort implementation
        for (int i = 0; i < n - 1; i++) {
            // Find the minimum element in unsorted portion
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }
            
            // Swap the found minimum element with the first element
            if (minIndex != i) {
                int temp = array[i];
                array[i] = array[minIndex];
                array[minIndex] = temp;
            }
        }
        
        return array;
    }
}
