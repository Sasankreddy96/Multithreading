import java.util.Arrays;

import java.util.Scanner;

import java.util.concurrent.Executor;

import java.util.concurrent.Executors;

import java.util.concurrent.atomic.AtomicInteger;

public class QuickSorter {
private static int N_THREADS;
private static final int FALLBACK = 2;
private static Executor pool;
public QuickSorter(int nthreads) {

       N_THREADS = nthreads;

       pool = Executors.newFixedThreadPool(N_THREADS);

}
public static <T extends Comparable<T>> void threadedQuicksort(T[] input) {

final AtomicInteger count = new AtomicInteger(1);

pool.execute(new QuicksortRunnable<T>(input, 0, input.length - 1, count));

try {

synchronized (count) {

count.wait();

}

} catch (InterruptedException e) {

e.printStackTrace();

}

}
private static <T extends Comparable<T>> int partition(T arr[], int low, int high)

{

T pivot = arr[high];

int i = (low-1);

for (int j=low; j<high; j++)

{
if (arr[j].compareTo(pivot) <= 0)

{

i++;

T temp = arr[i];

arr[i] = arr[j];

arr[j] = temp;

}

}

  

// swap arr[i+1] and arr[high] (or pivot)

T temp = arr[i+1];

arr[i+1] = arr[high];

arr[high] = temp;

  

return i+1;

}

private static <T extends Comparable<T>> void sort(T arr[], int low, int high)

{

if (low < high)

{

int pi = partition(arr, low, high);

sort(arr, low, pi-1);

sort(arr, pi+1, high);

}

}

  

public static <T extends Comparable<T>> void nomralQuickSort(T[] input) {

       sort(input, 0, input.length - 1);

}
private static class QuicksortRunnable<T extends Comparable<T>> implements Runnable {
private final T[] values;
private final int left;
private final int right;
private final AtomicInteger count;
public QuicksortRunnable(T[] values, int left, int right, AtomicInteger count) {

this.values = values;

this.left = left;

this.right = right;

this.count = count;

}
@Override

public void run() {

quicksort(left, right);

synchronized (count) {

if (count.getAndDecrement() == 1)

count.notify();

}

}
private void quicksort(int pLeft, int pRight) {

if (pLeft < pRight) {

int storeIndex = partition(pLeft, pRight);

if (count.get() >= FALLBACK * N_THREADS) {

quicksort(pLeft, storeIndex - 1);

quicksort(storeIndex + 1, pRight);

} else {

count.getAndAdd(2);

pool.execute(new QuicksortRunnable<T>(values, pLeft, storeIndex - 1, count));

pool.execute(new QuicksortRunnable<T>(values, storeIndex + 1, pRight, count));

}

}

}
private int partition(int pLeft, int pRight) {

T pivotValue = values[pRight];

int storeIndex = pLeft;

for (int i = pLeft; i < pRight; i++) {

if (values[i].compareTo(pivotValue) < 0) {

swap(i, storeIndex);

storeIndex++;

}

}

swap(storeIndex, pRight);

return storeIndex;

}
private void swap(int left, int right) {

T temp = values[left];

values[left] = values[right];

values[right] = temp;

}

}
public static void main (String args[]) {

       Scanner sc = new Scanner(System.in);

       int threads, n;

       System.out.println("Enter the number of threads: ");

       threads = sc.nextInt();

       System.out.println("Enter the number of elements in the array : ");

       n = sc.nextInt();

       Integer[] arr = new Integer[n];

       System.out.println("Enter the array elements (Space separated) : ");

       for(int i=0; i<n; i++) {

           arr[i] = sc.nextInt();

       }


       Integer[] copy = Arrays.copyOf(arr, arr.length);

          

       QuickSorter sorter = new QuickSorter(threads);

       long start, end;

       start = System.currentTimeMillis();

       sorter.threadedQuicksort(arr);

end = System.currentTimeMillis();

       System.out.println("Threaded quick sort took: " + (end-start) + " milliseconds.");

arr = Arrays.copyOf(copy, copy.length);

       start = System.currentTimeMillis();

       sorter.nomralQuickSort(arr);

       end = System.currentTimeMillis();

       System.out.println("Normal quick sort took: " + (end-start) + " milliseconds.");

}

}