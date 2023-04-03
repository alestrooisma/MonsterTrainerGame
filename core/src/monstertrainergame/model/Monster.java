package monstertrainergame.model;

public class Monster {
    private final MonsterType type;
    private final boolean ownedByPlayer;

    public Monster(MonsterType type, boolean ownedByPlayer) {
        this.type = type;
        this.ownedByPlayer = ownedByPlayer;
    }

    public MonsterType getType() {
        return type;
    }

    public boolean isOwnedByPlayer() {
        return ownedByPlayer;
    }

    public float getMaxMovementRange() {
        return type.getMaxMovementRange();
    }
}
