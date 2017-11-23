
package com.company.crossover;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Peter Tran
 */
public class OX implements PathCrossover
{
    private int[] parent1;
    private int[] parent2;
    private int[] offspring1;
    private int[] offspring2;
    private int cutPoint1;
    private int cutPoint2;

    private ArrayList<Integer> outerSegmentBuildArray;

    public OX(int [] parent1, int [] parent2){
        outerSegmentBuildArray = new ArrayList<Integer>();
        this.parent1 = new int[parent1.length];
        this.parent2 = new int[parent2.length];

        for(int index = 0; index < parent1.length; index ++){
           this.parent1[index] = parent1[index];
           this.parent2[index] = parent2[index];
        }

        offspring1 = new int[parent1.length];
        offspring2 = new int[parent2.length];
        Random cP1 = new Random();
        Random cP2 = new Random();
        // Generate Random cut points, must be unique from each other //
        // cutPoint2 should be greater than cutPoint1 //
        int length = parent1.length - 1;
        cutPoint1 = cP1.nextInt(length);
        cutPoint2 = cP2.nextInt(length);

        while(cutPoint2 == cutPoint1){
             cutPoint2 = cP2.nextInt(length);
        }

        if(cutPoint1 > cutPoint2){
            int temporary = cutPoint1;
            cutPoint1 = cutPoint2;
            cutPoint2 = temporary;
         }

        crossOver(offspring1, parent1, parent2);
        crossOver(offspring2, parent2, parent1);
    }

    public int[] get_parent1(){ return parent1; }

    public int[] get_parent2(){ return parent2; }

    public int[] getOffspring1(){ return offspring1; }

    public int[] getOffspring2(){ return offspring2; }

    public int get_cutPoint1(){ return cutPoint1; } // FOR TESTING PURPOSES //

    public int get_cutPoint2(){ return cutPoint2; } // FOR TESTING PURPOSES //


    private void remove_SpecifiedElement(int elementToRemove){
        for(int index = 0; index< outerSegmentBuildArray.size(); index++){
            if(outerSegmentBuildArray.get(index) == elementToRemove){
                outerSegmentBuildArray.remove(index);
                break;
            }
        }
    }

    public void crossOver(int [] offspring, int [] parentX, int [] parentY){
        int tempIndex = 0;
        int index = cutPoint2 + 1;
        // if index - cutPoint2 + 1  == parentX.length
        // add all parentX elements directly to  outerSegmentBuildArray ArrayList.
        if(index == parentX.length) { // e.g. (1 2 3 | 4 5 6 7 8| )
            for(int x = 0; x < parentX.length; x++){
                outerSegmentBuildArray.add(parentX[x]);
            }
        }

       // Else block here concatenates segments in the following order 3rd then (1 and 2)
       // outerSegmentBuildArray
        else {
            for(index = cutPoint2 + 1; index < parentX.length; index++){
               outerSegmentBuildArray.add(tempIndex, parentX[index]);
               tempIndex++;
            }
            for(index = 0; index <= cutPoint2; index++){
               outerSegmentBuildArray.add(tempIndex, parentX[index]);
               tempIndex++;
            }

        }


        for(int indexInSegment = cutPoint1; indexInSegment <=cutPoint2; indexInSegment++){
            // for ArrayList temp remove elements that appear in parentY mid segments 
            remove_SpecifiedElement(parentY[indexInSegment]);
        }

        for(int x = cutPoint1; x <= cutPoint2; x++){
            // copy mid segment from parent designated as Y,
            // into offspring to be created.
            offspring[x] = parentY[x];
        }


        // Belows section copies remaining elements in temp into offspring
        // starting from 3rd segment of offspring.
        tempIndex = 0;
        for(int y = cutPoint2 + 1; y < offspring.length; y++){
            if(y == offspring.length){ break; }
            offspring[y] = outerSegmentBuildArray.get(tempIndex);
            tempIndex++;
        }
        
        // after end of offspring reach, copy elements from temp haven't been copied
        // into offspring from 1st segment.
        for(int z = 0; z < cutPoint1; z++){
            if(z == offspring.length){ break; }
            offspring[z] = outerSegmentBuildArray.get(tempIndex);
            tempIndex++;
        }
    }

    // Used for Testing //
    public void printOffspring(int [] offspring1, int [] offspring2){
        System.out.println(" ");
        System.out.println("Parents");
        System.out.println("");
        for(int parent1Index = 0; parent1Index < parent1.length; parent1Index++){
            System.out.print(" " + parent1[parent1Index]);
        }
        System.out.println("");
        for(int parent2Index = 0; parent2Index < parent2.length; parent2Index++){
            System.out.print(" " + parent2[parent2Index]);
        }
        System.out.println("");

        System.out.println("Offsprings");
        for(int offspring1Index = 0;
                    offspring1Index < offspring1.length; offspring1Index++){
            System.out.print(" " + offspring1[offspring1Index]);
        }
        System.out.println("");
        for(int offspring2Index = 0;
                    offspring2Index < offspring2.length; offspring2Index++){
            System.out.print(" " + offspring2[offspring2Index]);
        }
        System.out.println("");
    }
}
