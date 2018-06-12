package com.bartoszosipiuk.model;

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
    private List<Integer> orderedPointsList;
    private Graph graph;
    private double pathLength;
    private double fitness;

    public Path(List<Integer> path, Graph graph) {
        this.orderedPointsList = new ArrayList<>(path);
        this.graph = graph;
        updatePathLength();
    }

    public Path(Path pathToCopy) {
        this.orderedPointsList = new ArrayList<>(pathToCopy.getOrderedPointsList());
        this.graph = pathToCopy.graph;
        updatePathLength();
    }

    public int getIdAt(int index) {
        return orderedPointsList.get(index);
    }

    public List<Integer> getOrderedPointsList() {
        return Collections.unmodifiableList(orderedPointsList);
    }

    public int[] getPathAsArray() {
        int[] p = new int[orderedPointsList.size()];
        for (int i = 0; i < orderedPointsList.size(); i++) {
            p[i] = orderedPointsList.get(i);
        }
        return p;
    }

    private void updatePathLength() {
        double size = 0;
        for (int i = 0; i <= orderedPointsList.size() - 1; i++) {
            Point first;
            Point second;
            if (i == orderedPointsList.size() - 1) {
                first = graph.getPointAt(orderedPointsList.get(i));
                second = graph.getPointAt(orderedPointsList.get(0));
            } else {
                first = graph.getPointAt(orderedPointsList.get(i));
                second = graph.getPointAt(orderedPointsList.get(i + 1));
            }

            size += first.distance(second);
        }
        pathLength = size;
    }

    public double getPathLength() {
        updatePathLength();
        return pathLength;
    }

    public int getPathSize() {
        return orderedPointsList.size();
    }

    public void mutateByCircuitInversion() {
        int firstIndex = ThreadLocalRandom.current().nextInt(orderedPointsList.size());
        int secondIndex = ThreadLocalRandom.current().nextInt(orderedPointsList.size());

        while (firstIndex == secondIndex) {
            secondIndex = ThreadLocalRandom.current().nextInt(orderedPointsList.size());
        }
        if (firstIndex > secondIndex) {
            int temp = firstIndex;
            firstIndex = secondIndex;
            secondIndex = temp;
        }

        while (true) {
            int dist = secondIndex - firstIndex;
            if (dist == 1 || dist == 2) {
                int temp = orderedPointsList.get(firstIndex);
                orderedPointsList.set(firstIndex, orderedPointsList.get(secondIndex));
                orderedPointsList.set(secondIndex, temp);
                break;
            }
            int temp = orderedPointsList.get(firstIndex);
            orderedPointsList.set(firstIndex, orderedPointsList.get(secondIndex));
            orderedPointsList.set(secondIndex, temp);
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

    public Graph getGraph(){
        return graph; //todo copy it!
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < orderedPointsList.size(); i++) {
            res.append(orderedPointsList.get(i)).append(" ");
        }
        return res.toString();
    }
}
