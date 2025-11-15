module co.edu.uniquindio.co.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Controladores y FXML
    opens co.edu.uniquindio.co.proyecto.app to javafx.fxml;

    // Modelos usados en FXML o reflexión
    opens co.edu.uniquindio.co.proyecto.model to javafx.fxml;

    // Export necesario para clases usadas desde otros módulos
    exports co.edu.uniquindio.co.proyecto.app;
    exports co.edu.uniquindio.co.proyecto.model;
    exports co.edu.uniquindio.co.proyecto.ds;
    exports co.edu.uniquindio.co.proyecto.graph;
    exports co.edu.uniquindio.co.proyecto.repository;
    exports co.edu.uniquindio.co.proyecto.service;
    exports co.edu.uniquindio.co.proyecto.catalog;

    // Si en algún momento un FXML necesita instanciar algo de estos paquetes
    opens co.edu.uniquindio.co.proyecto.service to javafx.fxml;
    opens co.edu.uniquindio.co.proyecto.catalog to javafx.fxml;
}
