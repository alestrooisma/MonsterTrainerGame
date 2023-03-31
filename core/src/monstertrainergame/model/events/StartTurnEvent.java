package monstertrainergame.model.events;

public class StartTurnEvent implements Event {

    @Override
    public void accept(EventListener listener) {
        listener.handleStartTurnEvent(this);
    }
}
