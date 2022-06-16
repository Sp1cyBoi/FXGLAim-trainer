package com.leewyatt.github.tank;

import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;


public class GameConfig {

    private GameConfig() {
    }

    private static final PropertyMap map;

    static {
        map = FXGL.getAssetLoader().loadPropertyMap("properties/game.properties");
    }

    public static final int MAX_LEVEL = map.getInt("maxLevel");


    public static final int PLAYER_BULLET_MAX_LEVEL = map.getInt("bulletMaxLevel");

    public static final int ENEMY_AMOUNT = map.getInt("enemyAmount");

    public static final int PLAYER_HEALTH = map.getInt("playerHealth");

    public static final int PLAYER_BULLET_SPEED = map.getInt("playerBulletSpeed");

    public static final int ENEMY_BULLET_SPEED = map.getInt("enemyBulletSpeed");

    public static final Duration PLAYER_SHOOT_DELAY = Duration.seconds(map.getDouble("playerShootDelay"));

    public static final Duration ENEMY_SHOOT_DELAY = Duration.seconds(map.getDouble("enemyShootDelay"));

    public static final Duration HELMET_TIME = Duration.seconds(map.getDouble("helmetTime"));

    public static final Duration STOP_MOVE_TIME = Duration.seconds(map.getDouble("stopMoveTime"));

    public static final Duration ITEM_SHOW_TIME = Duration.seconds(map.getDouble("itemShowTime"));

    public static final Duration ITEM_NORMAL_SHOW_TIME = Duration.seconds(map.getDouble("itemNormalShowTime"));

    public static final Duration SPADE_TIME = Duration.seconds(map.getDouble("spadeTime"));

    public static final Duration SPADE_NORMAL_TIME = Duration.seconds(map.getDouble("spadeNormalTime"));

    public static final Duration SPAWN_ENEMY_TIME = Duration.seconds(map.getDouble("spawnEnemyTime"));

    public static final double SPAWN_ITEM_PRO = map.getDouble("spawnItemPro");

    public static final int PLAYER_SPEED = map.getInt("playerSpeed");

    public static final int ENEMY_SPEED = map.getInt("enemySpeed");

    public static final String CUSTOM_LEVEL_PATH = map.getString("customLevelPath");

    public static final String CUSTOM_LEVEL_DATA = map.getString("customLevelData");

}
