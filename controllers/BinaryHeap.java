package cz3004MDP.controllers;

import java.util.PriorityQueue;
import java.util.Arrays;

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
    
	public boolean add(T value){
		if (size >= array.length-1)
			array = this.resize();
		
		// insert element to bottom of heap
		size++;
		int index = size;
		array[index] = value;
		 
		bubbleUp();
		return true;
	}

	private void bubbleUp() {
		// place new element in correct position
		// TODO Auto-generated method stub
		int index = this.size;
		while(hasParent(index) && (parent(index).compareTo(array[index]) > 0)){
			
			// parent or child out of order, swap
			swap(index, parentIndex(index));
			index = parentIndex(index);
		}
	}

	private void swap(int i, int j) {
		// TODO Auto-generated method stub
		T temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	private Comparable<T> parent(int index) {
		// TODO Auto-generated method stub
		return array[parentIndex(index)];
	}

	private int parentIndex(int index) {
		// TODO Auto-generated method stub
		return index / 2;
	}

	private boolean hasParent(int index) {
		// TODO Auto-generated method stub
		return index > 1;
	}

	private T[] resize() {
		// TODO Auto-generated method stub
		return Arrays.copyOf(array, array.length*2);
	}
	
	public boolean isEmpty(){
		// check if heap is empty where size = 0
		if (size == 0)
			return true;
		else
			return false;
	}
	
	// look at the min element in the heap
	public T peek(){
		if (this.isEmpty())
			throw new IllegalStateException();
		return array[1];
	}
	
	// return and remove the min heap
	public T remove(){
		T temp = peek();
		// remove the last leaf
		array[1] = array[size];
		// decrement heap
		array[size--] = null;
		
		bubbleDown();
		return temp;
	}

	protected void bubbleDown() {
		// TODO Auto-generated method stub
		int index =1;
		
		while(hasLeftChild(index)){
			int smallerChild = leftIndex(index);
			
			if(hasRightChild(index) && array[leftIndex(index)].compareTo(array[rightIndex(index)]) > 0)
				smallerChild = rightIndex(index);
			
			if (array[index].compareTo(array[smallerChild]) > 0)
				swap(index, smallerChild);
			else
				break;
			
			index = smallerChild;
		}
	}

	private boolean hasRightChild(int index) {
		// TODO Auto-generated method stub
		return leftIndex(index) <= size;
	}
	
	private int rightIndex(int index) {
		// TODO Auto-generated method stub
		return index *2 + 1;
	}

	private int leftIndex(int index) {
		// TODO Auto-generated method stub
		return index * 2;
	}

	private boolean hasLeftChild(int index) {
		// TODO Auto-generated method stub
		return rightIndex(index) <= size;
	}

}
