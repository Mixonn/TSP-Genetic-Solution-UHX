package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Bartosz Osipiuk on 2017-11-19.
 *
 * @author Bartosz Osipiuk
 */

public class Path {
    private List<Integer> path;
    private Graph graph;
    private long pathLength;
    private double fitness;

    public Path(int size){
        path = new ArrayList<>(size);
        for(int i=0; i<size; i++){
            path.add(i, -1);
        }
    }

    public Path(List<Integer> path, Graph graph){
        this.path=path;
        this.graph = graph;
        updatePathLength();
    }

    public List<Integer> getSubPath(int firstIndex, int lastIndex){
        return path.subList(firstIndex, lastIndex);
    }

    public int getPathAt(int index){
        return path.get(index);
    }

    public List<Integer> getPath(){
        return Collections.unmodifiableList(path);
    }

    public int[] getPathAsArray(){
        int[] p = new int[path.size()];
        for(int i=0; i<path.size(); i++){
            p[i] = path.get(i);
        }
        return p;
    }

    public void putNewPath(List<Integer> path){
        if(this.path.size()!=path.size()){
            throw new IllegalArgumentException("New path must be this same length");
        }
        this.path=path;
    }

    private void updatePathLength() {
        long size = 0;
        for(int i=0; i<=path.size()-1; i++){
            Point first;
            Point second;
            if(i==path.size()-1){
                first = graph.getPointAt(path.get(i));
                second = graph.getPointAt(path.get(0));
            }else {
                first = graph.getPointAt(path.get(i));
                second = graph.getPointAt(path.get(i + 1));
            }

            size += first.distance(second);
        }
        pathLength = size;
    }

    public long getPathLength() {
        updatePathLength();
        return pathLength;
    }

    public void mutateByCircuitInversion(){
        int firstIndex = ThreadLocalRandom.current().nextInt(path.size());
        int secondIndex = ThreadLocalRandom.current().nextInt(path.size());

        while(firstIndex==secondIndex){
            secondIndex = secondIndex = ThreadLocalRandom.current().nextInt(path.size());
        }
        if(firstIndex>secondIndex){
            int temp = firstIndex;
            firstIndex = secondIndex;
            secondIndex = temp;
        }

        while(true){
            int dist = secondIndex-firstIndex;
            if(dist==1||dist==2){
                int temp = path.get(firstIndex);
                path.set(firstIndex, path.get(secondIndex));
                path.set(secondIndex, temp);
                break;
            }
            int temp = path.get(firstIndex);
            path.set(firstIndex, path.get(secondIndex));
            path.set(secondIndex, temp);
            firstIndex++;
            secondIndex--;
        }
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        String res="";
        for(int i=0; i<path.size(); i++){
            res+= path.get(i) + " ";
            if(i%18==17){
                res+="\n\r";
            }
        }
        return res;
    }
}
