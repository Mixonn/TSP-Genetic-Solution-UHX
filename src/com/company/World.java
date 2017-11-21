package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Bartosz Osipiuk on 2017-11-19.
 *
 * @author Bartosz Osipiuk
 */

public class World {
    private Graph graph;
    private int generationLimit = Integer.MAX_VALUE;
    private int DRAW_EVERY_X_POINTS=0;

    private Path bestCurrentPath;
    private Path worstCurrentPath;
    private int currentGenerationNumber;
    private int unchangesGenerations;
    private int mutationsCounter;

    private final int POPULATION_SIZE;
    private final double CROSSOVER_PROBABILITY;
    private final double MUTATION_PROBABILITY;

    private List<Path> parents;
    private List<Path> selectionPool;
    private List<Path> childs;
    private long[] values;

    public World(Graph graph, int populationSize,
                  int generationLimit, double crossoverProbability,
                  double mutationProbability){
        this.graph=graph;
        this.POPULATION_SIZE = populationSize;

        parents = new ArrayList<>(POPULATION_SIZE);

        bestCurrentPath = null;
        worstCurrentPath = null;
        unchangesGenerations = 0;
        mutationsCounter = 0;
        currentGenerationNumber = 0;
        this.generationLimit = generationLimit;

        CROSSOVER_PROBABILITY = crossoverProbability;
        MUTATION_PROBABILITY = mutationProbability;

        for(int i = 0; i< POPULATION_SIZE; i++){
            parents.add(i, this.graph.generateRandomPath());
        }

        updateBestAndWorstValue();
        updateAllFitnesses();
    }

    public void run(){
        while(currentGenerationNumber<generationLimit){
            currentGenerationNumber++;
            if(currentGenerationNumber==1){
                System.out.print(currentGenerationNumber+ " ");
                /*for(Path p:parents){
                    System.out.print(p.getPathLength()+ " ");
                }*/
                System.out.println(" ***** BEST: " + bestCurrentPath.getPathLength());
            }
            if(DRAW_EVERY_X_POINTS!=0 && currentGenerationNumber%DRAW_EVERY_X_POINTS==1){
                try{
                    drawBestPath();
                }catch (Exception e){
                    e.printStackTrace();
                    return;
                }
            }



            selection();
            evolve();

            //crossover();
            //mutation();

            updateGeneration();
            updateBestAndWorstValue();
            updateAllFitnesses();

            if(currentGenerationNumber==generationLimit){
                System.out.print(currentGenerationNumber+ " ");
                /*for(Path p:parents){
                    System.out.print(p.getPathLength()+ " ");
                }*/
                System.out.println(" ***** BEST: " + bestCurrentPath.getPathLength());
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    public void drawEveryXPoints(int i){
        DRAW_EVERY_X_POINTS = i;
    }
    private void drawBestPath(){
        Main.clearLines();
        for(int i=0; i<bestCurrentPath.getPath().size();i++){
            int temp=bestCurrentPath.getPathAt(i);
            int tempNext;
            if(i==bestCurrentPath.getPath().size()-1){
                tempNext = bestCurrentPath.getPathAt(0);
            }else{
                tempNext = bestCurrentPath.getPathAt(i+1);
            }
            Point p = graph.getPointAt(temp);
            Point pNext = graph.getPointAt(tempNext);
            Main.addLine(p.getX(), p.getY(), pNext.getX(), pNext.getY(), Color.BLACK);
        }
    }

    public Path getBestPath(){
        return bestCurrentPath;
    }


    private void updateGeneration(){
        parents = childs;
    }

    private void evolve(){
        for(int i=0; i<selectionPool.size(); i+=2){
            if(i==selectionPool.size()-1){
                childs.add(selectionPool.get(i));
                continue;
            }
            double evolveChoise = ThreadLocalRandom.current().nextDouble();

            //Crossover
            if((evolveChoise>=0.0)&&(evolveChoise<=CROSSOVER_PROBABILITY)){
                crossover(selectionPool.get(i), selectionPool.get(i+1));
            }else if((evolveChoise>CROSSOVER_PROBABILITY)&&(evolveChoise<=CROSSOVER_PROBABILITY+MUTATION_PROBABILITY)){
                mutate(selectionPool.get(i), selectionPool.get(i+1));
            }else{
                copy(selectionPool.get(i), selectionPool.get(i+1));
            }


        }
    }

    //Done
    private void selection() {
        selectionPool = new ArrayList<>(POPULATION_SIZE);
        childs = new ArrayList<>(POPULATION_SIZE);
        childs.add(bestCurrentPath);

        RouletteWheel roulette = new RouletteWheel((ArrayList<Path>)parents);
        int currSpines = 1; //We added the best value to childs
        while(currSpines<parents.size()){
            selectionPool.add(parents.get(roulette.pickPathIndex()));
            currSpines++;
        }


    }
    //Done
    private void updateAllFitnesses(){
        double worstLength = worstCurrentPath.getPathLength();
        for(Path p:parents){
            p.setFitness(worstLength-p.getPathLength());
        }
    }

    //Done
    private void updateBestAndWorstValue(){
        values = new long[POPULATION_SIZE];
        for(int i = 0; i< POPULATION_SIZE; i++){
            values[i] = parents.get(i).getPathLength();
        }
        int bestPos = getBestCurrentValueIndex();
        int worstPos = getWorstCurrentValueIndex();
        Path newBestPath = parents.get(bestPos);
        Path newWorstPath = parents.get(worstPos);

        if(bestCurrentPath == null || newBestPath.getPathLength() < bestCurrentPath.getPathLength()){
            unchangesGenerations = 0;
        }else{
            unchangesGenerations++;
        }

        bestCurrentPath = newBestPath;
        worstCurrentPath = newWorstPath;
    }

    //Done
    private int getBestCurrentValueIndex() {
        int bestPos = -1;
        long bestVal = Long.MAX_VALUE;
        for(int i = 0; i< POPULATION_SIZE; i++){
            if(values[i]<bestVal){
                bestVal = values[i];
                bestPos = i;
            }
        }
        return bestPos;
    }

    //Done
    private int getWorstCurrentValueIndex() {
        int worstPos = -1;
        long worstVal = Long.MIN_VALUE;
        for(int i = 0; i< POPULATION_SIZE; i++){
            if(values[i]>worstVal){
                worstVal = values[i];
                worstPos = i;
            }
        }
        return worstPos;
    }

    private void crossover(Path path1, Path path2) {
        int[] path1Arr = path1.getPathAsArray();
        int[] path2Arr = path2.getPathAsArray();
        CrossoverPMX cross = new CrossoverPMX(path1Arr, path2Arr);

        List<Integer> ar1 = new ArrayList<>();
        List<Integer> ar2= new ArrayList<>();
        for(int i=0; i<path1Arr.length; i++){
            ar1.add(cross.get_offspring1()[i]);
            ar2.add(cross.get_offspring2()[i]);
        }

        childs.add(new Path(ar1, graph));
        childs.add(new Path(ar2, graph));
    }

    private void mutate(Path path1, Path path2){
        path1.mutateByCircuitInversion();
        path2.mutateByCircuitInversion();
        childs.add(path1);
        childs.add(path2);
    }

    private void copy(Path path1, Path path2){
        childs.add(path1);
        childs.add(path2);
    }



}
