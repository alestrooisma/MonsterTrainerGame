package monstertrainergame.view;

import aetherdriven.view.LayeredView;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class View implements Disposable {
    // Owned
    private final LayeredView view;
    private final Viewport viewport;

    public View() {
        // Create a camera
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        camera.position.set(720, 720, 0);

        // Create the layered view
        view = new LayeredView(0.2f, 0.2f, 0.2f);

        // Add the map layer
        TiledMap map = new TmxMapLoader().load("maps/default-map.tmx");
        MapLayer mapLayer = new MapLayer(camera, map);
        view.add(mapLayer);
    }

    public LayeredView getView() {
        return view;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        view.resize(width, height);
    }

    public void render(float dt) {
        view.render(dt);
    }

    public void dispose() {
        view.dispose();
    }
}
