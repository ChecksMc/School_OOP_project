package sorter;

import dataclass.sort_array;

public abstract class sorter {
    public sort_array arr;
    protected String storageComplexity;

    public sorter(sort_array in_arr) {
        this.arr = in_arr;
        this.storageComplexity = "O(1)";
    }

    public abstract int[] solve();

    public String getStorageComplexity() {
        return storageComplexity;
    }
}