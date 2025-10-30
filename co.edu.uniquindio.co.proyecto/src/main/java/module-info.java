module co.edu.uniquindio.co.proyecto. {
    requires javafx.graphics;final{
        requires javafx.controls;
        requires javafx.fxml;


        opens co.edu.uniquindio.co.proyecto.final to javafx.fxml;
        exports co.edu.uniquindio.co.proyecto.final;
        }