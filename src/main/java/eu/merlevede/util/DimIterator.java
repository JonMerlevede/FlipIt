package eu.merlevede.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by jonat on 26/05/2016.
 */
public class DimIterator implements Iterator<int[]> {
    private final int[] sizes;
    private final int[] currentIndex;

    public DimIterator(int[] sizes) {
        this.sizes = sizes;
        currentIndex = new int[sizes.length];
        currentIndex[0] = -1;
    }

    @Override
    public boolean hasNext() {
        for (int dim = 0; dim < sizes.length; dim++) {
            int size = sizes[dim];
            if (currentIndex[dim] < size - 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] next() {
        int dim = 0;
        currentIndex[dim]++;
        while (currentIndex[dim] >= sizes[dim]) {
            currentIndex[dim] = 0;
            dim++;
            if (dim >= sizes.length) {
                throw new NoSuchElementException();
            }
            currentIndex[dim]++;
        }
        System.out.println(Arrays.toString(currentIndex));
        return currentIndex.clone();
    }
}
