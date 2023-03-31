package monstertrainergame.model.events;

public class ModelUpdater implements EventListener {

    @Override
    public void handleStartTurnEvent(StartTurnEvent event) {
    }

    @Override
    public void handleEndTurnEvent(EndTurnEvent event) {
    }

    @Override
    public void handleMoveEvent(MoveEvent event) {
        event.getMonster().setPosition(event.getDestination());
    }
}
