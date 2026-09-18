module com.example.sentencebuilder {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sentencebuilder to javafx.fxml;
    exports com.example.sentencebuilder;
}