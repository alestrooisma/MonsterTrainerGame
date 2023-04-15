package monstertrainergame.controller;

import monstertrainergame.events.AbilityEvent;
import monstertrainergame.events.EndTurnEvent;
import monstertrainergame.events.EventListener;
import monstertrainergame.events.MoveEvent;
import monstertrainergame.events.StartTurnEvent;
import monstertrainergame.model.Battle;
import monstertrainergame.model.FieldedMonster;

public class ModelUpdater implements EventListener {
    private final Battle battle;

    public ModelUpdater(Battle battle) {
        this.battle = battle;
    }

    @Override
    public void handleStartTurnEvent(StartTurnEvent event) {
    }

    @Override
    public void handleEndTurnEvent(EndTurnEvent event) {
        for (FieldedMonster monster : battle.getFieldedMonsters()) {
            monster.endTurn();
        }
    }

    @Override
    public void handleMoveEvent(MoveEvent event) {
        event.getMonster().moveTo(event.getDestination());
    }

    @Override
    public void handleAbilityEvent(AbilityEvent event) {
        event.getMonster().setPerformedAbility(true);
        event.getTarget().reduceHealth(event.getAbility().getDamage());
    }
}
