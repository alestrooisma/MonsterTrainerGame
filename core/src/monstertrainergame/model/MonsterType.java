package monstertrainergame.model;

public class MonsterType {
    private final String name;
    private final float radius;
    private final float maxMovementRange;
    private final float maxHealth;

    public MonsterType(String name, float radius, float maxMovementRange, float maxHealth) {
        this.name = name;
        this.radius = radius;
        this.maxMovementRange = maxMovementRange;
        this.maxHealth = maxHealth;
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

    public float getMaxHealth() {
        return maxHealth;
    }
}
