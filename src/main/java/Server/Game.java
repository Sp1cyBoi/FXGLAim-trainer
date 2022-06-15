package Server;

import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class Game {
    public static final int maxPoints = 5;
    public static final int neededPlayers = 1;

    enum State {
        WAITING,
        STARTED,
        FINISHED
    }

    State state = State.WAITING;
    List<ClientHandler> player = new ArrayList<>();

    private Map<ClientHandler, Integer> hits = new HashMap<>();
    private Map<ClientHandler, Integer> scores = new HashMap<>();
    int pointNo = 0;


    public void hit(ClientHandler client, String id, Integer dur) {
        hits.put(client, dur);
        if (hits.size() == 1) {
            FXGL.runOnce(() -> {
                determineFaster(pointNo);
            }, Duration.millis(70));
        }
    }

    private void determineFaster(int determinePointNo) {
        if( state != State.STARTED) return;
        if( determinePointNo != pointNo) return;
        Map.Entry<ClientHandler, Integer> winner =
                hits
                        .entrySet()
                        .stream()
                        .sorted(Comparator.comparingInt(p -> p.getValue()))
                        .findFirst()
                        .orElse(null);

        for (ClientHandler c : player) {
            if(scores.get(c) == null) {
                scores.put(c, 0);
            }
            if (winner != null && c.equals(winner.getKey())) {
                scores.put(c, scores.get(c) + 1);
            }
        }
        int maxScore = scores.values().stream().reduce((a,b)->Math.max(a,b)).orElse(0);
        for (ClientHandler c : player) {
            try {
                    c.send((maxPoints == maxScore ? "FINALSCORE:": "SCORE:") + "id:" + scores.get(c) + (winner != null && c.equals(winner.getKey()) ? ":1:" : ":0:") +
                            scores.entrySet().stream()
                                    .filter(p->!p.getKey().equals(c))
                                    .map(p->p.getValue())
                                    .findFirst().orElse(0));


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        hits.clear();
        if( maxScore == maxPoints) {
            state = State.FINISHED;
        }
        else {
            FXGL.runOnce(() -> {
                createPoint(1);
            }, Duration.seconds(3));
        }
    }




    public void startRequest(int wantedPlayers, ClientHandler requester) {
        if (state == State.WAITING && player.size() < neededPlayers) {
            player.add(requester);

            if ( player.size() < neededPlayers) {
                send("WAIT:" + (neededPlayers - player.size()) + "\n");
            } else {
                state = State.STARTED;
                send("GO");

                FXGL.runOnce(() -> {
                    createPoint(1);
                }, Duration.seconds(3));
            }
        }
    }

    private void createPoint(int level) {
        if( state != State.STARTED) return;
        hits.clear();
        pointNo++;
        String id = level + "-" + pointNo;
        int determinePointNo = pointNo;

        send("POINT:" + id + ":" + Math.random() + ":" + Math.random() + ":" + 40 + ":" + 5000);
        FXGL.runOnce(() -> {
            determineFaster(determinePointNo);
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
