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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image, tNave, tEnemy, tMissile;
    private Sprite nave, enemy, missile;
    private float posX, posY, velocity, xMissile, yMissile;
    private boolean attack, gameOver;
    private Array<Rectangle> enemies;
    private long lastEnemyTime;
    private int score, power, numEnemies;

    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont bitmap;

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

        score = 0;
        power = 3;
        numEnemies = 799999999;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 30;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;
        bitmap = generator.generateFont(parameter);

        gameOver = false;
    }

    @Override
    public void render() {

        this.moveNave();
        this.moveMissible();
        this.moveEnemies();

        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        batch.draw(image, 0, 0);

        if (!gameOver) {
            if (attack) {
                batch.draw(missile, xMissile, yMissile);
            }
            batch.draw(nave, posX, posY);

            for (Rectangle enemy : enemies) {
                batch.draw(tEnemy, enemy.x, enemy.y);
            }
            bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
            bitmap.draw(batch, "Power: " + power, Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 20);

        } else  {
            bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
            bitmap.draw(batch, "GAME OVER!" + power, Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 20);

            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                score = 0;
                power = 3;
                posX = 0;
                posY = 0;
                gameOver = false;
            }
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
            yMissile = posY + nave.getHeight() / 2 - 12;
        }
        if (attack) {
            if (xMissile < Gdx.graphics.getWidth()) {
                xMissile += 40;
            } else {
                xMissile = Gdx.graphics.getWidth();
                attack = false;
            }
        } else {
            xMissile = posX - nave.getHeight() / 2;
            yMissile = posY + nave.getHeight() / 2 - 12;
        }
    }

    private void spawnEnemies() {
        Rectangle enemy = new Rectangle(Gdx.graphics.getWidth(), MathUtils.random(0, Gdx.graphics.getHeight() - tEnemy.getHeight()), tEnemy.getWidth(), tEnemy.getHeight());
        enemies.add(enemy);
        lastEnemyTime = TimeUtils.nanoTime();
    }

    private void moveEnemies() {
        if (TimeUtils.nanoTime() - lastEnemyTime > numEnemies) {
            this.spawnEnemies();
        }

        for (Iterator<Rectangle> iter = enemies.iterator(); iter.hasNext();) {
            Rectangle enemy = iter.next();
            enemy.x -= 200 * Gdx.graphics.getDeltaTime();

            // Colisão com o missil
            if (collide(enemy.x, enemy.y, enemy.width, enemy.height, xMissile, yMissile, missile.getHeight(), missile.getHeight()) && attack){
//                System.out.println("Score: " + ++score);
                ++score;
                if (score % 2 == 0) {
                    numEnemies -= 100;
                }
                attack = false;
                iter.remove();
            // Colisão com a nave
            } else if (collide(enemy.x, enemy.y, enemy.width, enemy.height, posX, posY, nave.getHeight(), nave.getHeight()) && !gameOver) {
                if (power <= 0) {
                    gameOver = true;
                }
                --power;
                iter.remove();
            }

            if (enemy.x + tEnemy.getHeight() < 0 ) {
                iter.remove();
            }
        }
    }

    private boolean collide(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
        if (x1 + w1 > x2 && x1 < x2 + w2 && y1 + h1 > y2 && y1 < y2 + h2) {
            return true;
        }
        return false;
    }
}
