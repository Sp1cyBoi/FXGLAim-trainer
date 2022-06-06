package com.leewyatt.github.tank.ui;

import com.almasb.fxgl.app.scene.StartupScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * @author LeeWyatt
 * 游戏启动的场景
 */
public class GameStartupScene extends StartupScene {
    public GameStartupScene(int appWidth, int appHeight) {
        super(appWidth, appHeight);
        ImageView iv = new ImageView(getClass().getResource("/assets/textures/ui/aimtrainer_logo.jpg").toExternalForm());
        iv.setFitHeight(50);
        iv.setFitWidth(200);
        iv.setX(0);
        iv.setY(appHeight - 50);
        Pane pane = new Pane(iv);

        getContentRoot().getChildren().addAll(pane);
    }
}
