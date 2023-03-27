package monstertrainergame.view;

import aetherdriven.view.AbstractLayer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;

public class MapLayer extends AbstractLayer {
    // Owned
    private final HexagonalTiledMapRenderer renderer;
    // Not owned
    private final OrthographicCamera camera;

    public MapLayer(OrthographicCamera camera, TiledMap map) {
        this.renderer = new HexagonalTiledMapRenderer(map);
        this.camera = camera;
    }

    @Override
    public void render() {
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
