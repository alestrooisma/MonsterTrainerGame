package monstertrainergame.model;

public class Ability {
    private final String name;
    private final Type type;

    public Ability(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        MELEE, PROJECTILE
    }
}
