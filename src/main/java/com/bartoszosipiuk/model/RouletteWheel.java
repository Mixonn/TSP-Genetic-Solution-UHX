package com.bartoszosipiuk.model;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Bartosz Osipiuk on 2017-11-20.
 *
 * @author Bartosz Osipiuk
 */

class RouletteWheel {
    private double[] rouletteItems;

    RouletteWheel(List<Path> population){
        double sumOfFitnesses = 0;
        rouletteItems = new double[population.size()+1];

        for(Path path: population){
            sumOfFitnesses += path.getFitness();
        }

        rouletteItems[0]= 0.0;
        int nextIndex = 1;
        int prevIndex = 0;


        if(sumOfFitnesses == 0.0){
            for(int i=0; i<population.size(); i++){
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

    int pickPathIndex(){
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
