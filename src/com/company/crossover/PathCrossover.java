package com.company.crossover;

/**
 * @author Peter Tran
 */
public interface PathCrossover
{
    public int[] getOffspring1();
    public int[] getOffspring2();

    public void crossOver(int[] offSpring, int[] parentX, int[] parentY);
 
}
