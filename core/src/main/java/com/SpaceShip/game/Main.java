package com.SpaceShip.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.sql.Time;
import java.util.Iterator;



/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image, tNave, tEnemy, tMissile;
    private Sprite nave, enemy, missile;
    private float posX, posY, velocity, xMissile, yMissile;
    private boolean attack;
    private Array<Rectangle> enemies;
    private long lastEnemyTime;



    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("bg.png");
        tNave = new Texture("spaceship.png");
        nave = new Sprite(tNave);
        posX = 0;
        posY = 0;
        velocity = 10;

        tMissile = new Texture("missile.png");
        missile = new Sprite(tMissile);
        yMissile = posY;
        xMissile = posX;
        attack = false;

        tEnemy = new Texture("enemy.png");
        enemies = new Array<Rectangle>();

        lastEnemyTime = 0;
    }

    @Override
    public void render() {

        this.moveNave();
        this.moveMissible();
        this.moveEnemies();

        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        batch.draw(image, 0, 0);
        batch.draw(missile, xMissile + nave.getWidth() / 2, yMissile + nave.getHeight() / 2 - 12);
        batch.draw(nave, posX, posY);
        for (Rectangle enemy : enemies) {
            batch.draw(tEnemy, enemy.x, enemy.y);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        tNave.dispose();
    }

    private void moveNave() {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (posX < Gdx.graphics.getWidth() - nave.getWidth()) {
                posX += velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (posX > 0) {
                posX -= velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (posY < Gdx.graphics.getHeight() - nave.getHeight()) {
                posY += velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (posY > 0) {
                posY -= velocity;
            }
        }
    }

    private void moveMissible() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !attack) {
            attack = true;
            yMissile = posY;
        }
        if (attack) {
            if (xMissile < Gdx.graphics.getWidth()) {
                xMissile += 40;
            } else {
                xMissile = posX;
                attack = false;
            }
        } else {
            xMissile = posX;
            yMissile = posY;
        }
    }

    private void spawnEnemies() {
        Rectangle enemy = new Rectangle(Gdx.graphics.getWidth(), MathUtils.random(0, Gdx.graphics.getHeight() - tEnemy.getHeight()), tEnemy.getWidth(), tEnemy.getHeight());
        enemies.add(enemy);
        lastEnemyTime = TimeUtils.nanoTime();
    }

    private void moveEnemies() {
        if (TimeUtils.nanoTime() - lastEnemyTime > 1000000000) {
            this.spawnEnemies();
        }

        for (Iterator<Rectangle> iter = enemies.iterator(); iter.hasNext();) {
            Rectangle enemy = iter.next();
            enemy.x -= 200 * Gdx.graphics.getDeltaTime();
            if (enemy.x + tEnemy.getHeight() < 0 ) {
                iter.remove();
            }
        }
    }
}
