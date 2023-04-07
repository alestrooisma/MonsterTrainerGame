package monstertrainergame.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

/**
 * A wrapper for ShapeRenderer that uses a Projection object to distinguish between world
 * coordinates (Vector2) en pixel coordinates (Vector3)
 */
public class ProjectedShapeRenderer implements Disposable {
    // Owned
    private final ShapeRenderer renderer = new ShapeRenderer();
    // Not owned
    private final Projection projection;
    // Utilities
    private final Vector3 from3 = new Vector3();
    private final Vector3 to3 = new Vector3();
    private final Vector3 ellipseCenter = new Vector3();
    private final Vector3 ellipseAxes = new Vector3();
    private final Vector2 arrowHeadBase = new Vector2();
    private final Vector2 arrowHeadWidth = new Vector2();
    private final Vector2 vec2 = new Vector2();

    public ProjectedShapeRenderer(Projection projection) {
        this.projection = projection;
    }

    public void setColor(Color color) {
        renderer.setColor(color);
    }

    public void setProjectionMatrix(Matrix4 matrix) {
        renderer.setProjectionMatrix(matrix);
    }

    public void begin(ShapeRenderer.ShapeType type) {
        renderer.begin(type);
    }

    public void end() {
        renderer.end();
    }

    public void line(Vector2 from, Vector2 to) {
        projection.worldToPixelCoordinates(from, from3);
        projection.worldToPixelCoordinates(to, to3);
        renderer.line(from3, to3);
    }

    public void line(Vector2 from, Vector3 to) {
        projection.worldToPixelCoordinates(from, from3);
        renderer.line(from3, to);
    }

    public void rectangle(Rectangle rect, Vector3 offset) {
        renderer.rect(offset.x + rect.x, offset.y + rect.y, rect.width, rect.height);
    }

    public void ellipse(Vector2 center, float radiusInWorldCoordinates) {
        projection.worldToPixelCoordinates(center, ellipseCenter);
        ellipse(ellipseCenter, radiusInWorldCoordinates);
    }

    public void ellipse(Vector3 center, float radiusInWorldCoordinates) {
        projection.worldToPixelCoordinates(radiusInWorldCoordinates, radiusInWorldCoordinates, ellipseAxes);
        ellipse(center, ellipseAxes);
    }

    public void ellipse(Vector3 center, Vector3 axes) {
        renderer.ellipse(center.x - axes.x, center.y - axes.y, axes.x * 2, axes.y * 2);
    }

    public void arrow(Vector2 from, Vector2 to, float length, float width) {
        arrowHeadBase.set(from).sub(to).nor().scl(length).add(to);
        arrowHeadWidth.set(to).sub(from).nor().rotateDeg(90).scl(width);

        line(from, to);
        line(vec2.set(arrowHeadBase).sub(arrowHeadWidth), to3); // Don't recalculate to3
        line(vec2.set(arrowHeadBase).add(arrowHeadWidth), to3); // Don't recalculate to3
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
