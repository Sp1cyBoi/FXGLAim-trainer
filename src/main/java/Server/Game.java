package Server;

import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game {
    enum State {
        WAITING,
        STARTED,
        FINISHED
    }
    State state = State.WAITING;
    int neededPlayers = 2;
    List<ClientHandler> player = new ArrayList<>();

    public void startRequest(int wantedPlayers, ClientHandler requester) {
        if(state == State.WAITING && player.size() < neededPlayers) {
            player.add(requester);

            if(false && player.size() < neededPlayers) {
                send("WAIT:" + (neededPlayers - player.size()) + "\n");
            } else {
                state = State.STARTED;
                send("GO");

                FXGL.runOnce(()->{
                    createPoint(1,0);
                }, Duration.seconds(3));
            }
        }
    }

    private void createPoint(int level, int pointNum) {
        String id = level +"-" + pointNum;
        send("POINT:" + id + ":" + Math.random() + ":" + Math.random() + ":" + 40);
    }

    public void send(String message) {
        for (ClientHandler c : player) {
            try {
                c.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
