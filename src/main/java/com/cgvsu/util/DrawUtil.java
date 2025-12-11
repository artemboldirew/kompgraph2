package com.cgvsu.util;

import com.cgvsu.Config;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DrawUtil {
    public static void drawGrid(GraphicsContext graphicsContext, int width) {
        PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        int vertical = 0;
        int horizontal = 0;
        while (vertical < Config.getScreenHeight()) {
            while (horizontal < Config.getScreenWidth()) {
                pixelWriter.setColor(horizontal, vertical, Color.rgb(177, 177, 177, 0.4));
                horizontal++;
            }
            vertical += width;
            horizontal = 0;
        }

        vertical = 0;
        horizontal = width;
        while (horizontal < Config.getScreenWidth()) {
            while (vertical < Config.getScreenHeight()) {
                pixelWriter.setColor(horizontal, vertical, Color.rgb(177, 177, 177, 0.4));
                vertical++;
            }
            horizontal += width;
            vertical = 0;
        }
    }

    public static void drawLine2(GraphicsContext gr, Point p1, Point p2, Color color, int width) {
        PixelWriter pw = gr.getPixelWriter();
        List<Point> points = new ArrayList<>();
        int x0 = p1.x;
        int y0 = p1.y;
        int x1 = p2.x;
        int y1 = p2.y;
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = (x0 < x1) ? 1 : -1;
        int sy = (y0 < y1) ? 1 : -1;

        int err = dx - dy;
        int currentX = x0;
        int currentY = y0;

        while (true) {
            points.add(new Point(currentX, currentY));
            if (currentX == x1 && currentY == y1) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                currentX += sx;
            }

            if (e2 < dx) {
                err += dx;
                currentY += sy;
            }
        }
        for (Point p : points) {
            drawFilledCircleOptimized(gr, p.x, p.y, width, color);
        }
    }

    public static void drawCurve(GraphicsContext gr, List<Point> curve, int width, Color color) {
        for (int i = 1; i < curve.size(); i++) {
            Point prev = curve.get(i - 1);
            Point cur = curve.get(i);
            DrawUtil.drawLine(gr, prev, cur, color, width);
        }
    }


    public static void drawFilledCircleOptimized(GraphicsContext gr, int centerX, int centerY, int radius, Color color) {
        int radiusSq = radius * radius;
        PixelWriter pw = gr.getPixelWriter();
        if (radius <= 1) {
            pw.setColor(centerX, centerY, color);
            return;
        }
        for (int y = -radius; y <= radius; y++) {
            int xLimit = (int) Math.sqrt(radiusSq - y * y);
            for (int x = -xLimit; x <= xLimit; x++) {
                pw.setColor(centerX + x, centerY + y, color);
            }
        }
    }

    public static List<Point> getFilledCircleOptimized(int centerX, int centerY, int radius) {
        int radiusSq = radius * radius;
        List<Point> points = new ArrayList<>();
        for (int y = -radius; y <= radius; y++) {
            int xLimit = (int) Math.sqrt(radiusSq - y * y);
            for (int x = -xLimit; x <= xLimit; x++) {
                points.add(new Point(centerX + x, centerY + y));
            }
        }
        return points;
    }


    public static void drawListOfPoints(GraphicsContext gr, List<Point> points) {
        PixelWriter pw = gr.getPixelWriter();
        for (Point p : points) {
            pw.setColor(p.x, p.y, Color.BLACK);
        }
    }



    public static void drawLine(GraphicsContext gr, Point p1, Point p2, Color color, int width) {
        // Основная линия
        gr.setImageSmoothing(false);
        PixelWriter pw = gr.getPixelWriter();
        drawBresenhamLine(pw, p1.x, p1.y, p2.x, p2.y, color);

        // Если толщина больше 1, добавляем дополнительные линии
        if (width > 1) {
            //drawThickLine(pw, p1.x, p1.y, p2.x, p2.y, width, color);
            drawLine2(gr, p1, p2, color, width);
        }
    }


    private static void drawBresenhamLine(PixelWriter pw, int x1, int y1,
                                          int x2, int y2, Color color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;

        int err = dx - dy;
        int e2;

        int currentX = x1;
        int currentY = y1;

        while (true) {
            pw.setColor(currentX, currentY, color);
            if (currentX == x2 && currentY == y2) {
                break;
            }

            e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                currentX += sx;
            }

            if (e2 < dx) {
                err += dx;
                currentY += sy;
            }
        }
    }

}
