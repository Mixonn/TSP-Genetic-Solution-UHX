package com.company.graphic;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

import static com.company.Main.GRAPH_SIZE_MULTIPLIER;

/**
 * Created by Bartosz Osipiuk on 2017-11-20.
 *
 * @author Bartosz Osipiuk
 */

public class GraphComponent extends JComponent {
    private static class Line {
        final int x1;
        final int y1;
        final int x2;
        final int y2;
        final Color color;

        public Line(int x1, int y1, int x2, int y2, Color color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }
    }

    private final LinkedList<Line> lines = new LinkedList<Line>();

    public void addLine(int x1, int x2, int x3, int x4) {
        addLine(x1, x2, x3, x4, Color.black);
    }

    public void addLine(int x1, int y1, int x2, int y2, Color color) {
        lines.add(new Line(x1*GRAPH_SIZE_MULTIPLIER, y1*GRAPH_SIZE_MULTIPLIER,
                x2*GRAPH_SIZE_MULTIPLIER, y2*GRAPH_SIZE_MULTIPLIER, color));
        repaint();
    }

    public void clearLines() {
        lines.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        try {
            super.paintComponent(g);
            for (int i = 0; i < lines.size(); i++) {
                g.setColor(lines.get(i).color);
                g.drawLine(lines.get(i).x1, lines.get(i).y1, lines.get(i).x2, lines.get(i).y2);
            }
            for (int i = 0; i < circles.size(); i++) {
                g.drawOval(circles.get(i).x, circles.get(i).y, circles.get(i).r, circles.get(i).r);
            }
        }catch(Exception e){
            e.getMessage();
            System.err.println("Some drawing problems (SWING)");
        }

    }

    private static class Circle {
        final int x;
        final int y;
        final int r;
        final Color color;

        public Circle(int x1, int y1, int r, Color color) {
            this.x = x1;
            this.y = y1;
            this.r = r;
            this.color = color;
        }
    }

    private final LinkedList<Circle> circles = new LinkedList<Circle>();

    public void addCircle(int x1, int x2, int r) {
        addCircle(x1*GRAPH_SIZE_MULTIPLIER, x2*GRAPH_SIZE_MULTIPLIER, r, Color.black);
    }

    public void addCircle(int x1, int y1, int r, Color color) {
        circles.add(new Circle(x1*GRAPH_SIZE_MULTIPLIER, y1*GRAPH_SIZE_MULTIPLIER, r, color));
        repaint();
    }

    public void clearCircles() {
        circles.clear();
        repaint();
    }
}