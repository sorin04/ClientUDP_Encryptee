package com.astier.bts.client;

import com.astier.bts.client.tcp.TCP;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;


import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.LIME;

public class HelloController implements Initializable {
    public Button button;
    public Button connecter;
    public Button deconnecter;
    public TextField textFieldIP;
    public TextField textFieldPort;
    public TextField textFieldRequette;
    public Circle voyant;
    public TextArea textAreaReponses;
    public TCP tcp;
    static boolean enRun = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        button.setOnAction(event -> {
            try {
                envoyer();
            } catch (InterruptedException e) {
                Logger.getLogger(HelloController.class.getName()).log(Level.SEVERE, null, e);
            }
        });
        connecter.setOnAction(event -> {
            try {
                connecter();
            } catch (UnknownHostException e) {
                Logger.getLogger(HelloController.class.getName()).log(Level.SEVERE, null, e);
            }
        });
        deconnecter.setOnAction(event -> {
            try {
                deconnecter();
            } catch (InterruptedException e) {
                Logger.getLogger(HelloController.class.getName()).log(Level.SEVERE, null, e);
            }
        });
        voyant.setFill(RED);
    }

    private void envoyer() throws InterruptedException {
        String requette = textFieldRequette.getText();
        if (requette.equalsIgnoreCase("exit")) {
            deconnecter();
            return;
        }
        if (!requette.isEmpty()) {
            try {
                tcp.requette(requette);
            } catch (IOException ex) {
                Logger.getLogger(HelloController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void deconnecter() throws InterruptedException {
        if (enRun) {
            tcp.deconnection();
            enRun = false;
            voyant.setFill(RED);
            textAreaReponses.appendText("Déconnecté du serveur.\n");
        }
    }

    private void connecter() throws UnknownHostException {
        String adresseDuServeur = textFieldIP.getText();
        String portDuServeur = textFieldPort.getText();
        if (!adresseDuServeur.isEmpty() && !portDuServeur.isEmpty()) {
            if (!enRun) {
                tcp = new TCP(InetAddress.getByName(adresseDuServeur), Integer.parseInt(portDuServeur), this);
                tcp.connection();
                voyant.setFill(LIME);
                enRun = true;
                textAreaReponses.appendText("Connecté à " + adresseDuServeur + ":" + portDuServeur + "\n");
            }
        }
    }
}
