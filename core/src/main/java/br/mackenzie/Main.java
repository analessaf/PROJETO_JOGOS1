package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class Main implements ApplicationListener {
    Texture naveTexture;
    SpriteBatch spriteBatch;
    FitViewport viewport;
    Sprite naveSprite;

    Texture backgroundTexture;
    float backgroundY;
    Vector2 touchPos;
    Texture alienTexture;

    Array<Sprite> alienSprites;
    float alienTimer;

    Rectangle bucketRectangle;
    Rectangle dropRectangle;

    Sound dropSound;
    Music music;

    @Override
    public void create() {
        naveTexture = new Texture("nave_rosa.png");
        backgroundTexture = new Texture("ceu.jpg");
        alienTexture = new Texture("alien.png");

        spriteBatch = new SpriteBatch();    
        viewport = new FitViewport(8, 5);

        naveSprite = new Sprite(naveTexture);
        naveSprite.setSize(1.3f, 1.3f);

        touchPos = new Vector2();

        alienSprites = new Array<>();

        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();

        alienSound = Gdx.audio.newSound(Gdx.files.internal("alien.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("DaftPunk.mp3"));
        music.setLooping(true);
        music.setVolume(.5f);
        music.play();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }

    @Override
    public void render() {
        // Draw your application here.
        input();
        logic();
        draw();
    }

    private void input() {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            naveSprite.translateX(speed * delta); 
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            naveSprite.translateX(-speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            naveSprite.translateY(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            naveSprite.translateY(-speed * delta);
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            naveSprite.setCenterX(touchPos.x);
            naveSprite.setCenterY(touchPos.y);
        }
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float naveWidth = naveSprite.getWidth();
        float worldHeight = viewport.getWorldHeight();
        float naveHeight = naveSprite.getHeight();

        naveSprite.setX(MathUtils.clamp(naveSprite.getX(), 0, worldWidth - naveWidth));
        naveSprite.setY(MathUtils.clamp(naveSprite.getY(), 0, worldHeight - naveHeight));

        float delta = Gdx.graphics.getDeltaTime();

        bucketRectangle.set(naveSprite.getX(), naveSprite.getY(), naveWidth, naveHeight);

        float velocidadeFundo = 1f;
        backgroundY -= velocidadeFundo * delta;
        if (backgroundY <= -worldHeight){
            backgroundY = 0f;
        }
        

        //for (int i = alienSprites.size - 1; i >= 0; i--) {
          // Sprite alienSprite = alienSprites.get(i);
          //  alienSprite.translateY(-2f * delta);

           // if (naveSprite.getBoundingRectangle().overlaps(alienSprite.getBoundingRectangle())) {
            //    alienSprites.removeIndex(i);
           // } else if (alienSprite.getY() + alienSprite.getHeight() < 0) {
            //    alienSprites.removeIndex(i);
          //  }
       // }

       for (int i = alienSprites.size - 1; i >= 0; i--) {
            Sprite alienSprite = alienSprites.get(i); // Get the sprite from the list
            float alienWidth = alienSprite.getWidth();
            float alienHeight = alienSprite.getHeight();
            
            alienSprite.translateY( -2f * delta);
            
            // if the top of the drop goes below the bottom of the view, remove it
            if (alienSprite.getY() < -alienHeight) alienSprites.removeIndex(i);
            else if (bucketRectangle.overlaps(dropRectangle)){
                alienSprite.removeIndex(i);
                alienSound.play();
            }
        }

        alienTimer += delta;
        if (alienTimer > 1f) {
            alienTimer = 0;
            createAlienlet();
        }
    }

    public void createalien(){
        float alienWidth = 0.7f;
        float alienHeight = 0.7f;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite alienSprite = new Sprite(alienTexture);
        alienSprite.setSize(alienWidth, alienHeight);
        //alienSprite.setX(0);
        alienSprite.setX(MathUtils.random(0f, worldWidth - alienWidth));
        alienSprite.setY(worldHeight);
        alienSprites.add(alienSprite);

    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        naveSprite.draw(spriteBatch);

        for (Sprite alienSprite : alienSprites) {
            alienSprite.draw(spriteBatch);
        }
        spriteBatch.end();
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}
