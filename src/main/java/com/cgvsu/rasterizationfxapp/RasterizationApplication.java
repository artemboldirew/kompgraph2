package com.cgvsu.rasterizationfxapp;

import com.cgvsu.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RasterizationApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RasterizationApplication.class.getResource("mainwindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Config.getScreenWidth(), Config.getScreenHeight());

        stage.setTitle("Rasterization App");
        stage.setMaximized(true);
//        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }


}