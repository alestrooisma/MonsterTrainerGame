package monstertrainergame.events;

public class EndTurnEvent implements Event {

    @Override
    public void accept(EventListener listener) {
        listener.handleEndTurnEvent(this);
    }
}
