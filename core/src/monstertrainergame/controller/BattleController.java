package monstertrainergame.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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

    public FieldedMonster getSelectedMonster() {
        return selected;
    }

    public FieldedMonster getTarget() {
        return target;
    }

    public void selectAbility(Ability ability) {
        this.ability = ability;
    }

    public Ability getSelectedAbility() {
        return ability;
    }

    public Vector2 getDestination() {
        return destination;
    }

    public void interact(float x, float y, FieldedMonster monster) {
        Interaction interaction = determineInteraction(x, y, monster);
        performInteraction(interaction);
        if (isTurnCompleted()) {
            endTurn();
        } else if (!canAct(selected)) {
            next();
        }
    }

    public Interaction determineInteraction(Vector2 location, FieldedMonster monster) {
        return determineInteraction(location.x, location.y, monster);
    }

    public Interaction determineInteraction(float x, float y, FieldedMonster monster) {
        if (monster != null && !Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
            target = monster;
        } else {
            target = battle.getMonsterAt(x, y);
        }

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
                select(target);
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

    private boolean isTurnCompleted() {
        for (FieldedMonster monster : battle.getFieldedMonsters()) {
            if (canAct(monster)) {
                return false;
            }
        }
        return true;
    }

    private boolean canAct(FieldedMonster monster) {
        return !monster.isSkipped() && !monster.hasPerformedAbility();
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
        deselect();
    }

    public void skip() {
        if (selected != null) {
            selected.skip();
            if (isTurnCompleted()) {
                endTurn();
            } else {
                next();
            }
        }
    }

    public void next() {
        int index = 0;
        if (selected != null) {
            index = battle.getFieldedMonsters().indexOf(selected, true);
            do {
                index = (index + 1) % battle.getFieldedMonsters().size;
            } while (!canAct(battle.getFieldedMonsters().get(index)));
        }
        select(index);
    }

    public void endTurn() {
        // Clear any relevant state in this controller
        deselect();

        // Fire end turn event
        EventDispatcher.instance.dispatch(new EndTurnEvent());

        //TODO execute AI turn
        EventDispatcher.instance.dispatch(new MoveEvent(battle.getOpponents().get(0), new Vector2(52, 36)));

        // Fire event for new player turn
        EventDispatcher.instance.dispatch(new StartTurnEvent());
    }

    private void select(int index) {
        select(battle.getFieldedMonsters().get(index));
    }

    private void select(FieldedMonster monster) {
        selected = monster;
        ability = null;
    }

    private void deselect() {
        selected = null;
        ability = null;
    }

    public enum Interaction {
        NONE, SELECT, MOVE, ABILITY, MOVE_AND_ABILITY
    }
}
