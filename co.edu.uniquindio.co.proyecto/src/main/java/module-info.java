module co.edu.uniquindio.co.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens co.edu.uniquindio.co.proyecto.app to javafx.fxml;
    exports co.edu.uniquindio.co.proyecto.app;
    exports co.edu.uniquindio.co.proyecto.model;
    exports co.edu.uniquindio.co.proyecto.ds;
    exports co.edu.uniquindio.co.proyecto.graph;
    exports co.edu.uniquindio.co.proyecto.repository;
}