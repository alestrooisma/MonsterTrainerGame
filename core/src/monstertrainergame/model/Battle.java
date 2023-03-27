package monstertrainergame.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Battle {
    private final Rectangle bounds;
    private final Array<FieldedMonster> fieldedMonsters;
    private final Array<FieldedMonster> opponents;

    public Battle(Rectangle bounds, Array<FieldedMonster> fieldedMonsters, Array<FieldedMonster> opponents) {
        this.bounds = bounds;
        this.fieldedMonsters = fieldedMonsters;
        this.opponents = opponents;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Array<FieldedMonster> getFieldedMonsters() {
        return fieldedMonsters;
    }

    public Array<FieldedMonster> getOpponents() {
        return opponents;
    }
}
