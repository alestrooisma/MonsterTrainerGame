package monstertrainergame.view;

import aetherdriven.view.AbstractLayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.Comparator;
import monstertrainergame.controller.BattleController;
import monstertrainergame.controller.CameraController;
import monstertrainergame.model.FieldedMonster;
import monstertrainergame.model.events.EventDispatcher;
import monstertrainergame.model.events.EventListener;
import monstertrainergame.model.events.MoveEvent;

public class BattleLayer extends AbstractLayer {
    // Owned
    private final InputProcessor inputProcessor = new BattleLayerInputProcessor();
    private final ReverseYComparator reverseYComparator = new ReverseYComparator();
    private final ShapeRenderer renderer = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final Array<Element> elements = new Array<>();
    private boolean debug = false;
    // Not owned
    private final BattleController controller;
    private final Projection projection;
    private final CameraController cameraController;
    // Utilities
    private final Vector3 pixel = new Vector3();
    private final Vector2 world = new Vector2();

    public BattleLayer(BattleController controller, Projection projection, CameraController cameraController) {
        this.controller = controller;
        this.projection = projection;
        this.cameraController = cameraController;
        EventDispatcher.instance.register(new BattleLayerEventListener());
    }

    public void add(Element e) {
        elements.add(e);
        // Set the element position to the pixel coordinates matching the unit's world coordinates
        if (e.getMonster() != null) {
            projection.worldToPixelCoordinates(e.getMonster().getPosition(), e.getPosition());
        }
    }

    @Override
    public void update(float dt) {
        final float v = 150;
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            cameraController.move(-v*dt, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            cameraController.move(v*dt, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            cameraController.move(0, -v*dt);
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            cameraController.move(0, v*dt);
        }
        projection.getCamera().update();
    }

    @Override
    public void render() {
        batch.setProjectionMatrix(projection.getCamera().combined);
        renderer.setProjectionMatrix(projection.getCamera().combined);

        if (debug) {
            renderDebugLines();
        }
        renderElements();
    }

    private void renderElements() {
        // Prepare drawing
        batch.begin();

        // Render all elements (with decoration)
        elements.sort(reverseYComparator);
        for (Element e : elements) {
            if (e.getMonster() == controller.getSelected()) {
                renderIndicator(e);
            }
            e.draw(batch);
        }

        // Finalize drawing
        batch.end();
    }

    private void renderIndicator(Element e) {
        // Finalize batch drawing and prepare shape rendering
        batch.end();
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.WHITE);

        // Render indicator
        renderFootprint(e);

        // Finalize shape rendering and resume batch drawing
        renderer.end();
        batch.begin();
    }

    private void renderFootprint(Element e) {
        float radius = e.getMonster().getType().getRadius();
        projection.worldToPixelCoordinates(radius, radius, pixel);
        renderer.ellipse(e.getPosition().x - pixel.x, e.getPosition().y - pixel.y, pixel.x * 2, pixel.y * 2);
    }

    private void renderDebugLines() {
        // Prepare drawing
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.PINK);

        // Render debug lines for all elements
        for (Element e : elements) {
            Rectangle bounds = e.getSkin().getBounds();
            renderer.rect(e.getPosition().x + bounds.x, e.getPosition().y + bounds.y, bounds.width, bounds.height);
            renderFootprint(e);
        }

        // Finalize drawing
        renderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    public Element findElement(FieldedMonster monster) {
        for (Element e : elements) {
            if (e.getMonster() == monster) {
                return e;
            }
        }
        return null;
    }

    private class BattleLayerInputProcessor extends InputAdapter {
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (pointer != 0) {
                return false;
            }

            if (button == Input.Buttons.LEFT) {
                projection.screenToWorldCoordinates(screenX, screenY, world);
                controller.interact(world.x, world.y);
            } else if (button == Input.Buttons.RIGHT) {
                controller.cancel();
            } else {
                return false;
            }

            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (keycode == Input.Keys.F12) {
                debug = !debug; // Don't return true so other layers can enable debug mode too
            }
            return false;
        }
    }

    private class BattleLayerEventListener implements EventListener {

        @Override
        public void handleMoveEvent(MoveEvent event) {
            Element element = findElement(event.getMonster());
            if (element != null) {
                projection.worldToPixelCoordinates(event.getDestination(), element.getPosition());
            }
        }
    }

    private static class ReverseYComparator implements Comparator<Element> {
        @Override
        public int compare(Element first, Element second) {
            return Float.compare(second.getPosition().y, first.getPosition().y);
        }
    }
}
