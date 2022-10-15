package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class DropGame extends ApplicationAdapter {

    private float bucketSpeed = 300;
    private float diamondSpeed = 200;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Texture bucketImage;
    private Texture backgroundImage;
    private Texture diamondImage;

    private Rectangle bucket;

    private Sound dropSound;
    private Music backgroundMusic;

    private Array<Rectangle> diamonds;
    private long lastDropTime;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        bucketImage = new Texture("bucket.png");
        diamondImage = new Texture("diamond.png");
        backgroundImage = new Texture("background_image.png");

        bucket = new Rectangle();
        bucket.setWidth(64);
        bucket.setHeight(75);

        bucket.setX((800 - bucket.width) / 2);
        bucket.setY(10);

        diamonds = new Array<>();
        spawnDiamond();

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("moog_city_2.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        batch = new SpriteBatch();
    }

    private void spawnDiamond() {
        Rectangle diamond = new Rectangle();

        diamond.setWidth(32);
        diamond.setHeight(35);

        // random between 10 and 800 - 10 - diamond.width
        diamond.setX(MathUtils.random(10, 800 - 10 - diamond.width));

        diamond.setY(600 - diamond.height - 10);

        diamonds.add(diamond);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);

        if (TimeUtils.nanoTime() - lastDropTime > Math.pow(10, 9)) {
            spawnDiamond();
        }

        batch.begin();
        batch.draw(backgroundImage, 0, 0);
        batch.draw(bucketImage, bucket.x, bucket.y);

        for (Rectangle diamond : diamonds) {
            batch.draw(diamondImage, diamond.x, diamond.y);
        }

        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.x -= Gdx.graphics.getDeltaTime() * bucketSpeed;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x += Gdx.graphics.getDeltaTime() * bucketSpeed;
        }

        if (bucket.x < 10) {
            bucket.setX(10);
        }

        if (bucket.x > 800 - 10 - bucket.width) {
            bucket.setX(800 - 10 - bucket.width);
        }

        Array<Rectangle> diamondsToRemove = new Array<>();

        for (Rectangle diamond : diamonds) {
            diamond.y -= diamondSpeed * Gdx.graphics.getDeltaTime();

            if (diamond.y < 10) {
                diamondsToRemove.add(diamond);
            }

            if (diamond.y < bucket.height + 10 - 20 && bucket.overlaps(diamond)) {
                dropSound.play();
                diamondsToRemove.add(diamond);
            }
        }

        diamonds.removeAll(diamondsToRemove, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        bucketImage.dispose();
        diamondImage.dispose();
        dropSound.dispose();
        backgroundMusic.dispose();
    }
}
