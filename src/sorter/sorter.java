package sorter;
import dataclass.sort_array;

public abstract class sorter
{
    sort_array arr;
    
    public sorter(sort_array arr)
    {
        this.arr = arr;
    }

    public abstract int[] solve();
}