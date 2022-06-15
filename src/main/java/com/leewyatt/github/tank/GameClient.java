package com.leewyatt.github.tank;

import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class GameClient {
    private String serverIp;
    private int port;
    private BufferedWriter wr;
    private Consumer<String> goHandler;
    private Consumer<String> waitHandler;
    private Consumer<Exception> errorHandler;
    private Consumer<String> pointHandler;
    private Consumer<String> scoreHandler;
    private  Consumer<String>  finalScoreHandler;

    public GameClient(String serverIp, int i) {
        this.serverIp = serverIp;
        this.port = i;

    }

    public void connect() {
        try {
            Socket socket = new Socket(serverIp, port);


            wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            new Thread(() -> {

                BufferedReader r = null;
                try {
                    r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s = "";
                    while ((s = r.readLine()) != null) {
                        System.out.println("Client:" + s);
                        if (s.startsWith("GO")) {
                            FXGL.runOnce(()->{this.goHandler.accept("");}, Duration.millis(0));
                        }
                        if (s.startsWith("WAIT:")) {
                            String params = s.substring(5);
                            FXGL.runOnce(()->{this.waitHandler.accept(params);}, Duration.millis(0));
                        }
                        if (s.startsWith("POINT:")) {
                            String params = s.substring(6);
                            FXGL.runOnce(()->{this.pointHandler.accept(params);}, Duration.millis(0));
                        }
                        if (s.startsWith("SCORE:")) {
                            String params = s.substring(6);
                            FXGL.runOnce(()->{this.scoreHandler.accept(params);}, Duration.millis(0));
                        }
                        if (s.startsWith("FINALSCORE:")) {
                            String params = s.substring(11);
                            FXGL.runOnce(()->{this.finalScoreHandler.accept(params);}, Duration.millis(0));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    FXGL.runOnce(()->{ errorHandler.accept(e);}, Duration.millis(0));

                }
                System.out.println("After client  read loop");
            }).start();
        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());
            FXGL.runOnce(()->{ errorHandler.accept(ex);}, Duration.millis(0));

        } catch (IOException exc) {
            exc.printStackTrace();

        }
    }

    public void setGoHandler(Consumer<String> consumer) {
        this.goHandler = consumer;
    }

    public void setPointHandler(Consumer<String> consumer) {
        this.pointHandler = consumer;
    }

    public void setWaitHandler(Consumer<String> consumer) {
        this.waitHandler = consumer;
    }

    public void setErrorHandler(Consumer<Exception> consumer) {
        this.errorHandler = consumer;
    }

    public void setScoreHandler(Consumer<String> consumer) {
        this.scoreHandler = consumer;
    }

    public void setFinalScoreHandler(Consumer<String> consumer) {
        this.finalScoreHandler = consumer;
    }

    public void start() {
            send("START:2");
    }

    public void send(String message) {
        try {
            wr.write(message + "\n");
            wr.flush();
        } catch (IOException e) {
            e.printStackTrace();
            errorHandler.accept(e);
        }
    }


}

