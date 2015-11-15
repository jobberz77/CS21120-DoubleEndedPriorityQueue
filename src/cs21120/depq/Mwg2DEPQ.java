package cs21120.depq;

/**
 *
 * @author Michal Goly
 */
public class Mwg2DEPQ implements DEPQ {

   private Interval[] heap;
   private int size;

   /**
    *
    */
   public Mwg2DEPQ() {
      heap = new Interval[1000];
      size = 0;
   }

   /**
    *
    * @return
    */
   @Override
   public Comparable inspectLeast() {
      if (size == 0) {
         return null;
      } else {
         Interval root = heap[0];
         return root.getLeft();
      }
   }

   /**
    *
    * @return
    */
   @Override
   public Comparable inspectMost() {
      if (size == 0) {
         return null;
      } else {
         Interval root = heap[0];
         return root.getRight();
      }
   }

   /**
    *
    * @param c
    */
   @Override
   public void add(Comparable c) {
      if (size == heap.length) {
         expand();
      }

      if (size == 0) {
         heap[0] = new Interval(c, c);
         size++;
      } else if (size == 1) {
         if (c.compareTo(heap[0].getLeft()) < 0) {
            heap[0].setLeft(c);
         } else {
            heap[0].setRight(c);
         }
         size++;
      } else {
         // size >= 2
         if (size % 2 == 0) {
            int newNodeIndex = (size / 2) + (size % 2);
            int parentIndex = (newNodeIndex - 1) / 2;

            // initialize placeholder for that index in the array if used 1st time
            if (heap[newNodeIndex] == null) {
               heap[newNodeIndex] = new Interval();
            }

            // if c < parent.left
            if (c.compareTo(heap[parentIndex].getLeft()) < 0) {
               minHeapInsert(c, newNodeIndex);

               // duplicate the item in the last Interval
               heap[newNodeIndex].setRight(heap[newNodeIndex].getLeft());
            } else {
               maxHeapInsert(c, newNodeIndex);

               // duplicate the item in the last Interval
               heap[newNodeIndex].setLeft(heap[newNodeIndex].getRight());
            }
         } else {
            int lastNodeIndex = (size / 2) + (size % 2) - 1;

            if (c.compareTo(heap[lastNodeIndex].getLeft()) < 0) {
               minHeapInsert(c, lastNodeIndex);
            } else {
               maxHeapInsert(c, lastNodeIndex);
            }
         }
      }
   }

   /**
    *
    * @return
    */
   @Override
   public Comparable getLeast() {
      Comparable result = null;

      if (isEmpty()) {
         return result;
      } else if (size == 1) {
         result = heap[0].getLeft();

         heap[0] = null;
         size--;

         return result;
      } else {
         result = heap[0].getLeft();

         int lastNodeIndex = (size / 2) + (size % 2) - 1;
         if (lastNodeIndex == 0) {
            // only root Interval exists
            heap[0].setLeft(heap[0].getRight());
            size--;
            return result;
         }

         // fix heap & start by moving the last element.left to the root.left
         heap[0].setLeft(heap[lastNodeIndex].getLeft());

         if (size % 2 == 0) {
            heap[lastNodeIndex].setLeft(heap[lastNodeIndex].getRight());
            size--;

            fixMinHeap(lastNodeIndex);
         } else {
            heap[lastNodeIndex] = null;
            size--;
            lastNodeIndex--;

            fixMinHeap(lastNodeIndex);
         }
         return result;
      }
   }

   /**
    *
    * @return
    */
   @Override
   public Comparable getMost() {
      Comparable result = null;

      if (isEmpty()) {
         return result;
      } else if (size == 1) {
         result = heap[0].getLeft();

         heap[0] = null;
         size--;

         return result;
      } else {
         result = heap[0].getRight();

         int lastNodeIndex = (size / 2) + (size % 2) - 1;
         if (lastNodeIndex == 0) {
            // only root Interval exists
            heap[0].setRight(heap[0].getLeft());
            size--;
            return result;
         }

         // fix heap & start by moving the last element.right to the root.right
         heap[0].setRight(heap[lastNodeIndex].getRight());

         if (size % 2 == 0) {
            heap[lastNodeIndex].setRight(heap[lastNodeIndex].getLeft());
            size--;

            fixMaxHeap(lastNodeIndex);
         } else {
            heap[lastNodeIndex] = null;
            size--;
            lastNodeIndex--;

            fixMaxHeap(lastNodeIndex);
         }
      }

      return result;
   }

   /**
    *
    * @return
    */
   @Override
   public boolean isEmpty() {
      return size == 0;
   }

   /**
    *
    * @return
    */
   @Override
   public int size() {
      return size;
   }

   /*
    * 
    */
   private void expand() {
      Interval[] newHeap = new Interval[heap.length * 2];
      System.arraycopy(heap, 0, newHeap, 0, heap.length);
      heap = newHeap;
   }

   /*
    * 
    * @param c
    * @param lastNodeIndex 
    */
   private void minHeapInsert(Comparable c, int lastNodeIndex) {

      int index = lastNodeIndex;
      while (index != 0 && c.compareTo(heap[(index - 1) / 2].getLeft()) < 0) {
         int parent = (index - 1) / 2;

         // move parent.left down (explicitly) and c up (implicitly)
         heap[index].setLeft(heap[parent].getLeft());
         index = parent;
      }

      heap[index].setLeft(c);
      size++;
   }

