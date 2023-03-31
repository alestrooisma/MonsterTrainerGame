package monstertrainergame.model;

public class MonsterType {
    private final String name;
    private final float radius;
    private final float maxMovementRange;

    public MonsterType(String name, float radius, float maxMovementRange) {
        this.name = name;
        this.radius = radius;
        this.maxMovementRange = maxMovementRange;
    }

    public String getName() {
        return name;
    }

    public float getRadius() {
        return radius;
    }

    public float getMaxMovementRange() {
        return maxMovementRange;
    }
}
