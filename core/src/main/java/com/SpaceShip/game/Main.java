package com.SpaceShip.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image, tNave, tEnemy, tMissile;
    private Sprite nave, enemy, missile;
    private float posX, posY, velocity, xMissile, yMissile;




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
    }

    @Override
    public void render() {

        this.moveNave();
        this.moveMissible();

        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        batch.draw(image, 0, 0);
        batch.draw(missile, xMissile + nave.getWidth() / 2, yMissile + nave.getHeight() / 2 - 12);
        batch.draw(nave, posX, posY);
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
        yMissile = posY;
        xMissile = posX;
    }
}
