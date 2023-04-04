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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.Comparator;
import monstertrainergame.controller.BattleController;
import monstertrainergame.controller.CameraController;
import monstertrainergame.events.AbilityEvent;
import monstertrainergame.events.EndTurnEvent;
import monstertrainergame.events.EventDispatcher;
import monstertrainergame.events.EventListener;
import monstertrainergame.events.MoveEvent;
import monstertrainergame.events.StartTurnEvent;
import monstertrainergame.model.FieldedMonster;

public class BattleLayer extends AbstractLayer {
    // Owned
    private final InputProcessor inputProcessor = new BattleLayerInputProcessor();
    private final TweenEngine engine = new TweenEngine();
    private final ReverseYComparator reverseYComparator = new ReverseYComparator();
    private final ShapeRenderer renderer = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final Array<Element> elements = new Array<>();
    private boolean debug = false;
    private boolean playerTurn = false;
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
        // Handle camera controls
        final float vx = 6;
        final float vy = vx*1.3f;
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            cameraController.move(-vx*dt, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            cameraController.move(vx*dt, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            cameraController.move(0, -vy*dt);
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            cameraController.move(0, vy*dt);
        }
        projection.getCamera().update();

        // Update animations
        engine.update(dt);
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
                renderIndicators(e);
            }
            e.draw(batch);
        }

        // Finalize drawing
        batch.end();
    }

    private void renderIndicators(Element e) {
        // Finalize batch drawing and prepare shape rendering
        batch.end();
        renderer.begin(ShapeRenderer.ShapeType.Line);

        // Render indicators
        renderer.setColor(Color.WHITE);
        renderFootprint(e);
        renderer.setColor(Color.GRAY);
        renderMovementRange(e);

        // Finalize shape rendering and resume batch drawing
        renderer.end();
        batch.begin();
    }

    private void renderFootprint(Element e) {
        renderEllipse(e.getPosition(), e.getMonster().getRadius());
    }

    private void renderMovementRange(Element e) {
        renderEllipse(e.getPosition(), e.getMonster().getRemainingMovementRange());
    }

    private void renderEllipse(Vector3 center, float radiusInWorldCoordinates) {
        projection.worldToPixelCoordinates(radiusInWorldCoordinates, radiusInWorldCoordinates, pixel);
        renderer.ellipse(center.x - pixel.x, center.y - pixel.y, pixel.x * 2, pixel.y * 2);
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
            if (pointer != 0 || !playerTurn) {
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
            // Debug mode
            if (keycode == Input.Keys.F12) {
                debug = !debug; // Don't return true so other layers can enable debug mode too
            }
 
            // End turn
            if (playerTurn && keycode == Keys.ENTER) {
                controller.endTurn();
                return true;
            }

            return false;
        }
    }

    private class BattleLayerEventListener implements EventListener, TweenEngine.Callback {
        private final Vector3 origin = new Vector3();
        private final Vector3 target = new Vector3();

        @Override
        public void handleStartTurnEvent(StartTurnEvent event) {
            engine.add(this, null);
        }

        @Override
        public void handleEndTurnEvent(EndTurnEvent event) {
            playerTurn = false;
        }

        @Override
        public void handleMoveEvent(MoveEvent event) {
            Element element = findElement(event.getMonster());
            if (element != null) {
                projection.worldToPixelCoordinates(event.getDestination(), pixel);
                engine.add(element.getPosition(), pixel, 300);
            }
        }

        @Override
        public void handleAbilityEvent(AbilityEvent event) {
            projection.worldToPixelCoordinates(event.getMonster().getPosition(), origin);
            projection.worldToPixelCoordinates(event.getTarget().getPosition(), target);
            origin.z = determineProjectileHeight(event.getMonster());
            target.z = determineProjectileHeight(event.getTarget());

            // Create the projectile element
            Element projectile = new Element(event.getAbility().getName());
            projectile.setPosition(origin);

            // Set the rotation of the projectile
            pixel.set(target.x, target.y + target.z, 0).sub(origin.x, origin.y + origin.z, 0);
            float angle = MathUtils.acos(pixel.dot(0, 1, 0) / pixel.len());
            projectile.setRotation(Math.signum(pixel.x) * angle);
            // Note: angle is the absolute angle between vectors, so it needs to be multiplied by
            // the sign of the x component to get the rotation in the correct direction

            // Add to render list and animation engine
            elements.add(projectile);
            engine.add(projectile.getPosition(), target, 600);

            // Add callback to remove projectile from render list once animation is done
            engine.add(this, projectile);
        }

        private float determineProjectileHeight(FieldedMonster monster) {
            Element e = findElement(monster);
            Rectangle bounds = e.getSkin().getBounds();
            return bounds.y + bounds.height / 2;
        }

        @Override
        public void callback(Object payload) {
            if (payload != null) { // Remove projectile
                elements.removeValue((Element) payload, true);
            } else { // Start player turn
                playerTurn = true;
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
