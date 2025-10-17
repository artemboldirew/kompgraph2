package com.cgvsu.rasterization;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CurveManager {
    List<BezierCurve> curves = new ArrayList<>();
    HashMap<Point, BezierCurve> mapOfPointsAndCurves = new HashMap<>();
    HashSet<Point> allPoints = new HashSet<>();



    public void addCurve(BezierCurve bzc) {
        curves.add(bzc);
        allPoints.addAll(bzc.getSegmentPoints());
        HashMap<Point, BezierCurve> map = bzc.getCurrentCurveMap();
        mapOfPointsAndCurves.putAll(map);
    }

    public BezierCurve getCurveByPoint(Point p) {
        return mapOfPointsAndCurves.get(p);
    }

//    public BezierCurve getCurveBySetPoints(HashSet<Point> points, Point cursor) {
//        HashSet<Point> intersection = new HashSet<>(points);
//        intersection.retainAll(allPoints);
//
//    }

    public boolean isCurveClose(HashSet<Point> points) {
        //HashSet<Point> inter = points.retainAll(allPoints);
        HashSet<Point> intersection = new HashSet<>(points);
        intersection.retainAll(allPoints);
        return !intersection.isEmpty();
    }
}
