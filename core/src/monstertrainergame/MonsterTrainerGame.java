package monstertrainergame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import monstertrainergame.controller.BattleController;
import monstertrainergame.model.Battle;
import monstertrainergame.model.FieldedMonster;
import monstertrainergame.model.Monster;
import monstertrainergame.model.MonsterType;
import monstertrainergame.view.ResourceManager;
import monstertrainergame.view.Skin;
import monstertrainergame.view.View;

public class MonsterTrainerGame extends ApplicationAdapter {
    private ResourceManager resources;
    private View view;

    @Override
    public void create() {
        // Create the model
        Battle battle = createBattle();

        // Create the view
        resources = loadResources();
        view = new View(new BattleController(battle));
        populateView(battle);

        // Set up an input event listener
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(view.getView());
        inputMultiplexer.addProcessor(new InputHandler());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private static Battle createBattle() {
        // Battlefield size
        Rectangle bounds = new Rectangle(18, 36, 48 * 54 - 18, 24 * 72 - 36);
        float cx = (48 * 54 + 18) / 2f;
        float cy = (24 * 72 + 36) / 2f;

        MonsterType firedragon = new MonsterType("Fire Dragon", 48);
        MonsterType seawyrm = new MonsterType("Sea Wyrm", 33);
        MonsterType serpent = new MonsterType("Serpent", 30);
        MonsterType mudwalker = new MonsterType("Mudwalker", 26);
        MonsterType mudcrawler = new MonsterType("Mudcrawler", 19);

        Array<FieldedMonster> player = new Array<>(3);
        player.add(new FieldedMonster(new Monster(firedragon), cx - 100, cy - 100));
        player.add(new FieldedMonster(new Monster(seawyrm), cx - 220, cy - 50));
        player.add(new FieldedMonster(new Monster(serpent), cx - 100, cy - 150));

        Array<FieldedMonster> opponent = new Array<>(3);
        player.add(new FieldedMonster(new Monster(mudwalker), cx + 100, cy + 25));
        player.add(new FieldedMonster(new Monster(mudcrawler), cx + 80, cy + 75));
        player.add(new FieldedMonster(new Monster(mudcrawler), cx + 155, cy + 5));
        return new Battle(bounds, player, opponent);
    }

    private static ResourceManager loadResources() {
        ResourceManager resources = ResourceManager.create();
        resources.add("Fire Dragon", createSkin("fire-dragon.png", 94, 72, 8, 46, 96, 150));
        resources.add("Sea Wyrm", createSkin("seaserpent.png", 34, 26, 4, 2, 66, 70));
        resources.add("Serpent", createSkin("water-serpent.png", 36, 20, 6, 7, 60, 53));
        resources.add("Mudwalker", createSkin("giant-mudcrawler.png", 37, 21, 8, 7, 52, 48));
        resources.add("Mudcrawler", createSkin("mudcrawler.png", 35, 18, 16, 11, 38, 23));
        return  resources;
    }

    private void populateView(Battle battle) {
        for (FieldedMonster monster : battle.getFieldedMonsters()) {
            view.add(monster);
        }
        for (FieldedMonster monster : battle.getOpponents()) {
            view.add(monster);
        }
    }

    private static Skin createSkin(String filename, float offsetX, float offsetY, float boxX, float boxY, float boxWidth, float boxHeight) {
        Texture texture = new Texture(Gdx.files.internal("images/monsters/" + filename));
        return new Skin(texture, offsetX, offsetY, new Rectangle(boxX - offsetX, boxY - offsetY, boxWidth, boxHeight));
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
        resources.dispose();
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
