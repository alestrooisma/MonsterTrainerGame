package monstertrainergame.view;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class TweenEngine {
    private final Array<Action> actions = new Array<>();

    public void add(Vector3 target, float x, float y, float speed) {
        actions.add(new Vector3TweenAction(target, x, y, speed));
    }

    public void add(Vector3 target, Vector3 destination, float speed) {
        add(target, destination.x, destination.y, speed);
    }

    public void add(Callback callback, Object payload) {
        actions.add(new CallbackAction(callback, payload));
    }

    public void update(float dt) {
        if (actions.notEmpty()) {
            Action action = actions.first();
            boolean done = action.update(dt);
            if (done) {
                actions.removeValue(action, true);
            }
        }
    }

    public interface Action {
        boolean update(float dt);
    }

    public static class Vector3TweenAction implements Action {
        // Owned
        private final float speed;
        private final Vector3 destination = new Vector3();
        private final Vector3 velocity = new Vector3();
        private boolean started = false;
        // Not owned
        private final Vector3 target;

        public Vector3TweenAction(Vector3 target, float x, float y, float speed) {
            this.target = target;
            this.destination.set(x, y, target.z);
            this.speed = speed;
        }

        public boolean update(float dt) {
            if (!started) {
                velocity.set(destination).sub(target).nor().scl(speed);
                started = true;
            }

            if (Math.abs(velocity.x * dt) < Math.abs(destination.x - target.x)) {
                target.add(velocity.x * dt, velocity.y * dt, 0);
                return false;
            } else {
                target.set(destination);
                return true;
            }
        }
    }

    public static class CallbackAction implements Action {
        private final Callback callback;
        private final Object payload;

        public CallbackAction(Callback callback, Object payload) {
            this.callback = callback;
            this.payload = payload;
        }

        @Override
        public boolean update(float dt) {
            callback.callback(payload);
            return true;
        }
    }

    public interface Callback {
        void callback(Object payload);
    }
}
