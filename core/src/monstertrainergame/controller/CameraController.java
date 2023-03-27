package monstertrainergame.controller;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import monstertrainergame.view.Projection;

public class CameraController {
    private final Projection projection;
    private final Rectangle bounds;
    private final Vector3 pixel = new Vector3();
    private final Vector2 world = new Vector2();

    public CameraController(Projection projection, Rectangle bounds) {
        this.projection = projection;
        this.bounds = bounds;
    }

    public void center() {
        set(bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f);
    }

    public void set(Vector2 position) {
        set(position.x, position.y);
    }

    public void set(float x, float y) {
        // Get viewport with in world coordinates
        projection.pixelToWorldCoordinates(projection.getViewport().getWorldWidth() / 2, projection.getViewport().getWorldHeight() / 2, world);
        float halfWorldWidth = world.x;
        float halfWorldHeight = world.y;

        // Determine minimum and maximum allowed position for the camera
        float minX = bounds.x + halfWorldWidth;
        float minY = bounds.y + halfWorldHeight;
        float maxX = bounds.x + bounds.width - halfWorldWidth;
        float maxY = bounds.y + bounds.height - halfWorldHeight;

        // Limit camera position
        if (minX < maxX) {
            x = MathUtils.clamp(x, minX, maxX);
        } else {
            x = bounds.x + bounds.width / 2;
        }
        if (minY < maxY) {
            y = MathUtils.clamp(y, minY, maxY);
        } else {
            y = bounds.y + bounds.height / 2;
        }

        // Actually set the camera position
        projection.worldToPixelCoordinates(x, y, pixel);
        projection.getCamera().position.set(pixel.x, pixel.y, pixel.z);
    }

    public void move(float dx, float dy) {
        projection.pixelToWorldCoordinates(projection.getCamera().position, world);
        set(world.x + dx, world.y + dy);
    }

    public void snap() {
        projection.pixelToWorldCoordinates(projection.getCamera().position, world);
        set(world);
    }
}
