package monstertrainergame.controller;

import static monstertrainergame.controller.BattleController.Interaction.NONE;
import static monstertrainergame.controller.BattleController.Interaction.SELECT;
import monstertrainergame.model.Battle;
import monstertrainergame.model.FieldedMonster;

public class BattleController {
    // Not owned
    private final Battle battle;
    private FieldedMonster selected = null;
    private FieldedMonster target = null;

    public BattleController(Battle battle) {
        this.battle = battle;
    }

    public Battle getBattle() {
        return battle;
    }

    public FieldedMonster getSelected() {
        return selected;
    }

    public void interact(float x, float y) {
        Interaction interaction = determineInteraction(x, y);
        if (interaction == SELECT) {
            selected = target;
        }
    }

    public Interaction determineInteraction(float x, float y) {
        target = battle.getPlayerMonsterAt(x, y);
        return target != null? SELECT : NONE;
    }

    public void cancel() {
        selected = null;
    }

    public enum Interaction {
        NONE, SELECT
    }
}
