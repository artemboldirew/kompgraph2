package com.cgvsu.rasterization;

import java.util.ArrayList;
import java.util.List;

public class CurveManager {
    List<BezierCurve> curves = new ArrayList<>();




    public void addCurve(BezierCurve bzc) {
        curves.add(bzc);
    }
}
