package com.cgvsu.rasterization;

import java.awt.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class CoordinateUtil {
    public static Point closestPoint(HashSet<Point> points, Point me) {
        return points.stream()
                .min(Comparator.comparingDouble(p -> p.distance(me)))
                .orElse(null);
    }

    public static boolean isSetCloseToPoint(int mouseX, int mouseY, HashSet<Point> points) {
        List<Point> circle = DrawUtil.getFilledCircleOptimized(mouseX, mouseY, 10);
        HashSet<Point> circleCursor = new HashSet<>(circle);
        HashSet<Point> intersection = new HashSet<>(circleCursor);
        intersection.retainAll(points);
        return !intersection.isEmpty();
    }

    public static Point getClosestPointFromSet(int mouseX, int mouseY, HashSet<Point> points) {
        List<Point> circle = DrawUtil.getFilledCircleOptimized(mouseX, mouseY, 10);
        HashSet<Point> circleCursor = new HashSet<>(circle);
        //HashSet<Point> intersection = new HashSet<>(circleCursor);
        points.retainAll(circleCursor);
        return closestPoint(points, new Point(mouseX, mouseY));
    }
}
