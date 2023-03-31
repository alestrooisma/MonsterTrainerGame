package monstertrainergame.model;

public class Monster {
    private final MonsterType type;

    public Monster(MonsterType type) {
        this.type = type;
    }

    public MonsterType getType() {
        return type;
    }

    public float getMaxMovementRange() {
        return type.getMaxMovementRange();
    }
}
