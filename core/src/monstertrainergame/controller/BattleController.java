package monstertrainergame.controller;

import com.badlogic.gdx.math.Vector2;
import static monstertrainergame.controller.BattleController.Interaction.ABILITY;
import static monstertrainergame.controller.BattleController.Interaction.MOVE;
import static monstertrainergame.controller.BattleController.Interaction.MOVE_AND_ABILITY;
import static monstertrainergame.controller.BattleController.Interaction.NONE;
import static monstertrainergame.controller.BattleController.Interaction.SELECT;
import monstertrainergame.events.AbilityEvent;
import monstertrainergame.events.EndTurnEvent;
import monstertrainergame.events.EventDispatcher;
import monstertrainergame.events.MoveEvent;
import monstertrainergame.events.StartTurnEvent;
import monstertrainergame.model.Ability;
import static monstertrainergame.model.Ability.Type.MELEE;
import monstertrainergame.model.Battle;
import monstertrainergame.model.FieldedMonster;

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

    public FieldedMonster getTarget() {
        return target;
    }

    public Vector2 getDestination() {
        return destination;
    }

    public void interact(float x, float y) {
        Interaction interaction = determineInteraction(x, y);
        performInteraction(interaction);
    }

    public Interaction determineInteraction(Vector2 location) {
        return determineInteraction(location.x, location.y);
    }

    public Interaction determineInteraction(float x, float y) {
        target = battle.getMonsterAt(x, y);

        if (target != null && target != selected && target.isOwnedByPlayer()) {
            destination.set(target.getPosition());
            return SELECT;
        } else if (canPerformSelectedAbility()) {
            destination.set(selected.getPosition());
            return ABILITY;
        } else if (canMoveToPerformSelectedAbility()) {
            // destination gets set in function called by canMoveToPerformSelectedAbility()
            return MOVE_AND_ABILITY;
        } else if (selected != null) {
            pathfinder.determineMovementDestinationTowards(selected, x, y, destination);
            return MOVE;
        } else {
            return NONE;
        }
    }

    private void performInteraction(Interaction interaction) {
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
            case MOVE_AND_ABILITY:
                performInteraction(MOVE);
                performInteraction(ABILITY);
                break;
        }
    }

    private boolean canPerformSelectedAbility() {
        return canPerformAbilityPreChecks() && !(ability.getType() == MELEE && !withinMeleeRange());
    }

    private boolean canMoveToPerformSelectedAbility() {
        return canPerformAbilityPreChecks() && ability.getType() == MELEE && canMoveWithinMeleeRange();
    }

    private boolean canPerformAbilityPreChecks() {
        return selected != null && target != null && ability != null
                && !selected.hasPerformedAbility() && !target.isOwnedByPlayer();
    }

    private boolean withinMeleeRange() {
        return withinRange(selected.getRadius() + target.getRadius());
    }

    private boolean canMoveWithinMeleeRange() {
        FieldedMonster moveEnder = pathfinder.determineMovementDestinationTowards(selected, target, destination);
        return moveEnder == target;
    }

    private boolean withinRange(float range) {
        float distance2 = selected.getPosition().dst2(target.getPosition());
        return distance2 < range * range * (1 + 1e-5f); // Accommodate for floating point errors;
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
        EventDispatcher.instance.dispatch(new MoveEvent(battle.getOpponents().get(0), new Vector2(52, 36)));

        // Fire event for new player turn
        EventDispatcher.instance.dispatch(new StartTurnEvent());
    }

    public enum Interaction {
        NONE, SELECT, MOVE, ABILITY, MOVE_AND_ABILITY
    }
}
