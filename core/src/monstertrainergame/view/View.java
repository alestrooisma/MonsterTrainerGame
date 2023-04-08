package monstertrainergame.view;

import aetherdriven.view.LayeredView;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import monstertrainergame.controller.BattleController;
import monstertrainergame.controller.CameraController;
import monstertrainergame.model.FieldedMonster;

public class View implements Disposable {
    // Owned
    private final Projection projection;
    private final CameraController cameraController;
    private final LayeredView view;
    // Not owned
    private final BattleLayer battleLayer;

    public View(BattleController controller) {
        // Create a camera
        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new ScreenViewport(camera);
        projection = new BattleProjection(viewport);

        // Create camera controller
        cameraController = new CameraController(projection, controller.getBattle().getBounds());
        cameraController.center();

        // Create the layered view
        view = new LayeredView(0.2f, 0.2f, 0.2f);

        // Add the map layer
        TiledMap map = new TmxMapLoader().load("maps/default-map.tmx");
        MapLayer mapLayer = new MapLayer(camera, map);
        view.add(mapLayer);

        // Add the battle layer
        battleLayer = new BattleLayer(controller, projection, cameraController);
        view.add(battleLayer);

        // Add the battle HUD
        view.add(new BattleHudLayer(controller));
    }

    public LayeredView getView() {
        return view;
    }

    public void add(FieldedMonster monster) {
        battleLayer.add(new Element(monster));
    }

    public void resize(int width, int height) {
        projection.update(width, height);
        cameraController.snap();
        view.resize(width, height);
    }

    public void render(float dt) {
        view.render(dt);
    }

    public void dispose() {
        view.dispose();
    }
}
