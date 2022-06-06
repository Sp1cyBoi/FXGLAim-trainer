package com.leewyatt.github.tank;

import javafx.application.Platform;

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
                        if (s.startsWith("GO")) {
                            Platform.runLater(()->{this.goHandler.accept("");});
                        }
                        if (s.startsWith("WAIT:")) {
                            String params = s.substring(5);
                            Platform.runLater(()->{this.waitHandler.accept(params);});
                        }
                        if (s.startsWith("POINT:")) {
                            String params = s.substring(6);
                            Platform.runLater(()->{this.pointHandler.accept(params);});
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Platform.runLater(()->{ errorHandler.accept(e);});

                }
                System.out.println("After client  read loop");
            }).start();
        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());
            Platform.runLater(()->{ errorHandler.accept(ex);});

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

    public void start() {
        try {
            send("START:2");
        } catch (IOException e) {
            e.printStackTrace();
            errorHandler.accept(e);
        }
    }

    public void send(String message) throws IOException {
        wr.write(message + "\n");
        wr.flush();
    }


}

