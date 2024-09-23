module org.example.client_udp_encrypter {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.client_udp_encrypter to javafx.fxml;
    exports org.example.client_udp_encrypter;
}