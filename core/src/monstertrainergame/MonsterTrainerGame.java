package monstertrainergame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import monstertrainergame.view.View;

public class MonsterTrainerGame extends ApplicationAdapter {
    private View view;

    @Override
    public void create() {
        // Create the view
        view = new View();

        // Set up an input event listener
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(view.getView());
        inputMultiplexer.addProcessor(new InputHandler());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void resize(int width, int height) {
        view.resize(width, height);
    }

    @Override
    public void render() {
        view.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        view.dispose();
    }

    private static class InputHandler extends InputAdapter {

        @Override
        public boolean keyUp(int keycode) {
            if (keycode == Keys.ESCAPE) {
                Gdx.app.exit();
                return true;
            }
            return false;
        }
    }
}
