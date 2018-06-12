package com.bartoszosipiuk;

import com.bartoszosipiuk.algorithm.GeneticAlgorithmListener;
import com.bartoszosipiuk.gui.MyFrame;
import com.bartoszosipiuk.model.Path;
import com.bartoszosipiuk.model.Point;

import java.awt.*;

/**
 * Created by Bartosz Osipiuk on 2018-06-12.
 *
 * @author Bartosz Osipiuk
 */

public class AlgorithmListener implements GeneticAlgorithmListener {

    private MyFrame frame;

    public AlgorithmListener(MyFrame frame){
        this.frame = frame;
    }

    @Override
    public void drawCurrentBestPath(Path path){
        clearLines();
            for (int i = 0; i < path.getOrderedPointsList().size(); i++) {
                int temp = path.getIdAt(i);
                int tempNext;
                if (i == path.getOrderedPointsList().size() - 1) {
                    tempNext = path.getIdAt(0);
                } else {
                    tempNext = path.getIdAt(i + 1);
                }
                Point p = path.getGraph().getPointAt(temp);
                Point pNext = path.getGraph().getPointAt(tempNext);
                addLine(p.getX(), p.getY(), pNext.getX(), pNext.getY(), Color.BLACK);
            }
    }

    public void addLine(int x1, int y1, int x2, int y2, Color color) {
        frame.addLine(x1, y1, x2, y2, color);
    }

    public void clearFrame() {
        frame.removeAll();
        frame.revalidate();
        frame.repaint();
    }

    public void clearLines() {
        frame.clearLines();
    }
}
