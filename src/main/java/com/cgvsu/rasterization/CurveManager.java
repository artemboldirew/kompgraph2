package com.cgvsu.rasterization;

import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CurveManager {
    List<BezierCurve> curves = new ArrayList<>();
    HashMap<Point, BezierCurve> mapOfPointsAndCurves = new HashMap<>();
    HashSet<Point> allPoints = new HashSet<>();
    BezierCurve activeCurve = null;



    public void draw() {
        for (BezierCurve curve : curves) {
            allPoints.addAll(curve.getSegmentPoints());
            mapOfPointsAndCurves.putAll(curve.getCurrentCurveMap());
            curve.draw();
        }
    }


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

    public boolean isCurveInPoints(HashSet<Point> points) {
        HashSet<Point> intersection = new HashSet<>(points);
        intersection.retainAll(allPoints);
        return !intersection.isEmpty();
    }

    public boolean isCurveCloseToPoint(int mouseX, int mouseY) {
        List<Point> circle = DrawUtil.getFilledCircleOptimized(mouseX, mouseY, 10);
        HashSet<Point> circleCursor = new HashSet<>(circle);
        return isCurveInPoints(circleCursor);
    }


    public BezierCurve getClosestCurveToPoint(int mouseX, int mouseY) {
        List<Point> circle = DrawUtil.getFilledCircleOptimized(mouseX, mouseY, 10);
        HashSet<Point> circleCursor = new HashSet<>(circle);
        circleCursor.retainAll(allPoints);
        Point closestP = CoordinateUtil.closestPoint(circleCursor, new Point(mouseX, mouseY));
        return getCurveByPoint(closestP);
    }

    public void setActiveCurve(BezierCurve active) {
        if (this.activeCurve != null) {
            this.activeCurve.setActive(false);
        }
        this.activeCurve = active;
        if (this.activeCurve != null) {
            active.setActive(true);
        }

    }

    public void removeCurve(BezierCurve bz) {
        curves.remove(bz);
    }

    public BezierCurve getActiveCurve() {
        return this.activeCurve;
    }


}
