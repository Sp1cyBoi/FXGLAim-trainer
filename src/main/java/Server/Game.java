package Server;

import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class Game {
    private Map<ClientHandler, Integer> hits = new HashMap<>();
    private Map<ClientHandler, Integer> scores = new HashMap<>();

    public void hit(ClientHandler client, String id, Integer dur) {
        hits.put(client, dur);
        if (hits.size() == 1) {
            FXGL.runOnce(() -> {
                determineFaster();
            }, Duration.millis(70));
        }
    }

    private void determineFaster() {
        Map.Entry<ClientHandler, Integer> winner =
                hits
                        .entrySet()
                        .stream()
                        .sorted(Comparator.comparingInt(p -> p.getValue()))
                        .findFirst()
                        .orElse(null);


        for (ClientHandler c : player) {
            try {
                if(scores.get(c) == null) {
                    scores.put(c, 0);
                }
                if(c.equals(winner)) {
                    scores.put(c, scores.get(c) + 1);
                    c.send("SCORE:" + "id:" + scores.get(c) + ":1");
                } else {
                    c.send("SCORE:" + "id:" + scores.get(c) + ":0");

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        hits.clear();
        FXGL.runOnce(() -> {
            createPoint(1,2);
        }, Duration.seconds(3));
    }

    enum State {
        WAITING,
        STARTED,
        FINISHED
    }

    State state = State.WAITING;
    int neededPlayers = 2;
    List<ClientHandler> player = new ArrayList<>();

    public void startRequest(int wantedPlayers, ClientHandler requester) {
        if (state == State.WAITING && player.size() < neededPlayers) {
            player.add(requester);

            if (player.size() < neededPlayers) {
                send("WAIT:" + (neededPlayers - player.size()) + "\n");
            } else {
                state = State.STARTED;
                send("GO");

                FXGL.runOnce(() -> {
                    createPoint(1, 0);
                }, Duration.seconds(3));
            }
        }
    }

    private void createPoint(int level, int pointNum) {
        hits.clear();
        String id = level + "-" + pointNum;
        send("POINT:" + id + ":" + Math.random() + ":" + Math.random() + ":" + 40 + ":" + 5000);
        FXGL.runOnce(() -> {
            determineFaster();
        }, Duration.millis(5000));
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
