module pl.example.przychodniafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;
    requires java.base;
    requires jakarta.mail;
    requires jakarta.activation;


    opens pl.example.przychodniafx to javafx.fxml;
    exports pl.example.przychodniafx;
    exports pl.example.przychodniafx.model;
    opens pl.example.przychodniafx.model to javafx.fxml;
}