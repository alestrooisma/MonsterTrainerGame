package monstertrainergame.controller;

import com.badlogic.gdx.math.Vector2;
import monstertrainergame.events.AbilityEvent;
import monstertrainergame.events.EndTurnEvent;
import monstertrainergame.events.EventDispatcher;
import monstertrainergame.events.MoveEvent;
import monstertrainergame.events.StartTurnEvent;
import monstertrainergame.model.Ability;
import monstertrainergame.model.Battle;
import monstertrainergame.model.FieldedMonster;
import static monstertrainergame.controller.BattleController.Interaction.*;

public class BattleController {
    // Not owned
    private final Battle battle;
    private FieldedMonster selected = null;
    private FieldedMonster target = null;
    private Ability ability = null;
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
        switch (interaction) {
            case SELECT:
                selected = target;
                ability = selected.getAbilities().isEmpty() ? null : selected.getAbilities().first();
                break;
            case MOVE:
                EventDispatcher.instance.dispatch(new MoveEvent(selected, destination));
                break;
            case ABILITY:
                EventDispatcher.instance.dispatch(new AbilityEvent(selected, ability, target));
                break;
        }
    }

    public Interaction determineInteraction(float x, float y) {
        target = battle.getMonsterAt(x, y);
        if (canMoveTo(selected, x, y, target)) {
            destination.set(x, y);
            return MOVE;
        } else if (target != null) {
            if (target.isOwnedByPlayer()) {
                return SELECT;
            } else if (ability != null && !selected.hasPerformedAbility()) {
                return ABILITY;
            } else {
                return NONE;
            }
        } else {
            return NONE;
        }
    }

    private static boolean canMoveTo(FieldedMonster movingMonster, float x, float y, FieldedMonster monsterAtXY) {
        return movingMonster != null && (monsterAtXY == null || monsterAtXY == movingMonster)
                && isWithinMovementRange(movingMonster, x, y);
    }

    private static boolean isWithinMovementRange(FieldedMonster movingMonster, float x, float y) {
        float distance2 = movingMonster.getPosition().dst2(x, y);
        float remaining = movingMonster.getRemainingMovementRange();
        return distance2 < remaining * remaining;
    }

    public void cancel() {
        selected = null;
        ability = null;
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
        NONE, SELECT, MOVE, ABILITY
    }
}
