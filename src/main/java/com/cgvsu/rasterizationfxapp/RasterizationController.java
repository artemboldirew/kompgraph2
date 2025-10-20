package com.cgvsu.rasterizationfxapp;

import com.cgvsu.interfaces.CanvasElement;
import com.cgvsu.rasterization.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import javax.swing.*;
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
    private HashMap<Point, CanvasElement> canvasMap;
    private Point draggingPoint;
    private boolean isCtrldown = false;
    private HashMap<KeyCode, Boolean> keys = new HashMap<>();

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        GraphicsContext gr = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        gc = gr;
        cv = new CurveManager();
        DrawUtil.drawGrid(gr, 10);

        for (KeyCode k : KeyCode.values()) {
            keys.put(k, false);
        }

        setupEventHandlers();
        firstDraw();
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 10_000_000) { // 10ms в наносекундах
                    repaint(); // Ваш метод отрисовки
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    private void firstDraw() {
//
    }

    public void repaint() {
        // Очищаем canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Перерисовываем содержимое
        drawContent();
    }

    private void drawContent() {
        DrawUtil.drawGrid(gc, 10);
        cv.draw();
    }

    private void setupEventHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
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
            if (cv.getActiveCurve() == null && keys.get(KeyCode.CONTROL)) {

                Point p1 = new Point(mouseX - 20, mouseY);
                Point p2 = new Point(mouseX + 20, mouseY);
                List<Point> vectorPoints = new ArrayList<>();
                vectorPoints.add(p1);
                vectorPoints.add(p2);
                BezierCurve newCurve = new BezierCurve(gc, vectorPoints, 100, 2, Color.BLACK);
                cv.addCurve(newCurve);
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            int mouseX = (int) event.getX();
            int mouseY = (int) event.getY();
            boolean IsMainPointsClose = cv.getActiveCurve() != null ? CoordinateUtil.isSetCloseToPoint(mouseX, mouseY, new HashSet<>(cv.getActiveCurve().getPoints())) : false;
            if (cv.getActiveCurve() != null && IsMainPointsClose) {
                draggingPoint = CoordinateUtil.getClosestPointFromSet(mouseX, mouseY, new HashSet<>(cv.getActiveCurve().getPoints()));
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            int mouseX = (int) event.getX();
            int mouseY = (int) event.getY();
            if (draggingPoint != null) {
                Point p = draggingPoint;
                p.x = mouseX;
                p.y = mouseY;
                cv.getActiveCurve().regenerateCurve();
            }

            //boolean IsMainPointsClose = cv.getActiveCurve() != null ? CoordinateUtil.isSetCloseToPoint(mouseX, mouseY, new HashSet<>(cv.getActiveCurve().getPoints())) : false;


        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            draggingPoint = null;
        });

        canvas.setOnMouseMoved(event -> {
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



        });


        //КЛАВИАТУРА
        canvas.setOnKeyPressed(keyEvent -> {
            KeyCode code = keyEvent.getCode();
            keys.put(code, true);
            boolean isCtrlDown = keys.get(KeyCode.CONTROL);
            boolean isADown = keys.get(KeyCode.A);
            if (isCtrlDown && isADown) {
                List<Point> mainPoints = cv.getActiveCurve().getPoints();
                Point last = mainPoints.getLast();
                cv.getActiveCurve().addVectorPoint(new Point(last.x + 30, last.y));
            }
        });

        canvas.setOnKeyReleased(keyEvent -> {
            KeyCode code = keyEvent.getCode();
            keys.put(code, false);

        });
    }

    private void getCurrentElement(MouseEvent event) {

    }


}