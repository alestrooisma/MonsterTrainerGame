package monstertrainergame.events;

public interface Event {
    void accept(EventListener listener);
}
