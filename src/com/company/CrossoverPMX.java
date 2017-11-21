package com.company;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Bartosz Osipiuk on 2017-11-19.
 *
 * @author Bartosz Osipiuk
 */

public class CrossoverPMX {
    private int[] parent1;
    private int[] parent2;
    private int[] offspring1;
    private int[] offspring2;
    private int[] segment1;
    private int[] segment2;
    private int   cutPoint1;
    private int   cutPoint2;

    public CrossoverPMX(int [] parent1, int [] parent2){
        this.parent1 = new int[parent1.length];
        this.parent2 = new int[parent2.length];
        for(int index = 0; index < parent1.length; index ++){
            this.parent1[index] = parent1[index];
            this.parent2[index] = parent2[index];
        }

        offspring1 = new int[parent1.length];
        offspring2 = new int[parent2.length];
        cutPoint1 = ThreadLocalRandom.current().nextInt((parent1.length) - 1);
        cutPoint2 = ThreadLocalRandom.current().nextInt((parent1.length) - 1);

        while(cutPoint1 == cutPoint2){
            cutPoint2 = ThreadLocalRandom.current().nextInt((parent1.length) - 1);
        }

        if(cutPoint1 > cutPoint2){
            int temp = cutPoint1;
            cutPoint1 = cutPoint2;
            cutPoint2 = temp;
        }
        create_Segments(cutPoint1, cutPoint2);
        crossOver(offspring1, parent1, parent2);
        crossOver(offspring2, parent2, parent1);

    }

    public int get_cutPoint1()   { return cutPoint1;  } // For Testing Purposes //
    public int get_cutPoint2()   { return cutPoint2;  } // For Testing Purposes //

    public int[] get_segment1()  { return segment1;   } // For Testing Purposes //
    public int[] get_segment2()  { return segment2;   } // For Testing Purposes //

    public int[] get_parent1()   { return parent1;    }
    public int[] get_parent2()   { return parent2;    }

    public int[] get_offspring1(){ return offspring1; }
    public int[] get_offspring2(){ return offspring2; }

    // For an Element given by its index check that it doesn't appear twice //
    private boolean check_forDuplicates(int [] offspring, int indexOfElement){
        for(int index = 0; index < offspring.length; index++){
            if((offspring[index] == offspring[indexOfElement]) &&
                    (indexOfElement != index) ){
                return true;
            }
        }
        return false;
    }

    // If Element is Duplicated, replace it by using its mapping //
    private void sort_Duplicates(int [] offspring, int indexOfElement){
        for(int index = 0; index < segment1.length; index++){
            if(segment1[index] == offspring[indexOfElement]){
                offspring[indexOfElement] = segment2[index];
            }
            else if(segment2[index] == offspring[indexOfElement]){
                offspring[indexOfElement] = segment1[index];
            }
        }
    }

    private void create_Segments(int cutPoint1, int cutPoint2){
        int capacity_ofSegments = (cutPoint2 - cutPoint1) + 1;
        segment1 = new int[capacity_ofSegments];
        segment2 = new int[capacity_ofSegments];
        int segment1and2Index = 0;
        for(int index = 0; index < parent1.length; index++){
            if((index >= cutPoint1) && (index <= cutPoint2)){
                int x = parent1[index];  int y = parent2[index];
                segment1[segment1and2Index] = x;
                segment2[segment1and2Index] = y;
                segment1and2Index++;
            }
        }
    }

    private void insert_Segments(int[] offspring, int[] segment){
        int segmentIndex = 0;
        for(int index = 0; index < offspring.length; index++){
            if((index >= cutPoint1) && (index <= cutPoint2)){
                offspring[index] = segment[segmentIndex];
                segmentIndex++;
            }
        }
    }

    // offspring2 gets segment 1, offspring1 gets segment2 //
    public void crossOver(int [] offspring, int[] parentX, int[] parentY){
        if(offspring == offspring1){
            int[] segment = segment2;
            insert_Segments(offspring, segment);
        }

        else if(offspring == offspring2){
            int [] segment = segment1;
            insert_Segments(offspring, segment);
        }

        for(int index = 0; index < offspring.length; index++){
            if((index < cutPoint1) || (index > cutPoint2)){
                offspring[index] = parentX[index];
            }
        }

        for(int index = 0; index < offspring.length; index++){
            if((index < cutPoint1) || (index > cutPoint2)){
                while(check_forDuplicates(offspring, index)){
                    sort_Duplicates(offspring, index);
                }
            }
        }
    }
}
