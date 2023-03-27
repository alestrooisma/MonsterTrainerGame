package monstertrainergame.model;

import com.badlogic.gdx.utils.Array;

public class Battle {
    private final Array<FieldedMonster> fieldedMonsters;
    private final Array<FieldedMonster> opponents;

    public Battle(Array<FieldedMonster> fieldedMonsters, Array<FieldedMonster> opponents) {
        this.fieldedMonsters = fieldedMonsters;
        this.opponents = opponents;
    }

    public Array<FieldedMonster> getFieldedMonsters() {
        return fieldedMonsters;
    }

    public Array<FieldedMonster> getOpponents() {
        return opponents;
    }
}
