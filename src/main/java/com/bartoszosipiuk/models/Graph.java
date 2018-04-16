package com.bartoszosipiuk.models;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Bartosz Osipiuk on 2017-11-18.
 *
 * @author Bartosz Osipiuk
 */

public class Graph {
    private final List<Point> points;
    private double[][] distances = null;

    public Graph(List<Point> points) {
        this.points = points;
    }

    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public Point getPointAt(int index) {
        return points.get(index);
    }

    public Path generateRandomPath() {
        List<Point> listHelper = new LinkedList<>(points);
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            int randomNumber = ThreadLocalRandom.current().nextInt(0, listHelper.size());
            result.add(i, listHelper.get(randomNumber).getId());
            listHelper.remove(randomNumber);
        }
        return new Path(result, this);
    }

    public double[][] getAllDistances() {
        if (distances == null) {
            distances = new double[points.size()][];
            for (int i = 0; i < points.size(); i++) {
                distances[i] = new double[i+1];
                for (int j = 0; j <= i; j++) {
                    if (i == j) {
                        distances[i][j] = 0;
                    } else {
                        distances[i][j] = points.get(i).distance(points.get(j));
                    }
                }
            }
        }
        return distances;
    }

    public double getDistance(int index1, int index2){
        if(distances==null){
            getAllDistances();
        }
        return (index1>index2)? distances[index1][index2]: distances[index2][index1];
    }

    public int size() {
        return points.size();
    }
}
