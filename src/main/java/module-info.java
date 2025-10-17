module com.cgvsu.rasterizationfxapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.cgvsu.rasterizationfxapp to javafx.fxml;
    exports com.cgvsu.rasterizationfxapp;
}