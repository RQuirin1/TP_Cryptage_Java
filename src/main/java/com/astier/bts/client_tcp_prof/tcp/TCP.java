/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.astier.bts.client_tcp_prof.tcp;


import com.astier.bts.client_tcp_prof.HelloController;
import com.astier.bts.client_tcp_prof.aes.Aes_cbc;
import com.astier.bts.client_tcp_prof.aes.Outils;
import com.astier.bts.client_tcp_prof.diffieHellman.DiffieHellman;
import javafx.application.Platform;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import static javafx.scene.paint.Color.RED;

/**
 * @author Michael
 */
public class TCP extends Thread {
    int port;
    InetAddress serveur;
    Socket socket;
    boolean marche = false;
    boolean connection = false;
    public OutputStream out;
    public InputStream in;
    Aes_cbc crypt;
    HelloController fxmlCont;
    byte[] motDePasse;
    byte[] iv;
    byte[] tabTemp = new byte[65535];
    byte[] tabKey;
    DiffieHellman dfHellman;
    BigInteger K;



    public TCP() {
    }

    public TCP(InetAddress serveur, int port, HelloController fxmlCont) throws IOException {
        this.port = port;
        this.serveur = serveur;
        this.fxmlCont = fxmlCont;

    }

    public void connection() throws IOException, InterruptedException {
       //todo
        socket = new Socket(serveur, port);
        out = socket.getOutputStream();
        in = socket.getInputStream();


        dfHellman = new DiffieHellman(this, 1024);
        tabKey = dfHellman.getParameters();
        motDePasse = Arrays.copyOfRange(tabKey, 1, 17);
        iv = Arrays.copyOfRange(tabKey, 17, 33);
        crypt = new Aes_cbc(motDePasse, iv);
        System.out.println("@ serveur: " + serveur + " port: " + port);

        if (!this.isAlive()){
            this.start();
        }

        this.marche = true;
    }

    public void deconnection() throws IOException {
        //todo

        byte[] b2 = crypt.cryptage("exit".getBytes(StandardCharsets.UTF_8));
        out.write(b2);
        out.close();
        in.close();
        socket.close();
        fxmlCont.voyant.setFill(RED);
        this.marche = false;

    }

    public void requete(String laRequette) throws IOException, InterruptedException {
        if (laRequette.equalsIgnoreCase("exit")){
            Thread.sleep(1000);
            System.out.println("Deconnexion");
            deconnection();
        }else{
            byte[] b2 = crypt.cryptage((laRequette.toLowerCase() + "\r\n").getBytes(StandardCharsets.UTF_8));
            out.write(b2); // envoi reseau
            System.out.println("la requete " + laRequette);
        }
    }

    public void run() {
        while (marche) {
            String message;
            try {
                int temp = in.read(tabTemp);
                byte[] b2 = Arrays.copyOfRange(tabTemp, 0, temp);
                byte[] b3 = crypt.decryptage(b2);
                message = new String(b3, "UTF-8");
                System.out.println("Message serveur " + message + "\n");
                updateMessage(message);
            } catch (IOException e) {
                System.out.println("Erreur lors de la requete de serveur " + serveur + "\n");
            }
        }
    }


    /*
    Pour déclencher une opération graphique en dehors du thread graphique  utiliser
    javafx.application.Platform.runLater(java.lang.Runnable)
    Cette méthode permet d'éxécuter le code du runnable par le thread graphique de JavaFX.
    */
    protected void updateMessage(String message) {
        Platform.runLater(() -> fxmlCont.TextAreaReponses.appendText("    MESSAGE SERVEUR >  \n      " + message + "\n"));
    }
}