package com.leewyatt.github.tank.ui;

import com.almasb.fxgl.texture.Texture;
import com.leewyatt.github.tank.GameConfig;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author LeeWyatt
 * 游戏右侧,显示敌人坦克数量,子弹等级的信息界面
 *    有空修改成单例类,感觉更好;
 */
public class InfoPane extends Pane {

    public InfoPane() {
        TilePane tilePane = new TilePane(10, 10);
        tilePane.setAlignment(Pos.TOP_LEFT);
        tilePane.setPrefSize(65, 390);
        for (int i = 0; i < GameConfig.ENEMY_AMOUNT; i++) {
            tilePane.getChildren().add(texture("ui/enemy_pre.png"));
        }
        tilePane.setLayoutX(25);
        tilePane.setLayoutY(50);

        Texture levelFlag = texture("ui/levelFlag.png");
        levelFlag.setLayoutX(25);
        levelFlag.setLayoutY(460);

        Text levelText = getUIFactoryService().newText("", Color.BLACK, 43);
        levelText.setLayoutX(38);
        levelText.setLayoutY(520);
        levelText.textProperty().bind(getip("level").asString());

        getChildren().addAll(tilePane, levelFlag, levelText);
        setPrefSize(24 * 6, 24 * 28);
        setLayoutX(24 * 28);
        setLayoutY(0);
        setStyle("-fx-background-color: #ffffff");

        ObservableList<Node> enemyPreNodes = tilePane.getChildren();
        getip("spawnedEnemy").addListener((ob, ov, nv) -> {
            for (int i = enemyPreNodes.size() - 1; i >= GameConfig.ENEMY_AMOUNT - nv.intValue(); i--) {
                enemyPreNodes.get(i).setVisible(false);
            }
        });
    }

}
