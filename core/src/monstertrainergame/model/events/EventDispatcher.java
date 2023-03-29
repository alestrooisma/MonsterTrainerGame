package monstertrainergame.model.events;

import com.badlogic.gdx.utils.Array;

public class EventDispatcher {
    public static EventDispatcher instance = null;
    private final EventListener modelUpdater;
    private final Array<EventListener> listeners = new Array<>();

    private EventDispatcher(EventListener modelUpdater) {
        this.modelUpdater = modelUpdater;
    }

    public void register(EventListener listener) {
        listeners.add(listener);
    }

    public void dispatch(Event event) {
        event.accept(modelUpdater);
        for (EventListener listener : listeners) {
            event.accept(listener);
        }
    }

    public static void create(EventListener modelUpdater) {
        if (instance != null) {
            throw new IllegalStateException("An EventDispatcher instance already exists");
        }
        instance = new EventDispatcher(modelUpdater);
    }
}
