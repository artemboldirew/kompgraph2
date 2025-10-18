package com.cgvsu.rasterizationfxapp;

import com.cgvsu.rasterization.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;

public class RasterizationController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    private GraphicsContext gc;
    private CurveManager cv;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        GraphicsContext gr = canvas.getGraphicsContext2D();
        gc = gr;
        cv = new CurveManager();
        DrawUtil.drawGrid(gr, 10);

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
        timer.start();
    }

    private void firstDraw() {
        List<Point> contP = List.of(new Point(10, 10), new Point(300, 800), new Point(500, 200), new Point(800, 800));
        BezierCurve bzc = new BezierCurve(gc, contP, 100, 2, Color.rgb(0,0,0));
        cv.addCurve(bzc);
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
                //bz.activeCurve();
                cv.setActiveCurve(bz);
                System.out.println("Близко");
            } else {
                if (cv.getActiveCurve() != null) {
                    cv.setActiveCurve(null);
                }
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {

        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {

        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {

        });

        canvas.setOnMouseMoved(event -> {
            int mouseX = (int) event.getX();
            int mouseY = (int) event.getY();
            boolean close = cv.isCurveCloseToPoint(mouseX, mouseY);
            if (close) {
                canvas.setCursor(Cursor.HAND);
            }
            else {
                canvas.setCursor(Cursor.DEFAULT);
            }
        });
    }


}