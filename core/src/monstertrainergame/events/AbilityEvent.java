package monstertrainergame.events;

import monstertrainergame.model.Ability;
import monstertrainergame.model.FieldedMonster;

public class AbilityEvent implements Event {
    // Not owned
    private final FieldedMonster monster;
    private final Ability ability;
    private final FieldedMonster target;

    public AbilityEvent(FieldedMonster monster, Ability ability, FieldedMonster target) {
        this.monster = monster;
        this.ability = ability;
        this.target = target;
    }

    public FieldedMonster getMonster() {
        return monster;
    }

    public Ability getAbility() {
        return ability;
    }

    public FieldedMonster getTarget() {
        return target;
    }

    @Override
    public void accept(EventListener listener) {
        listener.handleAbilityEvent(this);
    }
}
