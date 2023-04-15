package monstertrainergame.model;

public class Ability {
    private final String name;
    private final Type type;
    private final float damage;

    public Ability(String name, Type type, float damage) {
        this.name = name;
        this.type = type;
        this.damage = damage;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public float getDamage() {
        return damage;
    }

    public enum Type {
        MELEE, PROJECTILE
    }
}
