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
import static monstertrainergame.model.Ability.Type.*;

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
        MonsterType firewraith = new MonsterType("Fire Wraith", 25/25f, 5, 80);
        MonsterType seawyrm = new MonsterType("Sea Wyrm", 30/25f, 6, 100);
        MonsterType icemonax = new MonsterType("Icemonax", 35/25f, 7, 120);
        MonsterType mudwalker = new MonsterType("Mudwalker", 24/25f, 4, 100);
        MonsterType mudcrawler = new MonsterType("Mudcrawler", 19/25f, 3, 75);

        // Add player monsters
        Monster monster = new Monster(firewraith, true);
        monster.getAbilities().add(new Ability("Slash", MELEE, 20));
        monster.getAbilities().add(new Ability("Fireball", PROJECTILE, 50));
        battle.add(new FieldedMonster(monster, cx - 6, cy - 6));
        Monster monster2 = new Monster(seawyrm, true);
        monster2.getAbilities().add(new Ability("Bite", MELEE, 30));
        monster2.getAbilities().add(new Ability("Arcane Missile", PROJECTILE, 20));
        battle.add(new FieldedMonster(monster2, cx - 7, cy - 0));
        Monster monster3 = new Monster(icemonax, true);
        monster3.getAbilities().add(new Ability("Slam", MELEE, 40));
        monster3.getAbilities().add(new Ability("Tail Whip", MELEE, 25));
        battle.add(new FieldedMonster(monster3, cx - 2, cy - 8));

        // Add opponent monsters
        battle.add(new FieldedMonster(new Monster(mudwalker, false), cx + 4, cy + 1));
        battle.add(new FieldedMonster(new Monster(mudcrawler, false), cx + 3, cy + 3));
        battle.add(new FieldedMonster(new Monster(mudcrawler, false), cx + 6, cy + 0.2f));

        return battle;
    }

    private static ResourceManager loadResources() {
        ResourceManager resources = ResourceManager.create();
        resources.add("Fire Wraith", createMonsterSkin("firewraith.png", 34, 5, 9, 5, 51, 62));
        resources.add("Sea Wyrm", createMonsterSkin("seaserpent.png", 34, 22, 4, 2, 66, 70));
        resources.add("Icemonax", createMonsterSkin("great-icemonax.png", 58, 31, 0, 15, 93, 64));
        resources.add("Mudwalker", createMonsterSkin("giant-mudcrawler.png", 37, 21, 8, 7, 52, 48));
        resources.add("Mudcrawler", createMonsterSkin("mudcrawler.png", 35, 18, 16, 11, 38, 23));
        resources.add("Fireball", createProjectileSkin("fireball-n.png", 49, 63));
        resources.add("Arcane Missile", createProjectileSkin("whitemissile-n.png", 50, 50));
        return  resources;
    }

    private void populateView(Battle battle) {
        for (FieldedMonster monster : battle) {
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
