package monstertrainergame.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Battle {
    private final Rectangle bounds;
    private final Array<FieldedMonster> fieldedMonsters;
    private final Array<FieldedMonster> opponents;

    public Battle(Rectangle bounds) {
        this(bounds, 3, 3);
    }

    public Battle(Rectangle bounds, int playerPartySize, int opponentPartySize) {
        this(bounds, new Array<FieldedMonster>(playerPartySize), new Array<FieldedMonster>(opponentPartySize));
    }

    public Battle(Rectangle bounds, Array<FieldedMonster> fieldedMonsters, Array<FieldedMonster> opponents) {
        this.bounds = bounds;
        this.fieldedMonsters = fieldedMonsters;
        this.opponents = opponents;
    }

    public void add(FieldedMonster monster) {
        if (monster.isOwnedByPlayer()) {
            fieldedMonsters.add(monster);
        } else {
            opponents.add(monster);
        }
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

    public FieldedMonster getMonsterAt(float x, float y) {
        FieldedMonster monster = getPlayerMonsterAt(x, y);
        if (monster != null) {
            return monster;
        } else {
            return getOpponentMonsterAt(x, y);
        }
    }

    public FieldedMonster getPlayerMonsterAt(float x, float y) {
        return getMonsterAt(x, y, fieldedMonsters);
    }

    public FieldedMonster getOpponentMonsterAt(float x, float y) {
        return getMonsterAt(x, y, opponents);
    }

    private FieldedMonster getMonsterAt(float x, float y, Array<FieldedMonster> monsters) {
        for (FieldedMonster monster : monsters) {
            if (monster.occupies(x, y)) {
                return monster;
            }
        }
        return null;
    }
}
