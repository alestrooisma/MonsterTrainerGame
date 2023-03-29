package monstertrainergame.model;

public class MonsterType {
    private final String name;
    private final float radius;

    public MonsterType(String name, float radius) {
        this.name = name;
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public float getRadius() {
        return radius;
    }
}
