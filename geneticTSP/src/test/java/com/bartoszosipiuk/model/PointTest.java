package com.bartoszosipiuk.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class PointTest {

    private static final double DELTA = 1e-6;

    @Test
    public void shouldReturnCorrectCoordinatesAndId(){
        Point p = new Point(10, -23, 114);
        assertEquals(-23, p.getX());
        assertEquals(114, p.getY());
        assertEquals(10, p.getId());
    }

    @Test
    public void shouldReturnCorrectDistance(){
        Point p1 = new Point(10, -2, 2);
        Point p2 = new Point(10, 2, -2);
        assertEquals(5.6568542494923801952067548968388, p1.distance(p2), DELTA);

        p1 = new Point(10, 5, 0);
        p2 = new Point(10, 0, 0);
        assertEquals(5, p1.distance(p2), 0);

        p1 = new Point(10, 5, 2);
        p2 = new Point(10, 2, 2);
        assertEquals(3, p1.distance(p2), 0);

        p1 = new Point(10, 0, 0);
        p2 = new Point(10, 0, 0);
        assertEquals(0, p1.distance(p2), 0);
    }


}