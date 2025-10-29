package com.cgvsu;

import javafx.stage.Screen;

public class Config {
    public static final int SCREEN_WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth();//1300
    public static final int SCREEN_HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight();//900

    public static int getScreenWidth() {
        return (int) Screen.getPrimary().getVisualBounds().getWidth();
    }

    public static int getScreenHeight() {
        return (int) Screen.getPrimary().getVisualBounds().getHeight();
    }
}
