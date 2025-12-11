package com.cgvsu.rasterizationfxapp;

import com.cgvsu.Config;
import com.cgvsu.rasterization.*;
import com.cgvsu.util.CoordinateUtil;
import com.cgvsu.util.DrawUtil;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RasterizationController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    private GraphicsContext gc;
    private CurveManager cv;
    private HashSet<Point> allPoints;
    private Point draggingPoint;
    private HashMap<KeyCode, Boolean> keys = new HashMap<>();

    private final int SEGMENTS_AMOUNT = 10;
    private final int MAKING_POINT_SHIFT = 20;
    private final int SCREEN_UPDATE_TIME = 10_000_000;
    private final int CURVE_WIDTH = 1;
    private final int GRID_SHIFT = 10;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        canvas.setWidth(Config.getScreenWidth());
        canvas.setHeight(Config.getScreenHeight());
        GraphicsContext gr = canvas.getGraphicsContext2D();
        gr.setImageSmoothing(false);

        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        gc = gr;
        cv = new CurveManager();
        DrawUtil.drawGrid(gr, GRID_SHIFT);

        for (KeyCode k : KeyCode.values()) {
            keys.put(k, false);
        }

        setupEventHandlers();
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= SCREEN_UPDATE_TIME) { // 10ms в наносекундах
                    repaint(); // Ваш метод отрисовки
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    public void repaint() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawContent();
    }

    private void drawContent() {
        DrawUtil.drawGrid(gc, GRID_SHIFT);
        cv.draw();
    }

    private void setupEventHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            activateDisactivateCurve(event);
            makeCurve(event);
        });

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            startDragging(event);
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            activeDragging(event);
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            draggingPoint = null;
        });

        canvas.setOnMouseMoved(event -> {
            cursorMoving(event);
        });

        //КЛАВИАТУРА
        canvas.setOnKeyPressed(keyEvent -> {
            KeyCode code = keyEvent.getCode();
            keys.put(code, true);
            appendNewPointToCurve(keyEvent);
            deleteActiveCurve(keyEvent);
        });

        canvas.setOnKeyReleased(keyEvent -> {
            KeyCode code = keyEvent.getCode();
            keys.put(code, false);
        });
    }



    //Методы считывания ввода
    private void activateDisactivateCurve(MouseEvent event) {
        int mouseX = (int) event.getX();
        int mouseY = (int) event.getY();
        boolean close = cv.isCurveCloseToPoint(mouseX, mouseY);
        if (close) {
            BezierCurve bz = cv.getClosestCurveToPoint(mouseX, mouseY);
            cv.setActiveCurve(bz);
        } else {
            boolean IsMainPointsClose = cv.getActiveCurve() != null ? CoordinateUtil.isSetCloseToPoint(mouseX, mouseY, new HashSet<>(cv.getActiveCurve().getPoints())) : false;
            if (cv.getActiveCurve() != null && !IsMainPointsClose) {
                cv.setActiveCurve(null);
            }
        }
    }

    private void makeCurve(MouseEvent event) {
        int mouseX = (int) event.getX();
        int mouseY = (int) event.getY();
        if (cv.getActiveCurve() == null && keys.get(KeyCode.CONTROL)) {

            Point p1 = new Point(mouseX - MAKING_POINT_SHIFT, mouseY);
            Point p2 = new Point(mouseX + MAKING_POINT_SHIFT, mouseY);
            List<Point> vectorPoints = new ArrayList<>();
            vectorPoints.add(p1);
            vectorPoints.add(p2);
            BezierCurve newCurve = new BezierCurve(gc, vectorPoints, CURVE_WIDTH, Color.BLACK);
            cv.addCurve(newCurve);
        }
    }

    private void startDragging(MouseEvent event) {
        int mouseX = (int) event.getX();
        int mouseY = (int) event.getY();
        boolean IsMainPointsClose = cv.getActiveCurve() != null ? CoordinateUtil.isSetCloseToPoint(mouseX, mouseY, new HashSet<>(cv.getActiveCurve().getPoints())) : false;
        if (cv.getActiveCurve() != null && IsMainPointsClose) {
            draggingPoint = CoordinateUtil.getClosestPointFromSet(mouseX, mouseY, new HashSet<>(cv.getActiveCurve().getPoints()));
        }
    }

    private void activeDragging(MouseEvent event) {
        int mouseX = (int) event.getX();
        int mouseY = (int) event.getY();
        if (draggingPoint != null) {
            Point p = draggingPoint;
            if (mouseX >= 0 && mouseY >= 0 && mouseX <= canvas.getWidth() && mouseY <= canvas.getHeight()) {
                p.x = mouseX;
                p.y = mouseY;
                cv.getActiveCurve().regenerateCurve();
            }

        }
    }

    private void cursorMoving(MouseEvent event) {
        int mouseX = (int) event.getX();
        int mouseY = (int) event.getY();
        boolean close = cv.isCurveCloseToPoint(mouseX, mouseY);

        boolean IsMainPointsClose = cv.getActiveCurve() != null ? CoordinateUtil.isSetCloseToPoint(mouseX, mouseY, new HashSet<>(cv.getActiveCurve().getPoints())) : false;
        if (close || IsMainPointsClose) {
            canvas.setCursor(Cursor.HAND);
        }
        else {
            canvas.setCursor(Cursor.DEFAULT);
        }
    }


    private void appendNewPointToCurve(KeyEvent keyEvent) {
        boolean isCtrlDown = keys.get(KeyCode.CONTROL);
        boolean isADown = keys.get(KeyCode.A);
        if (isCtrlDown && isADown && cv.getActiveCurve() != null ) {
            List<Point> mainPoints = cv.getActiveCurve().getPoints();
            Point last = mainPoints.getLast();
            cv.getActiveCurve().addVectorPoint(new Point(Math.min(last.x + 30, Config.getScreenWidth()), last.y));
        }
    }

    private void deleteActiveCurve(KeyEvent keyEvent) {
        if (keys.get(KeyCode.DELETE) && cv.getActiveCurve() != null) {
            BezierCurve bc = cv.getActiveCurve();
            cv.removeCurve(bc);
            cv.setActiveCurve(null);
        }
    }


}