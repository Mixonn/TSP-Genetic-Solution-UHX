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

    public Graph(List<Point> points) throws MissedIdException {
        this.points = new ArrayList<>(points.size());
        for(Point p:points){
            this.points.add(p.getId(), p);
        }
        for(int i=0; i<points.size(); i++){
            if(this.points.get(i) == null){
                throw new MissedIdException("Missing ID on data path");
            }
        }
    }

    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public Point getPointAt(int index) {
        return points.get(index);
    }

    public List<Integer> generateRandomPath() {
        List<Point> listHelper = new LinkedList<>(points);
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            int randomNumber = ThreadLocalRandom.current().nextInt(0, listHelper.size());
            result.add(i, listHelper.get(randomNumber).getId());
            listHelper.remove(randomNumber);
        }
        return result;
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
