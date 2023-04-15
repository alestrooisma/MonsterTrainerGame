package monstertrainergame.view;

import aetherdriven.view.AbstractLayer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.StringBuilder;
import monstertrainergame.controller.BattleController;
import monstertrainergame.model.Ability;
import monstertrainergame.model.FieldedMonster;

public class BattleHudLayer extends AbstractLayer {
    // Owned
    private final InputProcessor inputProcessor = new BattleHudInputProcessor();
    private final SpriteBatch batch = new SpriteBatch();
    private final TextRenderer textRenderer = new TextRenderer(batch);
    private final StringBuilder text = new StringBuilder(200);
    // Not owned
    private final BattleController controller;
    private FieldedMonster selectedMonster;
    private int selectedAbilityIndex = -1;

    public BattleHudLayer(BattleController controller) {
        this.controller = controller;
    }

    @Override
    public void update(float dt) {
        // Update for monster selection change
        if (controller.getSelectedMonster() == null) {
            selectedMonster = null;
        } else if (controller.getSelectedMonster() != selectedMonster) {
            selectedMonster = controller.getSelectedMonster();
            prepareAbilityTextBox();
        }

        // Update for ability selection change
        if (controller.getSelectedAbility() != null) {
            selectedAbilityIndex = 0;
            for (Ability ability : controller.getSelectedMonster().getAbilities()) {
                if (ability == controller.getSelectedAbility()) {
                    break;
                }
                selectedAbilityIndex++;
            }
        } else {
            selectedAbilityIndex = -1;
        }
    }

    private void prepareAbilityTextBox() {
        text.clear();
        int i = 1;
        for (Ability ability : controller.getSelectedMonster().getAbilities()) {
            if (text.length != 0) {
                text.append("\n");
            }
            text.append(i).append(" - ").append(ability.getName());
            i++;
        }
    }

    @Override
    public void render() {
        // Can't draw if no monster is selected
        if (selectedMonster == null) {
            return;
        }

        // Gray out the text when the monster has already performed an ability
        if (selectedMonster.hasPerformedAbility()) {
            textRenderer.setColor(Color.GRAY);
        } else {
            textRenderer.setColor(Color.WHITE);
        }

        // Do the actual rendering
        batch.begin();
        if (selectedAbilityIndex >= 0 && !selectedMonster.hasPerformedAbility()) {
            textRenderer.draw(">", 10, determineY(selectedAbilityIndex));
        }
        textRenderer.draw(text.toString(), 25, determineY(0));
        batch.end();
    }

    private float determineY(int row) {
        return 10 + (4 - row) * textRenderer.getFont().getLineHeight();
    }

    @Override
    public void dispose() {
        textRenderer.dispose();
        batch.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    private class BattleHudInputProcessor extends InputAdapter {

        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Keys.NUM_1:
                case Keys.NUM_2:
                case Keys.NUM_3:
                case Keys.NUM_4:
                    controller.selectAbility(selectedMonster.getAbilities().get(keycode - Keys.NUM_1));
                    return true;
                case Keys.SPACE:
                    controller.skip();
                    return true;
                default:
                    return false;
            }
        }
    }
}
