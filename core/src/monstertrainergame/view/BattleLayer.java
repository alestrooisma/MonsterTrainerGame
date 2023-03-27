package monstertrainergame.view;

import aetherdriven.view.AbstractLayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import java.util.Comparator;
import monstertrainergame.controller.CameraController;

public class BattleLayer extends AbstractLayer {
    // Owned
    private final ReverseYComparator reverseYComparator = new ReverseYComparator();
    private final SpriteBatch batch = new SpriteBatch();
    private final Array<Element> elements = new Array<>();
    // Not owned
    private final Projection projection;
    private final CameraController cameraController;

    public BattleLayer(Projection projection, CameraController cameraController) {
        this.projection = projection;
        this.cameraController = cameraController;
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
        // Prepare drawing
        batch.setProjectionMatrix(projection.getCamera().combined);
        batch.begin();

        // Render all elements (with decoration)
        elements.sort(reverseYComparator);
        for (Element e : elements) {
            e.draw(batch);
        }

        // Finalize drawing
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private static class ReverseYComparator implements Comparator<Element> {
        @Override
        public int compare(Element first, Element second) {
            return Float.compare(second.getPosition().y, first.getPosition().y);
        }
    }
}
