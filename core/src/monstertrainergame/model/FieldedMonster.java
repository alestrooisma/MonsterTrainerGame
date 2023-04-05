package monstertrainergame.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FieldedMonster {
    private final Monster monster;
    private final Vector2 position;
    private float distanceMovedThisTurn = 0;
    private boolean performedAbility = false;

    public FieldedMonster(Monster monster, float x, float y) {
        this(monster, new Vector2(x, y));
    }

    public FieldedMonster(Monster monster, Vector2 position) {
        this.monster = monster;
        this.position = position;
    }

    public String getTypeName() {
        return monster.getTypeName();
    }

    public float getRadius() {
        return monster.getRadius();
    }

    public boolean isOwnedByPlayer() {
        return monster.isOwnedByPlayer();
    }

    public Array<Ability> getAbilities() {
        return monster.getAbilities();
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getRemainingMovementRange() {
        if (hasPerformedAbility()) {
            return 0;
        } else {
            return Math.max(0, monster.getMaxMovementRange() - distanceMovedThisTurn);
        }
    }

    public void moveTo(Vector2 destination) {
        distanceMovedThisTurn += position.dst(destination);
        this.position.set(destination);
    }

    public boolean hasPerformedAbility() {
        return performedAbility;
    }

    public void setPerformedAbility(boolean performedAbility) {
        this.performedAbility = performedAbility;
    }

    public void endTurn() {
        distanceMovedThisTurn = 0;
        performedAbility = false;
    }

    public boolean occupies(float x, float y) {
        float radius = monster.getRadius();
        float dx = x - position.x;
        float dy = y - position.y;
        return dx * dx + dy * dy <= radius * radius;
    }
}
