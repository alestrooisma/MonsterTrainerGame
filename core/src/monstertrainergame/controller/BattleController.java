package monstertrainergame.controller;

import com.badlogic.gdx.math.Vector2;
import monstertrainergame.events.EndTurnEvent;
import monstertrainergame.events.EventDispatcher;
import monstertrainergame.events.MoveEvent;
import monstertrainergame.events.StartTurnEvent;
import monstertrainergame.model.Battle;
import monstertrainergame.model.FieldedMonster;
import static monstertrainergame.controller.BattleController.Interaction.*;

public class BattleController {
    // Not owned
    private final Battle battle;
    private FieldedMonster selected = null;
    private FieldedMonster target = null;
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
        return selected != null && (monster == null || monster == selected) && isWithinRange(x, y);
    }

    private boolean isWithinRange(float x, float y) {
        float distance2 = selected.getPosition().dst2(x, y);
        float remaining = selected.getRemainingMovementRange();
        return distance2 < remaining * remaining;
    }

    public void cancel() {
        selected = null;
    }

    public void endTurn() {
        // Clear any relevant state in this controller
        selected = null;

        // Fire end turn event
        EventDispatcher.instance.dispatch(new EndTurnEvent());

        //TODO execute AI turn
        for (FieldedMonster monster : battle.getOpponents()) {
            EventDispatcher.instance.dispatch(new MoveEvent(monster, new Vector2(52, 36)));
        }

        // Fire event for new player turn
        EventDispatcher.instance.dispatch(new StartTurnEvent());
    }

    public enum Interaction {
        NONE, SELECT, MOVE
    }
}
