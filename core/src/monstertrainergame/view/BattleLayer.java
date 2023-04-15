package monstertrainergame.view;

import aetherdriven.view.AbstractLayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import static com.badlogic.gdx.math.MathUtils.ceil;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.Comparator;
import monstertrainergame.controller.BattleController;
import monstertrainergame.controller.BattleController.Interaction;
import static monstertrainergame.controller.BattleController.Interaction.ABILITY;
import static monstertrainergame.controller.BattleController.Interaction.MOVE;
import static monstertrainergame.controller.BattleController.Interaction.MOVE_AND_ABILITY;
import static monstertrainergame.controller.BattleController.Interaction.SELECT;
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
    private final AbilityAnimator animator = new AbilityAnimator();
    private final SelectionCallback selectionCallback = new SelectionCallback();
    private final ReverseYComparator reverseYComparator = new ReverseYComparator();
    private final TweenEngine engine = new TweenEngine();
    private final ProjectedShapeRenderer renderer;
    private final SpriteBatch batch = new SpriteBatch();
    private final Texture white;
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
    private Element selected = null;
    private boolean pendingSelection = false;

    public BattleLayer(BattleController controller, Projection projection, CameraController cameraController) {
        this.controller = controller;
        this.projection = projection;
        this.cameraController = cameraController;
        this.renderer = new ProjectedShapeRenderer(projection);
        white = createSinglePixelTexture();
        EventDispatcher.instance.register(new BattleLayerEventListener());
    }

    private Texture createSinglePixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void add(Element e) {
        elements.add(e);
        // Set the element position to the pixel coordinates matching the unit's world coordinates
        if (e.getMonster() != null) {
            projection.worldToPixelCoordinates(e.getMonster().getPosition(), e.getPosition());
        }
    }

    public Element findElement(FieldedMonster monster) {
        for (Element e : elements) {
            if (e.getMonster() == monster) {
                return e;
            }
        }
        return null;
    }

    private FieldedMonster getMonsterAt(Vector3 pixel) {
        Element e = getElementAt(pixel);
        return e != null ? e.getMonster() : null;
    }

    private Element getElementAt(Vector3 pixel) {
        Element touchedElement = null;

        // Iterate the elements in reverse to get the topmost element
        // for which the coordinates are in its skin's bounding box.
        for (int i = elements.size - 1; touchedElement == null && i >= 0; i--) {
            Element e = elements.get(i);
            if (e.contains(pixel.x, pixel.y)) {
                touchedElement = e;
            }
        }

        // Return the topmost element at (x, y), which may be null
        return touchedElement;
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
        // Apply camera settings
        batch.setProjectionMatrix(projection.getCamera().combined);
        renderer.setProjectionMatrix(projection.getCamera().combined);

        // Trigger selection change callback if needed
        if (!pendingSelection && (
                (selected == null && controller.getSelectedMonster() != null) ||
                (selected != null && selected.getMonster() != controller.getSelectedMonster()))) {
            selected = null;
            engine.add(selectionCallback, null);
            pendingSelection = true;
        }

        // Perform actual rendering
        renderIndicators();
        renderElements();
    }

    private void renderIndicators() {
        // Prepare rendering
        renderer.begin(ShapeRenderer.ShapeType.Line);

        // Render various indicators
        renderSelectionIndicator();
        renderInteractionIndicators();
        if (debug) {
            renderDebugLines();
        }

        // Finalize rendering
        renderer.end();
    }

    private void renderElements() {
        // Prepare drawing
        batch.begin();

        // Render all elements (with decoration)
        elements.sort(reverseYComparator);
        for (Element e : elements) {
            e.draw(batch);
            if (e.getMonster() != null && e.getMonster().getCurrentHealth() > 0) {
                drawHealthBar(e);
            }
        }

        // Finalize drawing
        batch.end();
    }

    private void drawHealthBar(Element e) {
        Rectangle bounds = e.getSkin().getBounds();

        // Calculate full bar sizes
        float width = 50;
        float height = 5;
        float x = e.getPosition().x - width / 2;
        float y = e.getPosition().y + bounds.y + bounds.height + 5;

        // Calculate bar fill with
        float healthFraction = e.getMonster().getCurrentHealth() / e.getMonster().getMaxHealth();
        float fillWidth = ceil((width - 2) * healthFraction);

        // Draw the health bar box
        batch.setColor(Color.BLACK);
        batch.draw(white, x, y, width, 1);
        batch.draw(white, x, y + height - 1, width, 1);
        batch.draw(white, x, y, 1, height);
        batch.draw(white, x + width - 1, y, 1, height);

        // Draw the health bar itself
        batch.setColor(Color.RED);
        batch.draw(white, x + 1, y + 1, fillWidth, height - 2);
    }

    private void renderSelectionIndicator() {
        if (selected != null) {
            renderer.setColor(Color.WHITE);
            renderFootprint(selected);

            renderer.setColor(Color.GRAY);
            renderMovementRange(selected);
        }
    }

    private void renderInteractionIndicators() {
        // Determine interaction if player clicked at current mouse position
        projection.screenToPixelCoordinates(Gdx.input.getX(), Gdx.input.getY(), pixel);
        projection.pixelToWorldCoordinates(pixel, world);
        Interaction interaction = controller.determineInteraction(world, getMonsterAt(pixel));

        // Render selection interaction indicator if applicable
        if (interaction == SELECT) {
            renderer.setColor(Color.WHITE);
            Element e = findElement(controller.getTarget());
            renderFootprint(e);
        }

        // Need selected monster for the following part
        if (selected == null) {
            return;
        }

        // Render movement indicator if applicable
        if (interaction == MOVE || interaction == MOVE_AND_ABILITY) {
            renderer.setColor(Color.WHITE);
            renderer.ellipse(controller.getDestination(), selected.getMonster().getRadius());
            renderer.arrow(selected.getMonster().getPosition(), controller.getDestination(), 0.6f, 0.3f);
        }

        // Render attack indicator if applicable
        if (interaction == ABILITY || interaction == MOVE_AND_ABILITY) {
            renderer.setColor(Color.RED);
            renderer.ellipse(controller.getTarget().getPosition(), controller.getTarget().getRadius());
            renderer.arrow(controller.getDestination(), controller.getTarget().getPosition(), 0.6f, 0.3f);
        }
    }

    private void renderDebugLines() {
        // Set debug color
        renderer.setColor(Color.PINK);

        // Render debug lines for all elements
        for (Element e : elements) {
            Rectangle bounds = e.getSkin().getBounds();
            renderer.rectangle(bounds, e.getPosition());
            renderFootprint(e);
        }
    }

    private void renderFootprint(Element e) {
        renderer.ellipse(e.getPosition(), e.getMonster().getRadius());
    }

    private void renderMovementRange(Element e) {
        renderer.ellipse(e.getPosition(), e.getMonster().getRadius() + e.getMonster().getRemainingMovementRange());
    }

    @Override
    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    @Override
    public void dispose() {
        white.dispose();
        batch.dispose();
        renderer.dispose();
    }

    private class BattleLayerInputProcessor extends InputAdapter {
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (pointer != 0 || !playerTurn) {
                return false;
            }

            if (button == Input.Buttons.LEFT) {
                projection.screenToPixelCoordinates(screenX, screenY, pixel);
                projection.pixelToWorldCoordinates(pixel, world);
                controller.interact(world.x, world.y, getMonsterAt(pixel));
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

        @Override
        public void handleStartTurnEvent(StartTurnEvent event) {
            engine.add(this, null);
        }

        @Override
        public void handleEndTurnEvent(EndTurnEvent event) {
            selected = null;
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
            // Animate the ability
            switch (event.getAbility().getType()) {
                case MELEE:
                    animator.animateMeleeAbility(event);
                    break;
                case PROJECTILE:
                    animator.animateProjectileAbility(event);
                    break;
            }

            // Animate target death if applicable
            if (event.getTarget().getCurrentHealth() == 0) {
                animator.animateDeath(event.getTarget());
            }
        }

        @Override
        public void callback(Object payload) {
            // Start player turn after all enemy animations have finished
            playerTurn = true;
        }
    }

    private class AbilityAnimator implements TweenEngine.Callback {
        private final Color invisiblack = new Color(0, 0, 0, 0);
        private final Vector3 origin = new Vector3();
        private final Vector3 target = new Vector3();

        public void animateMeleeAbility(AbilityEvent event) {
            projection.worldToPixelCoordinates(event.getMonster().getPosition(), origin);
            projection.worldToPixelCoordinates(event.getTarget().getPosition(), target);

            Vector3 pos = findElement(event.getMonster()).getPosition();
            engine.add(pos, target, 400);
            engine.add(pos, origin, 200);
        }

        public void animateProjectileAbility(AbilityEvent event) {
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

        public void animateDeath(FieldedMonster target) {
            Element e = findElement(target);
            engine.add(e.getTint(), invisiblack, 0.8f);
            engine.add(this, e);
        }

        @Override
        public void callback(Object payload) {
            elements.removeValue((Element) payload, true);
        }
    }

    private class SelectionCallback implements TweenEngine.Callback {
        @Override
        public void callback(Object payload) {
            selected = controller.getSelectedMonster() != null ? findElement(controller.getSelectedMonster()) : null;
            pendingSelection = false;
        }
    }

    private static class ReverseYComparator implements Comparator<Element> {
        @Override
        public int compare(Element first, Element second) {
            return Float.compare(second.getPosition().y, first.getPosition().y);
        }
    }
}
