package eu.merlevede.flipit.simulator.test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by jonat on 29/04/2016.
 */
public class QuickSort {
    static void quicksort(double[] unsorted) {
        quicksort(unsorted, 0, unsorted.length-1);
    }
    static void hoare(double[] unsorted) {
        hoare(unsorted, 0, unsorted.length-1);
    }

    static void swap(double[] array, int index1, int index2) {
        double t = array[index1];
        array[index1] = array[index2];
        array[index2] = t;
    }
    static Random random = new Random();
    static void quicksort(double[] array, int minIndex, int maxIndex) {
        if (minIndex >= maxIndex) { return; }
        int pivotIndex = ThreadLocalRandom.current().nextInt(minIndex,maxIndex+1);
        double pivot = array[pivotIndex]; // Invariant: array[pivotIndex] = pivot
        int upperBound = minIndex; // Invariant: values in [minIndex, upperBound) are < pivot
        int i = minIndex - 1;//  invariant: values in [upperBound, i] are >= pivot
        while(i < maxIndex) {
            i++;
            // Also: i >= upperBound, pivotIndex >= upperBound
            if (array[i] < pivot) {
                swap(array, i, upperBound);
                if (upperBound == pivotIndex) {
                    pivotIndex = i;
                }
                upperBound++;
            }
        }
        swap(array, upperBound, pivotIndex); // Value at upperBound is equal to pivot
        quicksort(array, minIndex, upperBound - 1);
        quicksort(array, upperBound + 1, maxIndex);
    }

    static void hoare(double[] array, int minIndex, int maxIndex) {
        if (minIndex >= maxIndex) { return ; }
        double pivot = array[minIndex];
        int lowerBound = minIndex-1; // Invariant: [minIndex, lowerBound] <= pivot
        int upperBound = maxIndex+1; // Invariant: [upperBound, maxIndex] >= pivot
        while(true) {
            lowerBound++; upperBound--; // we do this only to be able to write down the invariants :)
            while (array[upperBound] > pivot) {
                upperBound--;
            }
            while (array[lowerBound] < pivot) {
                lowerBound++;
            }
            if (upperBound <= lowerBound) {
                break;
            }
            swap(array, lowerBound, upperBound);
        }
        // upperBound <= lowerBound
        hoare(array, minIndex, upperBound);
        hoare(array, upperBound + 1, maxIndex);
    }

    public static void main(String[] args) {
        double[] toSort = {0,3,2,2,4,7,1,2,1,7};
        System.out.println(Arrays.toString(toSort));
        quicksort(toSort);
        System.out.println(Arrays.toString(toSort));
        hoare(toSort);
        System.out.println(Arrays.toString(toSort));
    }
}
