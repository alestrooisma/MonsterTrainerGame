package monstertrainergame.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import monstertrainergame.model.FieldedMonster;

public class Element {
    // Owned
    private final Color tint = new Color(Color.WHITE);
    private final Vector3 position = new Vector3();
    private float rotation = 0;
    // Not owned
    private final FieldedMonster monster;
    private final Skin skin;

    public Element(FieldedMonster monster) {
        this(monster, ResourceManager.instance.get(monster.getTypeName()));
    }

    public Element(String skinId) {
        this(null, ResourceManager.instance.get(skinId));
    }

    public Element(FieldedMonster monster, Skin skin) {
        this.monster = monster;
        this.skin = skin;
    }

    public Color getTint() {
        return tint;
    }

    public void setTint(Color color) {
        setTint(color.r, color.g, color.b, color.a);
    }

    public void setTint(float r, float g, float b, float a) {
        tint.set(r, g, b, a);
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position.set(position);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public FieldedMonster getMonster() {
        return monster;
    }

    public Skin getSkin() {
        return skin;
    }

    public boolean contains(float x, float y) {
        return skin.getBounds() != null && skin.contains(x - position.x, y - position.y);
    }

    public void draw(SpriteBatch batch) {
        batch.setColor(tint);
        skin.draw(batch, position, rotation);
    }
}