   /*
    * 
    * @param c
    * @param lastNodeIndex 
    */
   private void maxHeapInsert(Comparable c, int lastNodeIndex) {

      int index = lastNodeIndex;
      while (index != 0 && c.compareTo(heap[(index - 1) / 2].getRight()) > 0) {
         int parent = (index - 1) / 2;

         // move parent.right down (explicitly) and c up (implicitly)
         heap[index].setRight(heap[parent].getRight());
         index = parent;
      }

      heap[index].setRight(c);
      size++;
   }

   // pseudo code in orange notepad(reverse) page 5
   /*
    * 
    * @param lastNodeIndex 
    */
   private void fixMinHeap(int lastNodeIndex) {
      int currentNode = 0;
      int smallerChildNode;
      while (currentNode <= lastNodeIndex) {

         // compare left and right and swap if left > right
         if (heap[currentNode].getLeft()
                 .compareTo(heap[currentNode].getRight()) > 0) {
            // swap
            Comparable temp = heap[currentNode].getLeft();
            heap[currentNode].setLeft(heap[currentNode].getRight());
            heap[currentNode].setRight(temp);
         }

         // find smaller child
         int leftChildNode = currentNode * 2 + 1;
         int rightChildNode = currentNode * 2 + 2;

         // stop if there are no children
         if (rightChildNode > lastNodeIndex && heap[leftChildNode] == null) {
            break;
         }

         // cover the special case when right child is null
         if (heap[rightChildNode] == null) {
            smallerChildNode = leftChildNode;
         } else if (heap[leftChildNode].getLeft()
                 .compareTo(heap[rightChildNode].getLeft()) <= 0) {
            smallerChildNode = leftChildNode;
         } else {
            smallerChildNode = rightChildNode;
         }

         // compare current.left with smaller.left
         if (heap[currentNode].getLeft()
                 .compareTo(heap[smallerChildNode].getLeft()) > 0) {
            // swap
            Comparable temp = heap[currentNode].getLeft();
            heap[currentNode].setLeft(heap[smallerChildNode].getLeft());
            heap[smallerChildNode].setLeft(temp);
         } else {
            break;
         }

         currentNode = smallerChildNode;
      }
   }

   /*
    * 
    * @param lastNodeIndex 
    */
   private void fixMaxHeap(int lastNodeIndex) {
      int currentNode = 0;
      int largerChildNode;
      while (currentNode <= lastNodeIndex) {

         // compare left and right and swap if left > right
         if (heap[currentNode].getLeft()
                 .compareTo(heap[currentNode].getRight()) > 0) {
            // swap
            Comparable temp = heap[currentNode].getLeft();
            heap[currentNode].setLeft(heap[currentNode].getRight());
            heap[currentNode].setRight(temp);
         }

         // find smaller child
         int leftChildNode = currentNode * 2 + 1;
         int rightChildNode = currentNode * 2 + 2;

         // stop if there are no children
         if (rightChildNode > lastNodeIndex && heap[leftChildNode] == null) {
            break;
         }

         // take care of the special case when right child is null
         if (heap[rightChildNode] == null) {
            largerChildNode = leftChildNode;
         } else if (heap[leftChildNode].getRight()
                 .compareTo(heap[rightChildNode].getRight()) > 0) {
            largerChildNode = leftChildNode;
         } else {
            largerChildNode = rightChildNode;
         }

         // compare current.right with larger.right
         if (heap[currentNode].getRight()
                 .compareTo(heap[largerChildNode].getRight()) < 0) {
            // swap
            Comparable temp = heap[currentNode].getRight();
            heap[currentNode].setRight(heap[largerChildNode].getRight());
            heap[largerChildNode].setRight(temp);
         } else {
            break;
         }

         currentNode = largerChildNode;
      }
   }

   /*
    * Interval represents a single 'node' in the interval heap. It holds the
    * information about its two children. Left child should typically be smaller
    * then its right sibling. This should be enforced by the interval heap 
    * implementation.
    */
   private class Interval {

      // Left child in the interval
      private Comparable left;

      // Right child in the interval
      private Comparable right;

      /**
       * Creates a basic interval object with both of its children set to null
       */
      public Interval() {
      }

      /**
       * Creates the Interval object which represents a node in the interval heap.
       *
       * @param left The left child of the Interval (typically smaller)
       * @param right The right child of the Interval (typically larger)
       */
      public Interval(Comparable left, Comparable right) {
         this.left = left;
         this.right = right;
      }

      /**
       * @return The left child of the Interval
       */
      public Comparable getLeft() {
         return left;
      }

      /**
       * @return The right child of the Interval
       */
      public Comparable getRight() {
         return right;
      }

      /**
       * Assigns the new value to the left child
       *
       * @param left The new value to be assigned
       */
      public void setLeft(Comparable left) {
         this.left = left;
      }

      /**
       * Assigns the new value to the right child
       *
       * @param right The new value to be assigned
       */
      public void setRight(Comparable right) {
         this.right = right;
      }

   }

}
