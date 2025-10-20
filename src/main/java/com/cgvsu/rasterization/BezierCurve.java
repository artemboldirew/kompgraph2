package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BezierCurve {
    private List<Point> segmentPoints;
    private List<Point> points;
    private int segments;
    private GraphicsContext gr;
    int width = 5;
    private Color color;
    private boolean isActive = false;

    public BezierCurve(Point p1, Point p2) {

    }

    public BezierCurve(GraphicsContext gr, List<Point> points, int segments, int width, Color color) {
        this.gr = gr;
        this.points = points;
        this.segments = segments;
        this.width = width;
        this.color = color;
        segmentPoints = generateBezierCurveOptimized(points, segments);
    }

    public void draw() {
        DrawUtil.drawCurve(gr, segmentPoints, width, color);
        if (isActive) {
            drawMainPoints();
            DrawUtil.drawCurve(gr, points, 1, Color.BLUE);
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public static List<Point> generateBezierCurveOptimized(List<Point> controlPoints, int segments) {
        if (controlPoints.size() < 2) {
            throw new IllegalArgumentException("Нужно минимум 2 контрольные точки");
        }
        List<Point> curvePoints = new ArrayList<>();
        int n = controlPoints.size() - 1;
        double[] coefficients = new double[n + 1];
        for (int i = 0; i <= n; i++) {
            coefficients[i] = binomialCoefficient(n, i);
        }
        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments;
            double x = 0;
            double y = 0;
            for (int j = 0; j <= n; j++) {
                double blend = coefficients[j] * Math.pow(1 - t, n - j) * Math.pow(t, j);
                x += blend * controlPoints.get(j).x;
                y += blend * controlPoints.get(j).y;
            }
            curvePoints.add(new Point((int) x, (int) y));
        }
        return curvePoints;
    }

    private static double binomialCoefficient(int n, int k) {
        return factorial(n) / (factorial(k) * factorial(n - k));
    }

    private static double factorial(int n) {
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }


    private void drawMainPoints() {
        for (Point p : points) {
            DrawUtil.drawFilledCircleOptimized(gr, p.x, p.y, width*3, Color.BLUE);
        }
    }


    public List<Point> getSegmentPoints() {
        return segmentPoints;
    }

    public HashMap<Point, BezierCurve> getCurrentCurveMap() {
        HashMap<Point, BezierCurve> res = new HashMap<>();
        for (Point p : segmentPoints) {
            res.put(p, this);
        }
        return res;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


    public List<Point> getPoints() {
        return points;
    }

    public void regenerateCurve() {
//        List<Point> changedPoints = generateBezierCurveOptimized(points, segments);
//        for (int i = 0; i < segmentPoints.size(); i++) {
//            segmentPoints.get(i).x = changedPoints.get(i).x;
//            segmentPoints.get(i).y = changedPoints.get(i).y;
//        }
        segmentPoints = generateBezierCurveOptimized(points, segments);
    }
}
