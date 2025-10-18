package com.cgvsu.rasterization;

import java.awt.*;
import java.util.Comparator;
import java.util.HashSet;

public class CoordinateUtil {
    public static Point closestPoint(HashSet<Point> points, Point me) {
        return points.stream()
                .min(Comparator.comparingDouble(p -> p.distance(me)))
                .orElse(null);
    }
}
