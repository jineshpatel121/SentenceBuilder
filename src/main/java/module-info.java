module com.example.sentencebuilder {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sentencebuilder to javafx.fxml;
    exports com.example.sentencebuilder;
    exports com.example.sentencebuilder.ui;
    opens com.example.sentencebuilder.ui to javafx.fxml;
}