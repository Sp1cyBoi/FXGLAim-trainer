package com.leewyatt.github.tank.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.DialogService;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static javafx.scene.input.KeyCode.*;


public class GameMainMenu extends FXGLMenu {

    private final TranslateTransition tt;
    private final Pane defaultPane;


    public GameMainMenu() {
        super(MenuType.MAIN_MENU);


        Texture texture = texture("ui/aimtrainer_logo.jpg");
        texture.setLayoutX(10);
        texture.setLayoutY(10);
        texture.setFitWidth(getAppWidth()-20);
        texture.setFitHeight((getAppWidth()-20) * 0.25);

        MainMenuButton newGameBtn = new MainMenuButton("START GAME", this::fireNewGame);
        MainMenuButton constructBtn = new MainMenuButton("SETTINGS", () -> {
            getContentRoot().getChildren().setAll(new ConstructPane());
        });
        MainMenuButton helpBtn = new MainMenuButton("HELP", this::instructions);
        MainMenuButton exitBtn = new MainMenuButton("EXIT", () -> getGameController().exit());
        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(newGameBtn, constructBtn, helpBtn, exitBtn);
        newGameBtn.setSelected(true);
        VBox menuBox = new VBox(
                5,
                newGameBtn,
                constructBtn,
                helpBtn,
                exitBtn
        );
        menuBox.setAlignment(Pos.CENTER_LEFT);
        menuBox.setLayoutX(150);
        menuBox.setLayoutY(200);
        menuBox.setVisible(false);



        tt = new TranslateTransition(Duration.seconds(2), texture);
        tt.setInterpolator(Interpolators.ELASTIC.EASE_OUT());
        tt.setFromX(-100);
        tt.setFromY(10);
        tt.setToX(10);
        tt.setToY(10);
        tt.setOnFinished(e -> menuBox.setVisible(true));

        Rectangle bgRect = new Rectangle(getAppWidth(), getAppHeight(), Color.WHITE);

        Line line = new Line(30, 580, 770, 580);
        line.setStroke(Color.web("#FF0000"));
        line.setStrokeWidth(2);


        defaultPane = new Pane(bgRect, texture, menuBox, line);
        defaultPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        getContentRoot().getChildren().setAll(defaultPane);

    }

    @Override
    public void onCreate() {
        getContentRoot().getChildren().setAll(defaultPane);
        FXGL.play("mainMenuLoad.wav");
        tt.play();
    }


    private void instructions() {
        GridPane pane = new GridPane();
        pane.setHgap(20);
        pane.setVgap(15);
        KeyView kvW = new KeyView(W);
        kvW.setPrefWidth(38);
        TilePane tp1 = new TilePane(kvW, new KeyView(S), new KeyView(A), new KeyView(D));
        tp1.setPrefWidth(200);
        tp1.setHgap(2);
        tp1.setAlignment(Pos.CENTER_LEFT);

        pane.addRow(0, getUIFactoryService().newText("Movement"), tp1);
        pane.addRow(1, getUIFactoryService().newText("Shoot"), new KeyView(F));
        KeyView kvL = new KeyView(LEFT);
        kvL.setPrefWidth(38);
        TilePane tp2 = new TilePane(new KeyView(UP), new KeyView(DOWN), kvL, new KeyView(RIGHT));
        tp2.setPrefWidth(200);
        tp2.setHgap(2);
        tp2.setAlignment(Pos.CENTER_LEFT);
        pane.addRow(2, getUIFactoryService().newText("Movement"), tp2);
        pane.addRow(3, getUIFactoryService().newText("Shoot"), new KeyView(SPACE));
        DialogService dialogService = getDialogService();
        dialogService.showBox("Help", pane, getUIFactoryService().newButton("OK"));
    }

}
