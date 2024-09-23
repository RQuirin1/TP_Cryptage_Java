package com.astier.bts.client_tcp_prof;

import com.astier.bts.client_tcp_prof.tcp.TCP;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import static javafx.scene.paint.Color.*;

public class HelloController implements Initializable {
    public Button button;
    public Button connecter;
    public Button deconnecter;
    public TextField TextFieldIP;
    public TextField TextFieldPort;
    public TextField TextFieldRequete;
    public Circle voyant;
    public TextArea TextAreaReponses;
    static public TCP tcp;
    static boolean enRun = false;
    String adresse,port;
    InetAddress inetAddress;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        voyant.setFill(RED);
        connecter.setOnAction(event -> {
            try {
                try {
                    if (TextFieldPort.getText() != null && TextFieldIP.getText() != null){
                        adresse = TextFieldIP.getText();
                        port = TextFieldPort.getText();
                        inetAddress = InetAddress.getByName(adresse);
                        tcp = new TCP(inetAddress, Integer.parseInt(port), this);
                    }
                } catch (UnknownHostException e) {
                    System.out.println("Erreur de connexion : " + e.getMessage());
                }
                connecter();
            } catch (IOException | InterruptedException e) {
                System.out.println("erreur : " + e.getMessage());
            }
        });
        button.setOnAction(event -> {
            try {
                envoyer();
            } catch (IOException | InterruptedException e) {
                System.out.println("erreur : " + e.getMessage());
            }
        });
        deconnecter.setOnAction(event -> {
            try {
                deconnecter();
            } catch (IOException e) {
                System.out.println("erreur : " + e.getMessage());
            }
        });
    }


    private void envoyer() throws IOException, InterruptedException {
       //todo
        if (TextFieldRequete.getText().equalsIgnoreCase("exit")){
            enRun = false;
        }
        tcp.requete(TextFieldRequete.getText());

    }

    private void deconnecter() throws IOException {
        //todo
        if(enRun){
            tcp.deconnection();
            voyant.setFill(RED);
            enRun = false;
        }
    }

    private void connecter() throws IOException, InterruptedException {
        //todo
        if(!enRun){
            tcp.connection();
            voyant.setFill(GREEN);
            enRun = true;
        }

    }

}