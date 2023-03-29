package monstertrainergame.model;

import com.badlogic.gdx.math.Vector2;

public class FieldedMonster {
    private final Monster monster;
    private final Vector2 position;

    public FieldedMonster(Monster monster, float x, float y) {
        this(monster, new Vector2(x, y));
    }

    public FieldedMonster(Monster monster, Vector2 position) {
        this.monster = monster;
        this.position = position;
    }

    public MonsterType getType() {
        return monster.getType();
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean occupies(float x, float y) {
        float radius = monster.getType().getRadius();
        float dx = x - position.x;
        float dy = y - position.y;
        return dx * dx + dy * dy <= radius * radius;
    }
}
