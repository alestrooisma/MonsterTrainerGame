package aetherdriven.view;

import com.badlogic.gdx.InputProcessor;

public abstract class AbstractLayer implements Layer {

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render() {
    }

    @Override
    public InputProcessor getInputProcessor() {
        return Layer.DEFAULT_INPUT_PROCESSOR;
    }
}
