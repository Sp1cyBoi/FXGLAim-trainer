package com.leewyatt.github.tank;

import Server.ClientHandler;
import Server.Game;
import com.almasb.fxgl.app.CursorInfo;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.*;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import com.almasb.fxgl.notification.NotificationService;
import com.almasb.fxgl.time.TimerAction;
import com.leewyatt.github.tank.components.PlayerComponent;
import com.leewyatt.github.tank.components.PointComponent;
import com.leewyatt.github.tank.effects.HelmetEffect;
import com.leewyatt.github.tank.ui.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;


public class TankApp extends GameApplication {

    private static String serverIp;
    private static GameClient gameClient;
    private static boolean isServer;
    private Entity player;
    private PlayerComponent playerComponent;
    private Random random = new Random();
    public LazyValue<FailedScene> failedSceneLazyValue = new LazyValue<>(FailedScene::new);
    private LazyValue<SuccessScene> successSceneLazyValue = new LazyValue<>(SuccessScene::new);


    private int[] enemySpawnX = {30, 295 + 30, 589 + 20};

    private TimerAction spadeTimerAction;

    private TimerAction freezingTimerAction;

    private TimerAction spawnEnemyTimerAction;
    private Entity point;

    @Override
    protected void onPreInit() {
        getSettings().setGlobalSoundVolume(0.5);
        getSettings().setGlobalMusicVolume(0.5);

    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(28 * 24 + 6 * 24);
        settings.setHeight(28 * 24);
        settings.setTitle("Aim Trainer " + (isServer ? "(Server)" : ""));
        settings.setAppIcon("ui/fadenkreuz.png");
        settings.setVersion("Version 0.1");
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.getCSSList().add("tankApp.css");
        settings.setDefaultCursor(new CursorInfo("ui/fadenkreuz.png", 20, 20));

        settings.setSceneFactory(new SceneFactory() {
            @Override
            public StartupScene newStartup(int width, int height) {

                return new GameStartupScene(width, height);
            }

            @Override
            public FXGLMenu newMainMenu() {

                return new GameMainMenu();
            }

            @Override
            public LoadingScene newLoadingScene() {

                return new GameLoadingScene();
            }

        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        if (getFileSystemService().exists(GameConfig.CUSTOM_LEVEL_PATH)) {
            vars.put("level", 0);
        } else {
            vars.put("level", 1);
        }
        vars.put("gameOver", false);
    }

    @Override
    protected void initInput() {

        //onBtn(MouseButton.PRIMARY, this::shootAction);
    }






    @Override
    protected void initGame() {
        //getGameScene().getRoot().setBackground();
        getGameScene().setBackgroundColor(Color.WHITE);
        getGameWorld().addEntityFactory(new GameEntityFactory());
        Pane root = getGameScene().getRoot();
        buildAndStartLevel();
        /*getip("destroyedEnemy").addListener((ob, ov, nv) -> {
            if (nv.intValue() == GameConfig.ENEMY_AMOUNT) {
                set("gameOver", true);
                play("Win.wav");
                runOnce(
                        () -> getSceneService().pushSubScene(successSceneLazyValue.get()),
                        Duration.seconds(1.5));
            }
        });*/
    }

    public void buildAndStartLevel() {
        connectToServer();
        gameClient.start();

    }

    public void onPoint(String params) {
        String[] parArr = params.split(":");

         point.getComponent(PointComponent.class).show(parArr[0],Double.valueOf(parArr[1]) * getAppWidth() , Double.valueOf(parArr[2]) * getAppHeight(), Integer.valueOf(parArr[4]));


    }

    public void onGo(String params) {



        startLevel();

        Rectangle rect1 = new Rectangle(getAppWidth(), getAppHeight() / 2.0, Color.web("#333333"));
        Rectangle rect2 = new Rectangle(getAppWidth(), getAppHeight() / 2.0, Color.web("#333333"));
        rect2.setLayoutY(getAppHeight() / 2.0);
        Text text = new Text("STAGE " + geti("level"));
        text.setFill(Color.WHITE);
        text.setFont(new Font(35));
        text.setLayoutX(getAppWidth() / 2.0 - 80);
        text.setLayoutY(getAppHeight() / 2.0 - 5);
        Pane p1 = new Pane(rect1, rect2);
        addUINode(p1);
        Timeline tl = new Timeline(
                new KeyFrame(Duration.seconds(1.2),
                        new KeyValue(rect1.translateYProperty(), -getAppHeight() / 2.0),
                        new KeyValue(rect2.translateYProperty(), getAppHeight() / 2.0)
                ));
        tl.setOnFinished(e -> removeUINode(p1));

        PauseTransition pt = new PauseTransition(Duration.seconds(1.5));
        pt.setOnFinished(e -> {
            text.setVisible(false);
            tl.play();


        });
        pt.play();
    }
        private void onWait (String s){
            getDialogService().showMessageBox(s);
        }

        private void startLevel () {
            setLevelFromMap("level1.tmx");

            play("start.wav");

            //getGameScene().addGameView(new GameView(new InfoPane(), 100));

            point = spawn("point",0,0);

        }





        public void expireAction (TimerAction action){
            if (action == null) {
                return;
            }
            if (!action.isExpired()) {
                action.expire();
            }
        }

        public static void main (String[]args) throws IOException {
            if (args.length > 1 && args[0].equals("server")) {
                serverIp = args[1];
                isServer = false;
            } else {
                serverIp = "127.0.0.1";
                isServer = true;
                Game game = new Game();
                ServerSocket server = new ServerSocket(8088);
                new Thread(() -> {
                    while (true) {
                        System.out.println("ready to connect");

                        Socket client = null;
                        try {
                            client = server.accept();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        new ClientHandler(client, game).start();
                    }
                }).start();
            }
            launch(args);
        }

        public void connectToServer () {
            if (gameClient == null) {
                gameClient = new GameClient(serverIp, 8088);
                gameClient.setGoHandler(this::onGo);
                gameClient.setWaitHandler(this::onWait);
                gameClient.setErrorHandler(this::onProtocolError);
                gameClient.connect();
                gameClient.setPointHandler(this::onPoint);
                gameClient.setScoreHandler(this::onScore);
                gameClient.setFinalScoreHandler(this::onFinalScore);
            }
        }

    private void onScore(String params) {
        String[] parArr = params.split(":");
        FXGL.getNotificationService().pushNotification("Your Score: " + parArr[1] + " Opponent: " + parArr[3]);
        point.setVisible(false);
    }

    private void onFinalScore(String params) {
        String[] parArr = params.split(":");
        FXGL.getNotificationService().pushNotification("Final Score: " + parArr[1] + " Opponent: " + parArr[3]);
        point.setVisible(false);
        FXGL.runOnce( ()->getGameController().gotoMainMenu(), Duration.seconds(6));
    }
    private void onProtocolError (Exception s){
            getDialogService().showErrorBox(s);
        }


    public void pointClicked(Entity entity, String id, long dur) {
        if (gameClient != null) {
            gameClient.send("HIT:" + id + ":" + dur);
        }
    }
}
