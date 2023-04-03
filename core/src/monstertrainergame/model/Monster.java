package monstertrainergame.model;

import com.badlogic.gdx.utils.Array;

public class Monster {
    private final MonsterType type;
    private final boolean ownedByPlayer;
    private final Array<Ability> abilities;

    public Monster(MonsterType type, boolean ownedByPlayer) {
        this(type, ownedByPlayer, new Array<Ability>(4));
    }

    public Monster(MonsterType type, boolean ownedByPlayer, Array<Ability> abilities) {
        this.type = type;
        this.ownedByPlayer = ownedByPlayer;
        this.abilities = abilities;
    }

    public MonsterType getType() {
        return type;
    }

    public boolean isOwnedByPlayer() {
        return ownedByPlayer;
    }

    public Array<Ability> getAbilities() {
        return abilities;
    }

    public float getMaxMovementRange() {
        return type.getMaxMovementRange();
    }
}
