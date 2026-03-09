package dataclass;

public class sort_array
{
    private int[] array;
    private int size;
    private boolean is_sorted;

    public sort_array(int[] arr)
    {
        this.array = arr;
        this.size = arr.length;
        this.is_sorted = false;
    }

    public int[] get_array()
    {
        return this.array;
    }

    public int get_size()
    {
        return this.size;
    }

    public boolean get_is_sorted()
    {
        return this.is_sorted;
    }
}