package com.company.crossover;

/**
 * @author Peter Tran
 */

public class CX implements PathCrossover
{
    private int[] parent1;
    private int[] parent2;
    private int[] offspring1;
    private int[] offspring2;

    public CX(int[] parent1, int[] parent2){
        this.parent1 = new int[parent1.length];
        this.parent2 = new int[parent2.length];
        for(int index = 0; index < parent1.length; index ++){
           this.parent1[index] = parent1[index];
           this.parent2[index] = parent2[index];
        }
        offspring1 = new int[parent1.length];
        offspring2 = new int[parent2.length];
        for(int index = 0; index < offspring1.length; index++){
            offspring1[index] = -1;
            offspring2[index] = -1;
        }
        crossOver(offspring1, parent1, parent2);
        crossOver(offspring2, parent2, parent1);
        
    }

    public int[] getOffspring1(){  return offspring1; }
    public int[] getOffspring2(){  return offspring2; }
    public int[] get_parent1()   {  return parent1;    }
    public int[] get_parent2()   {  return parent2;    }


    // (1 x x 5 ) eg. element to search is 5 in 1st parent after 1 matches to 5..
    // (5 x x x )  // its position in parent 1 is 3.
    
    private int getPosition_ofSecondParentElement_infirstParent
                                    (int [] firstParent, int element_toSearch){
        int position = 0;
        for(int index = 0; index < parent1.length; index++){
            if(firstParent[index] == element_toSearch){
               position = index;
               break;
            }
        }
        return position;
    }

    // (1 x x 5 ) eg. element to search is 1, after look for it in 2nd parent.
    // (5 x x 1 )  // 1 has already been filled so return true.

    private boolean element_already_inOffspring(int [] offspring, int element){
        for(int index = 0; index < offspring.length; index++){
            if(offspring[index] == element){
                return true;
            }
        }
        return false;
    }

    public void crossOver(int [] offspring, int [] parentX, int [] parentY){
        int index = 0;
        while(!element_already_inOffspring(offspring, parentY[index])){
           offspring[index] = parentX[index];
           int position = getPosition_ofSecondParentElement_infirstParent
                                                      (parentX, parentY[index]);
           offspring[position] = parentY[index];
           index = position;
        }

        for(int offspring_index = 0; offspring_index < offspring.length; offspring_index++){
            if(offspring[offspring_index] == -1){
                offspring[offspring_index] = parentY[offspring_index];
            }
        }
    }

     // For Testing //
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
