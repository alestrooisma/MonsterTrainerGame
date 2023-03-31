package monstertrainergame.model.events;

public interface EventListener {
    void handleStartTurnEvent(StartTurnEvent event);
    void handleEndTurnEvent(EndTurnEvent event);
    void handleMoveEvent(MoveEvent event);
}
