package Controllers;

import java.util.Arrays;
import java.util.PriorityQueue;

public class BinaryHeap <T extends Comparable<T>> extends PriorityQueue<T>{
	private static final int DEFAULT_CAPACITY = 10;
    protected T[] array;
    protected int size;
    
    //Constructor to create a new BinaryHeap
    @SuppressWarnings("unchecked")
	public BinaryHeap () {
        array = (T[])new Comparable[DEFAULT_CAPACITY];  
        size = 0;
    }
    
    //Add a new value to the min-heap
    public boolean add(T value) {
        // check array size and increase if necessary
        if (size >= array.length - 1) 
            array = this.resize();
        
        
        // insert element into heap at bottom
        size++;
        int index = size;
        array[index] = value;
        
        bubbleUp();
        return true;
    }
    
    
    //check if BH is empty
    public boolean isEmpty() {
    	if(size==0)
    		return true;
    	else
    		return false;
    }
    
    //return minimum element in the heap(does not remove)
    public T peek() {
        if (this.isEmpty()) 
            throw new IllegalStateException();
        
        return array[1];
    }

    //return and remove minimum heap in the heap
    public T remove() {
    	T result = peek();
    	
    	// remove of the last leaf/decrement
    	array[1] = array[size];
    	array[size--] = null;
    	
    	bubbleDown();
    	
    	return result;
    }
    
    //Return string representation of binaryheap
    public String toString() {
        return Arrays.toString(array);
    }

    //bubble down function to place element at the root of the heapin its correct place to maintain the min-heap order
    protected void bubbleDown() {
        int index = 1;
        
        // bubble down
        while (hasLeftChild(index)) {
            // find the smaller child
            int smallerChild = leftIndex(index);
            
            if (hasRightChild(index)
                && array[leftIndex(index)].compareTo(array[rightIndex(index)]) > 0) 
                smallerChild = rightIndex(index);
            
            if (array[index].compareTo(array[smallerChild]) > 0) 
                swap(index, smallerChild);
            else 
                break;
            
            
            // update index of where the last element is put
            index = smallerChild;
        }        
    }
    
    
    // bubble up function to place new element in it's correct position in the heap in min-heap order.
    protected void bubbleUp() {
        int index = this.size;
        
        while (hasParent(index)
                && (parent(index).compareTo(array[index]) > 0)) {
            // parent/child are out of order; swap them
            swap(index, parentIndex(index));
            index = parentIndex(index);
        }        
    }
    
    
    protected boolean hasParent(int i) {
        return i > 1;
    }
    
    
    protected int leftIndex(int i) {
        return i * 2;
    }
    
    
    protected int rightIndex(int i) {
        return i * 2 + 1;
    }
    
    
    protected boolean hasLeftChild(int i) {
        return leftIndex(i) <= size;
    }
    
    
    protected boolean hasRightChild(int i) {
        return rightIndex(i) <= size;
    }
    
    
    protected T parent(int i) {
        return array[parentIndex(i)];
    }
    
    
    protected int parentIndex(int i) {
        return i / 2;
    }
    
    
    protected T[] resize() {
        return Arrays.copyOf(array, array.length * 2);
    }
   
    protected void swap(int index1, int index2) {
        T tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;        
    }
}
