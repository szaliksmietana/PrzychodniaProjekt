module pl.example.przychodniafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens pl.example.przychodniafx to javafx.fxml;
    exports pl.example.przychodniafx;
}