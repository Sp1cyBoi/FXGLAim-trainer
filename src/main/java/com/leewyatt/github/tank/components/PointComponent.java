package com.leewyatt.github.tank.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityGroup;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import com.leewyatt.github.tank.GameConfig;
import com.leewyatt.github.tank.TankApp;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.time.Instant;
import java.util.List;
import java.util.Random;

import static com.leewyatt.github.tank.GameType.*;


public class PointComponent extends Component {
    private BoundingBoxComponent bbox;
    private Instant startTime;
    private String id;

    @Override
    public void onUpdate(double tpf) {

    }


    @Override
    public void onAdded() {
        entity.getViewComponent().addOnClickHandler(e -> mouseHandler(e));
        startTime = Instant.now();
    }

    private void mouseHandler(MouseEvent mouseEvent) {
       if(entity == null || !entity.isActive()) {

       }else {
           ((TankApp)FXGL.getApp()).pointClicked(entity, id, java.time.Duration.between(Instant.now(), startTime).toMillis());
       }

    }

    public void setTimeout(Integer dur) {
        FXGL.runOnce(() -> {

            if (entity != null && entity.isActive()) {
                entity.removeFromWorld();
            }


        }, Duration.millis(dur));
    }

    public void setID(String id) {
        this.id = id;
    }
}
