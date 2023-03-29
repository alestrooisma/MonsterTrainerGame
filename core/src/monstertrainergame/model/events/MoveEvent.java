package monstertrainergame.model.events;

import com.badlogic.gdx.math.Vector2;
import monstertrainergame.model.FieldedMonster;

public class MoveEvent extends Event {
    // Not owned
    private final FieldedMonster monster;
    private final Vector2 destination = new Vector2();

    public MoveEvent(FieldedMonster monster, Vector2 destination) {
        this.monster = monster;
        this.destination.set(destination);
    }

    public FieldedMonster getMonster() {
        return monster;
    }

    public Vector2 getDestination() {
        return destination;
    }

    @Override
    public void accept(EventListener listener) {
        listener.handleMoveEvent(this);
    }
}
