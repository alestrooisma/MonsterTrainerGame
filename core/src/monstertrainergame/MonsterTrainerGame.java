package monstertrainergame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import monstertrainergame.controller.BattleController;
import monstertrainergame.controller.ModelUpdater;
import monstertrainergame.events.EventDispatcher;
import monstertrainergame.events.StartTurnEvent;
import monstertrainergame.model.Ability;
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
        EventDispatcher.create(new ModelUpdater(battle));

        // Create the view
        resources = loadResources();
        view = new View(new BattleController(battle));
        populateView(battle);

        // Set up an input event listener
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(view.getView());
        inputMultiplexer.addProcessor(new InputHandler());
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Start the first turn
        EventDispatcher.instance.dispatch(new StartTurnEvent());
    }

    private static Battle createBattle() {
        // Battlefield size
        Rectangle bounds = new Rectangle(18 / 25f, 36 / 25f, (48 * 54 - 18) / 25f, (24 * 72 - 36) / 25f);
        float cx = (48 * 54 + 18) / 50f;
        float cy = (24 * 72 + 36) / 50f;

        // Create battle object
        Battle battle = new Battle(bounds);

        // Create monster types
        MonsterType firedragon = new MonsterType("Fire Dragon", 48/25f, 5);
        MonsterType seawyrm = new MonsterType("Sea Wyrm", 30/25f, 6);
        MonsterType serpent = new MonsterType("Serpent", 28/25f, 7);
        MonsterType mudwalker = new MonsterType("Mudwalker", 24/25f, 4);
        MonsterType mudcrawler = new MonsterType("Mudcrawler", 19/25f, 3);

        // Add player monsters
        Monster monster = new Monster(firedragon, true);
        monster.getAbilities().add(new Ability("Fireball"));
        battle.add(new FieldedMonster(monster, cx - 4, cy - 4));
        battle.add(new FieldedMonster(new Monster(seawyrm, true), cx - 9, cy - 2));
        battle.add(new FieldedMonster(new Monster(serpent, true), cx - 2, cy - 8));

        // Add opponent monsters
        battle.add(new FieldedMonster(new Monster(mudwalker, false), cx + 4, cy + 1));
        battle.add(new FieldedMonster(new Monster(mudcrawler, false), cx + 3, cy + 3));
        battle.add(new FieldedMonster(new Monster(mudcrawler, false), cx + 6, cy + 0.2f));

        return battle;
    }

    private static ResourceManager loadResources() {
        ResourceManager resources = ResourceManager.create();
        resources.add("Fire Dragon", createMonsterSkin("fire-dragon.png", 94, 72, 8, 46, 140, 154));
        resources.add("Sea Wyrm", createMonsterSkin("seaserpent.png", 34, 26, 4, 2, 66, 70));
        resources.add("Serpent", createMonsterSkin("water-serpent.png", 36, 20, 6, 7, 60, 53));
        resources.add("Mudwalker", createMonsterSkin("giant-mudcrawler.png", 37, 21, 8, 7, 52, 48));
        resources.add("Mudcrawler", createMonsterSkin("mudcrawler.png", 35, 18, 16, 11, 38, 23));
        resources.add("Fireball", createProjectileSkin("fireball-n.png", 49, 63));
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

    private static Skin createMonsterSkin(String filename, float offsetX, float offsetY, float boxX, float boxY, float boxWidth, float boxHeight) {
        Texture texture = new Texture(Gdx.files.internal("images/monsters/" + filename));
        return new Skin(texture, offsetX, offsetY, new Rectangle(boxX - offsetX, boxY - offsetY, boxWidth, boxHeight));
    }

    private static Skin createProjectileSkin(String filename, float offsetX, float offsetY) {
        Texture texture = new Texture(Gdx.files.internal("images/projectiles/" + filename));
        return new Skin(texture, offsetX, offsetY);
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
