package com.company.models;

import com.company.models.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Bartosz Osipiuk on 2017-11-20.
 *
 * @author Bartosz Osipiuk
 */

public class RouletteWheel {
    private double[] rouletteItems;
    private double sumOfFitnesses;


    public RouletteWheel(List<Path> population){
        sumOfFitnesses = 0;
        rouletteItems = new double[population.size()+1];

        for(Path path: population){
            sumOfFitnesses += path.getFitness();
        }

        rouletteItems[0]= 0.0;
        int nextIndex = 1;
        int prevIndex = 0;


        if(sumOfFitnesses == 0.0){
            for(Path path: population){
                rouletteItems[nextIndex] = 0.0;
                nextIndex++;
            }
        }else {
            for (Path path : population) {
                double selectionProb = path.getFitness() / sumOfFitnesses;
                rouletteItems[nextIndex] =
                        rouletteItems[prevIndex] + selectionProb;
                prevIndex++;
                nextIndex++;
            }
        }
    }

    public int pickPathIndex(){
        return getParentIndex(ThreadLocalRandom.current().nextDouble());
    }

    private int getParentIndex(double randomValue) {
        int parentLoc = 0;

        for(int i = 0; i < rouletteItems.length-1; i++){
            if(randomValue>= rouletteItems[i] && (randomValue<= rouletteItems[i+1])){
                return i;
            }
        }
        return parentLoc;
    }

















}