package com.astier.bts.client.tcp;

import com.astier.bts.client.HelloController;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javafx.application.Platform;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCP extends Thread {
    private int port;
    private InetAddress serveur;
    private Socket socket;
    private boolean marche = false;
    private boolean connection = false;
    private OutputStream out;
    private InputStream in;
    private HelloController fxmlCont;

    private static final byte[] MOT_DE_PASSE = "azertyuiopqsdfgh".getBytes(StandardCharsets.UTF_8); // À adapter si nécessaire
    private static final byte[] IV = "gfdsqpoiuytreza".getBytes(StandardCharsets.UTF_8); // À adapter si nécessaire

    private SecretKeySpec specification;
    private IvParameterSpec iv;

    public TCP(InetAddress serveur, int port, HelloController fxmlCont) {
        this.port = port;
        this.serveur = serveur;
        this.fxmlCont = fxmlCont;
        this.specification = new SecretKeySpec(MOT_DE_PASSE, "AES");
        this.iv = new IvParameterSpec(IV);
    }

    public void connection() {
        try {
            socket = new Socket(serveur, port);
            out = socket.getOutputStream();
            in = socket.getInputStream();
            marche = true;
            connection = true;

            this.start();
        } catch (IOException e) {
            fxmlCont.textAreaReponses.appendText("Erreur de connexion : " + e.getMessage() + "\n");
        }
    }

    public void deconnection() {
        marche = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            fxmlCont.textAreaReponses.appendText("Erreur lors de la déconnexion : " + e.getMessage() + "\n");
        }
    }

    public void requette(String laRequette) throws IOException {
        if (connection) {
            byte[] messageCrypte = cryptage(laRequette.getBytes(StandardCharsets.UTF_8));
            out.write(messageCrypte);
        } else {
            fxmlCont.textAreaReponses.appendText("Pas de connexion au serveur\n");
        }
    }

    @Override
    public void run() {
        try {
            while (marche) {
                byte[] buffer = new byte[1024];
                int bytesRead = in.read(buffer);
                if (bytesRead > 0) {
                    byte[] messageRecu = new byte[bytesRead];
                    System.arraycopy(buffer, 0, messageRecu, 0, bytesRead);
                    String messageDecrypte = new String(decryptage(messageRecu), StandardCharsets.UTF_8);
                    updateMessage(messageDecrypte);
                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> fxmlCont.textAreaReponses.appendText("Erreur lors de la réception : " + e.getMessage() + "\n"));
        }
    }

    private void updateMessage(String message) {
        Platform.runLater(() -> fxmlCont.textAreaReponses.appendText("Message du serveur : " + message + "\n"));
    }

    private byte[] cryptage(byte[] aCoder) {
        try {
            Cipher chiffreur = Cipher.getInstance("AES/CBC/PKCS5Padding");
            chiffreur.init(Cipher.ENCRYPT_MODE, specification, iv);
            return chiffreur.doFinal(aCoder);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] decryptage(byte[] aDecoder) {
        try {
            Cipher dechiffreur = Cipher.getInstance("AES/CBC/PKCS5Padding");
            dechiffreur.init(Cipher.DECRYPT_MODE, specification, iv);
            return dechiffreur.doFinal(aDecoder);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
