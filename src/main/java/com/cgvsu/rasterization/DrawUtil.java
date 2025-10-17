package com.cgvsu.rasterization;

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
        while (vertical < Config.SCREEN_HEIGHT) {
            while (horizontal < Config.SCREEN_WIDTH) {
                pixelWriter.setColor(horizontal, vertical, Color.BLACK);
                horizontal++;
            }
            vertical += width;
            horizontal = 0;
        }

        vertical = 0;
        horizontal = width;
        while (horizontal < Config.SCREEN_WIDTH) {
            while (vertical < Config.SCREEN_HEIGHT) {
                pixelWriter.setColor(horizontal, vertical, Color.BLACK);
                vertical++;
            }
            horizontal += width;
            vertical = 0;
        }
    }

    public static void drawLine(GraphicsContext gr, Point p1, Point p2, Color color, int width) {
        PixelWriter pw = gr.getPixelWriter();
        List<Point> points = new ArrayList<>();
        int x0 = p1.x;
        int y0 = p1.y;
        int x1 = p2.x;
        int y1 = p2.y;
        // Вычисляем абсолютные разности
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        // Определяем направление движения по X и Y
        int sx = (x0 < x1) ? 1 : -1;
        int sy = (y0 < y1) ? 1 : -1;

        // Начальная ошибка
        int err = dx - dy;
        int currentX = x0;
        int currentY = y0;

        // Основной цикл
        while (true) {
            // Добавляем текущую точку
            points.add(new Point(currentX, currentY));

            // Если достигли конечной точки - выходим
            if (currentX == x1 && currentY == y1) {
                break;
            }

            // Вычисляем удвоенную ошибку
            int e2 = 2 * err;

            // Если ошибка по Y велика - двигаемся по Y
            if (e2 > -dy) {
                err -= dy;
                currentX += sx;
            }

            // Если ошибка по X велика - двигаемся по X
            if (e2 < dx) {
                err += dx;
                currentY += sy;
            }
        }
        int ddx = x1 - x0;
        int ddy = y0 - y1;
        for (Point p : points) {
            drawFilledCircleOptimized(gr, p.x, p.y, width, color);
//            pw.setColor(p.x, p.y, color);
//            pw.setColor(p.x, p.y - 1, color);
//            pw.setColor(p.x, p.y - 2, color);
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



}
