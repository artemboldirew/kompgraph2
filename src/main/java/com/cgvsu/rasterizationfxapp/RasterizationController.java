package com.cgvsu.rasterizationfxapp;

import com.cgvsu.rasterization.*;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RasterizationController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    private GraphicsContext gc;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        GraphicsContext gr = canvas.getGraphicsContext2D();
        gc = gr;
        //DrawUtil.drawGrid(gr, 10);
        setupEventHandlers();
        drawContent();
        Timer timer = new Timer(100, e -> {
            repaint();
        });
    }

    public void repaint() {
        // Очищаем canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Перерисовываем содержимое
        drawContent();
    }

    private void drawContent() {
        List<Point> contP = List.of(new Point(10, 10), new Point(300, 800), new Point(500, 200), new Point(800, 800));
        BezierCurve bzc = new BezierCurve(gc, contP, 100, 2, Color.rgb(255,255,255));
        bzc.draw();
        bzc.activeCurve();
    }

    private void setupEventHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {

        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {

        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {

        });

        canvas.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            if (mouseX < 400) {
                canvas.setCursor(Cursor.HAND);
            }
            else {
                canvas.setCursor(Cursor.DEFAULT);
            }
        });
    }

    private void distanceToCurve(MouseEvent event) {
        int mouseX = (int) event.getX();
        int mouseY = (int) event.getY();
        List<Point> circle = DrawUtil.getFilledCircleOptimized(mouseX, mouseY, 10);

    }

}