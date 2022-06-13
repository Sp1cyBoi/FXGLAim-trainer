package Server;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread{
    private final Game game;
    BufferedWriter wr;
    protected Socket client;

    public ClientHandler(Socket s, Game game) {
        client = s;
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return client.equals(that.client);
    }

    @Override
    public int hashCode() {
        return client.hashCode();
    }

    @Override
    public void run() {
        try {
           BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
            wr = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            System.out.println("connected to " + client.getRemoteSocketAddress());

            String s = "";
            while((s = r.readLine()) != null) {
                if( s.startsWith("START:")) {
                    Platform.runLater(()->{game.startRequest(2, this);});
                }
                if( s.startsWith("HIT:")) {
                    String[] par = s.split(":");
                    System.out.println(this.hashCode() + ": " + s);
                    Platform.runLater(()->{game.hit(this,par[1], Integer.valueOf(par[2]));});
                }
            }

        } catch (IOException io) {
            io.printStackTrace();
        }
        System.out.println("After server read loop");
    }

    public void send(String message) throws IOException {
        wr.write(message + "\n");
        wr.flush();
    }
}
