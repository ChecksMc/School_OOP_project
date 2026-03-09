import dataclass.sort_array;

abstract class solver
{
	sort_array arr;
    
    public solver(sort_array arr)
    {
        this.arr = arr;
    }

    public abstract int[] solve();
}