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
    // Owned
    private final Pathfinder pathfinder;
    // Not owned
    private final Battle battle;
    private FieldedMonster selected = null;
    private FieldedMonster target = null;
    private Ability ability = null;
    private final Vector2 destination = new Vector2();

    public BattleController(Battle battle) {
        this.battle = battle;
        this.pathfinder = new Pathfinder(battle);
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
        if (target != null && target.isOwnedByPlayer()) {
            return SELECT;
        } else if (target != null && ability != null && !selected.hasPerformedAbility()) {
            return ABILITY;
        } else if (selected != null) {
            pathfinder.determineMovementDestinationTowards(selected, x, y, destination);
            return MOVE;
        } else {
            return NONE;
        }
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
