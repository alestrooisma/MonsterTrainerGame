package monstertrainergame.view;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class ResourceManager implements Disposable {
    public static ResourceManager instance = null;
    private final ObjectMap<String, Skin> skins = new ObjectMap<>();

    private ResourceManager() {
    }

    public void add(String id, Skin skin) {
        skins.put(id, skin);
    }

    public Skin get(String id) {
        return skins.get(id);
    }

    @Override
    public void dispose() {
        for (ObjectMap.Entry<String, Skin> entry : skins) {
            Skin skin = entry.value;
            skin.dispose();
        }
        instance = null;
    }

    public static ResourceManager create() {
        if (instance != null) {
            throw new IllegalStateException("A ResourceManager instance already exists");
        }
        instance = new ResourceManager();
        return instance;
    }
}
