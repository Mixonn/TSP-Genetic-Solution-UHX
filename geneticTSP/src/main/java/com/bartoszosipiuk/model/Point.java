package com.bartoszosipiuk.model;

/**
 * Created by Bartosz Osipiuk on 2017-11-18.
 *
 * @author Bartosz Osipiuk
 */

public class Point {
    private final int x;
    private final int y;
    private final int id;
    public Point(int id, int x, int y){
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public double distance(Point p){
        return Math.sqrt(Math.pow((this.x-p.getX()),2)+Math.pow((this.y-p.getY()),2));
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }
}
