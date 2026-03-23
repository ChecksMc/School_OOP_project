package sorter;
import dataclass.sort_array;

public abstract class sorter
{
    public sort_array arr;
    
    public sorter(sort_array in_arr)
    {
        this.arr = in_arr;
    }

    public abstract int[] solve();
}