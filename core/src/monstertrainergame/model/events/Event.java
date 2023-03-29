package monstertrainergame.model.events;

public abstract class Event {
    public abstract void accept(EventListener listener);
}
