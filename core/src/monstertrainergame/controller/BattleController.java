package monstertrainergame.controller;

import com.badlogic.gdx.math.Vector2;
import monstertrainergame.model.Battle;
import monstertrainergame.model.FieldedMonster;
import monstertrainergame.model.events.EventDispatcher;
import static monstertrainergame.controller.BattleController.Interaction.*;
import monstertrainergame.model.events.MoveEvent;

public class BattleController {
    // Not owned
    private final Battle battle;
    private FieldedMonster selected = null;
    private FieldedMonster target = null;
    // Utilities
    private final Vector2 destination = new Vector2();

    public BattleController(Battle battle) {
        this.battle = battle;
    }

    public Battle getBattle() {
        return battle;
    }

    public FieldedMonster getSelected() {
        return selected;
    }

    public void interact(float x, float y) {
        Interaction interaction = determineInteraction(x, y);
        if (interaction == SELECT) {
            selected = target;
        } else if (interaction == MOVE) {
            EventDispatcher.instance.dispatch(new MoveEvent(selected, destination));
        }
    }

    public Interaction determineInteraction(float x, float y) {
        target = battle.getPlayerMonsterAt(x, y);
        if (canMoveTo(x, y)) {
            destination.set(x, y);
            return MOVE;
        } else if (target != null) {
            return SELECT;
        } else {
            return NONE;
        }
    }

    private boolean canMoveTo(float x, float y) {
        FieldedMonster monster = battle.getMonsterAt(x, y);
        return selected != null && (monster == null || monster == selected);
    }

    public void cancel() {
        selected = null;
    }

    public enum Interaction {
        NONE, SELECT, MOVE
    }
}
