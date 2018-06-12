package com.bartoszosipiuk.gui;

import org.apache.log4j.Logger;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * Created by Bartosz Osipiuk on 2017-11-20.
 *
 * @author Bartosz Osipiuk
 */

public class GraphComponent extends JComponent {
    private static final Logger log = Logger.getLogger(GraphComponent.class);
    public final int GRAPH_SIZE_MULTIPLIER;

    public GraphComponent(int graphSizeMultiplier){
        this.GRAPH_SIZE_MULTIPLIER = graphSizeMultiplier;
    }

    public GraphComponent(){
        this.GRAPH_SIZE_MULTIPLIER = 1;
    }



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

    private final LinkedList<Line> lines = new LinkedList<>();

    public void addLine(int x1, int y1, int x2, int y2, Color color) {
        lines.add(new Line(x1 * GRAPH_SIZE_MULTIPLIER, y1 * GRAPH_SIZE_MULTIPLIER,
                x2 * GRAPH_SIZE_MULTIPLIER, y2 * GRAPH_SIZE_MULTIPLIER, color));
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
            for (Line line : lines) {
                g.setColor(line.color);
                g.drawLine(line.x1, line.y1, line.x2, line.y2);
            }
            for (Circle circle : circles) {
                g.drawOval(circle.x, circle.y, circle.r, circle.r);
            }
        } catch (Exception e) {
            e.getMessage();
            log.warn("Some drawing problems (SWING): {}", e);
        }

    }

    private static class Circle {
        final int x;
        final int y;
        final int r;
        final Color color;

        Circle(int x1, int y1, int r, Color color) {
            this.x = x1;
            this.y = y1;
            this.r = r;
            this.color = color;
        }
    }

    private final LinkedList<Circle> circles = new LinkedList<>();

    public void addCircle(int x1, int x2, int r) {
        addCircle(x1 * GRAPH_SIZE_MULTIPLIER, x2 * GRAPH_SIZE_MULTIPLIER, r, Color.black);
    }

    public void addCircle(int x1, int y1, int r, Color color) {
        circles.add(new Circle(x1 * GRAPH_SIZE_MULTIPLIER, y1 * GRAPH_SIZE_MULTIPLIER, r, color));
        repaint();
    }

    public void clearCircles() {
        circles.clear();
        repaint();
    }
}