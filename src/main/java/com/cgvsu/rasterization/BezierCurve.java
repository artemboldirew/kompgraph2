package com.cgvsu.rasterization;

import com.cgvsu.util.DrawUtil;
import com.cgvsu.util.MathUtil;
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
    int width = 1;
    private Color color;
    private boolean isActive = false;


    public BezierCurve(GraphicsContext gr, List<Point> points, int width, Color color) {
        this.gr = gr;
        this.points = points;
        this.width = width;
        this.color = color;
        updateSegmentsAmount();
        segmentPoints = generateBezierCurveOptimized(points, segments);
    }

    public void draw() {
        DrawUtil.drawCurve(gr, segmentPoints, width, color);
        //DrawUtil.drawListOfPoints(gr, segmentPoints);
        if (isActive) {
            drawMainPoints();
            DrawUtil.drawCurve(gr, points, width, Color.BLUE);
        }
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
        return MathUtil.factorial(n) / (MathUtil.factorial(k) * MathUtil.factorial(n - k));
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
        List<Point> changedPoints = generateBezierCurveOptimized(points, segments);
        for (int i = 0; i < segmentPoints.size(); i++) {
            segmentPoints.get(i).x = changedPoints.get(i).x;
            segmentPoints.get(i).y = changedPoints.get(i).y;
        }
    }

    public void addVectorPoint(Point p) {
        points.add(p);
        updateSegmentsAmount();
        segmentPoints = generateBezierCurveOptimized(points, segments);
    }


    public static double calculateLength(List<Point> controlPoints) {
        // Константы метода
        final int MAX_ITERATIONS = 20;
        final double EPSILON = 1e-12;
        final int n = controlPoints.size() - 1; // Степень кривой

        if (n < 1) {
            return 0.0;
        }

        // Предварительно вычисляем биномиальные коэффициенты
        final double[] binom = new double[n];
        binom[0] = 1.0;
        for (int i = 1; i < n; i++) {
            binom[i] = binom[i - 1] * (n - i) / i;
        }

        // Массив для метода Ромберга
        double[][] R = new double[MAX_ITERATIONS][MAX_ITERATIONS];

        // Границы интегрирования
        final double a = 0.0;
        final double b = 1.0;

        // Первая итерация (правило трапеций с 1 интервалом)
        double fa = bezierSpeed(controlPoints, n, binom, a);
        double fb = bezierSpeed(controlPoints, n, binom, b);
        R[0][0] = 0.5 * (b - a) * (fa + fb);

        // Итерации метода Ромберга
        for (int k = 1; k < MAX_ITERATIONS; k++) {
            // Количество интервалов на текущей итерации (2^k)
            final int segments = 1 << k; // 2^k
            final double h = (b - a) / segments;

            // Сумма нечетных точек для правила трапеций
            double sum = 0.0;
            for (int i = 1; i < segments; i += 2) {
                sum += bezierSpeed(controlPoints, n, binom, a + i * h);
            }

            // Вычисление R[k][0] по формуле трапеций
            R[k][0] = 0.5 * R[k-1][0] + h * sum;

            // Экстраполяция Ричардсона
            for (int j = 1; j <= k; j++) {
                final double factor = Math.pow(4.0, j);
                R[k][j] = R[k][j-1] + (R[k][j-1] - R[k-1][j-1]) / (factor - 1.0);
            }

            // Проверка сходимости
            if (k >= 4) {
                final double error = Math.abs(R[k][k] - R[k-1][k-1]);
                if (error < EPSILON) {
                    return R[k][k];
                }
            }
        }

        return R[MAX_ITERATIONS-1][MAX_ITERATIONS-1];
    }


    private static double bezierSpeed(List<Point> points, int n, double[] binom, double t) {
        // Производная кривой Безье степени n
        // B'(t) = n * Σ_{i=0}^{n-1} binom(n-1, i) * (1-t)^{n-1-i} * t^i * (P_{i+1} - P_i)

        double dx = 0.0;
        double dy = 0.0;
        final double oneMinusT = 1.0 - t;

        // Вычисление производной
        for (int i = 0; i < n; i++) {
            // ВАЖНО: используем вещественные вычисления
            final double coefficient = n * binom[i] *
                    Math.pow(oneMinusT, n - 1 - i) * Math.pow(t, i);

            // Преобразуем int в double для точных вычислений
            final double deltaX = points.get(i + 1).x - points.get(i).x;
            final double deltaY = points.get(i + 1).y - points.get(i).y;

            dx += coefficient * deltaX;
            dy += coefficient * deltaY;
        }

        // Длина вектора скорости
        final double speed = Math.sqrt(dx * dx + dy * dy);
        return speed;
    }

    private void updateSegmentsAmount() {
        segments = (int) calculateLength(points);
    }
}
