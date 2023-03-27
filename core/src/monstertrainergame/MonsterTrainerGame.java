package monstertrainergame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.Array;
import monstertrainergame.model.Battle;
import monstertrainergame.model.FieldedMonster;
import monstertrainergame.model.Monster;
import monstertrainergame.model.MonsterType;
import monstertrainergame.view.View;

public class MonsterTrainerGame extends ApplicationAdapter {
    private View view;

    @Override
    public void create() {
        // Create the model
        Battle battle = createBattle();

        // Create the view
        view = new View();

        // Set up an input event listener
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(view.getView());
        inputMultiplexer.addProcessor(new InputHandler());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private static Battle createBattle() {
        MonsterType firedragon = new MonsterType("Fire Dragon");
        MonsterType seawyrm = new MonsterType("Sea Wyrm");
        MonsterType serpent = new MonsterType("Serpent");
        MonsterType mudwalker = new MonsterType("Mudwalker");
        MonsterType mudcrawler = new MonsterType("Mudcrawler");

        Array<FieldedMonster> player = new Array<>(3);
        player.add(new FieldedMonster(new Monster(firedragon), 650, 650));
        player.add(new FieldedMonster(new Monster(seawyrm), 630, 680));
        player.add(new FieldedMonster(new Monster(serpent), 670, 610));

        Array<FieldedMonster> opponent = new Array<>(3);
        player.add(new FieldedMonster(new Monster(mudwalker), 750, 750));
        player.add(new FieldedMonster(new Monster(mudcrawler), 770, 750));
        player.add(new FieldedMonster(new Monster(mudcrawler), 750, 770));

        return new Battle(player, opponent);
    }

    @Override
    public void resize(int width, int height) {
        view.resize(width, height);
    }

    @Override
    public void render() {
        view.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        view.dispose();
    }

    private static class InputHandler extends InputAdapter {

        @Override
        public boolean keyUp(int keycode) {
            if (keycode == Keys.ESCAPE) {
                Gdx.app.exit();
                return true;
            }
            return false;
        }
    }
}
