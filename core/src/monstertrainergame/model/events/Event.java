package monstertrainergame.model.events;

public interface Event {
    void accept(EventListener listener);
}
