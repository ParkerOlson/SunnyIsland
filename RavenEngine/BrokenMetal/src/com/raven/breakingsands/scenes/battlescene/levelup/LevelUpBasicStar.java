package com.raven.breakingsands.scenes.battlescene.levelup;

import com.raven.breakingsands.character.Ability;
import com.raven.breakingsands.scenes.battlescene.pawn.Pawn;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDataList;
import com.raven.engine2d.database.GameDatabase;
import com.raven.engine2d.util.math.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LevelUpBasicStar extends LevelUpStar {

    private Vector2f pos = new Vector2f();

    private LevelUpHexButton startButton,
            abilityButton1, abilityButton2,
            abilityButton3, abilityButton4,
            abilityButton5, abilityButton6,
            abilityButton12, abilityButton7,
            abilityButton8, abilityButton9,
            abilityButton10, abilityButton11,
            classButton1, classButton2,
            classButton3, classButton4,
            classButton5;

    private List<LevelUpHexButton> abilityButtonList = new ArrayList<>();
    private List<LevelUpHexButton> classButtonList = new ArrayList<>();
    private List<LevelUpHexButton> weaponButtonList = new ArrayList<>();

    private Pawn pawn;

    public LevelUpBasicStar(UILevelUp uiLevelUp) {
        super(uiLevelUp.getScene());

        startButton = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.START);
        addChild(startButton);
        startButton.setDisable(false);
        startButton.setActive(true);

        // abilities
        abilityButton1 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        abilityButton1.setY(20);
        addChild(abilityButton1);
        weaponButtonList.add(abilityButton1);

        abilityButton2 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        abilityButton2.setY(10);
        abilityButton2.setX(15);
        addChild(abilityButton2);
        abilityButtonList.add(abilityButton2);

        abilityButton3 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        abilityButton3.setY(-10);
        abilityButton3.setX(15);
        addChild(abilityButton3);
        weaponButtonList.add(abilityButton3);

        abilityButton4 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        abilityButton4.setY(-20);
        addChild(abilityButton4);
        abilityButtonList.add(abilityButton4);

        abilityButton5 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        abilityButton5.setY(-10);
        abilityButton5.setX(-15);
        addChild(abilityButton5);
        weaponButtonList.add(abilityButton5);

        abilityButton6 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        abilityButton6.setY(10);
        abilityButton6.setX(-15);
        addChild(abilityButton6);
        abilityButtonList.add(abilityButton6);

        abilityButton7 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.WEAPON);
        abilityButton7.setY(30);
        abilityButton7.setX(15);
        addChild(abilityButton7);
        abilityButtonList.add(abilityButton7);

        abilityButton8 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.WEAPON);
        abilityButton8.setY(0);
        abilityButton8.setX(30);
        addChild(abilityButton8);
        abilityButtonList.add(abilityButton8);

        abilityButton9 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.WEAPON);
        abilityButton9.setY(-30);
        abilityButton9.setX(15);
        addChild(abilityButton9);
        abilityButtonList.add(abilityButton9);

        abilityButton10 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.WEAPON);
        abilityButton10.setY(-30);
        abilityButton10.setX(-15);
        addChild(abilityButton10);
        abilityButtonList.add(abilityButton10);

        abilityButton11 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.WEAPON);
        abilityButton11.setY(0);
        abilityButton11.setX(-30);
        addChild(abilityButton11);
        abilityButtonList.add(abilityButton11);

        abilityButton12 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.WEAPON);
        abilityButton12.setY(30);
        abilityButton12.setX(-15);
        addChild(abilityButton12);
        abilityButtonList.add(abilityButton12);

        // class
        classButton1 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.CLASS);
        classButton1.setY(20);
        classButton1.setX(30);
        addChild(classButton1);
        classButtonList.add(classButton1);

        classButton2 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.CLASS);
        classButton2.setY(-20);
        classButton2.setX(30);
        addChild(classButton2);
        classButtonList.add(classButton2);

        classButton3 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.CLASS);
        classButton3.setY(-20);
        classButton3.setX(-30);
        addChild(classButton3);
        classButtonList.add(classButton3);

        classButton4 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.CLASS);
        classButton4.setY(20);
        classButton4.setX(-30);
        addChild(classButton4);
        classButtonList.add(classButton4);

        classButton5 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.CLASS);
        classButton5.setY(40);
//        classButton5.setX(-30);
        addChild(classButton5);
        classButtonList.add(classButton5);

        // Connections
//        addChild(new LevelUpHexConnection(getScene(), startButton, abilityButton1));
        addChild(new LevelUpHexConnection(getScene(), startButton, abilityButton2));
//        addChild(new LevelUpHexConnection(getScene(), startButton, abilityButton3));
        addChild(new LevelUpHexConnection(getScene(), startButton, abilityButton4));
//        addChild(new LevelUpHexConnection(getScene(), startButton, abilityButton5));
        addChild(new LevelUpHexConnection(getScene(), startButton, abilityButton6));

        addChild(new LevelUpHexConnection(getScene(), abilityButton1, abilityButton2));
        addChild(new LevelUpHexConnection(getScene(), abilityButton2, abilityButton3));
        addChild(new LevelUpHexConnection(getScene(), abilityButton3, abilityButton4));
        addChild(new LevelUpHexConnection(getScene(), abilityButton4, abilityButton5));
        addChild(new LevelUpHexConnection(getScene(), abilityButton5, abilityButton6));
        addChild(new LevelUpHexConnection(getScene(), abilityButton6, abilityButton1));

        addChild(new LevelUpHexConnection(getScene(), abilityButton12, abilityButton6));
//        addChild(new LevelUpHexConnection(getScene(), abilityButton12, abilityButton1));
//        addChild(new LevelUpHexConnection(getScene(), abilityButton7, abilityButton1));
        addChild(new LevelUpHexConnection(getScene(), abilityButton7, abilityButton2));
        addChild(new LevelUpHexConnection(getScene(), abilityButton8, abilityButton2));
//        addChild(new LevelUpHexConnection(getScene(), abilityButton8, abilityButton3));
//        addChild(new LevelUpHexConnection(getScene(), abilityButton9, abilityButton3));
        addChild(new LevelUpHexConnection(getScene(), abilityButton9, abilityButton4));
        addChild(new LevelUpHexConnection(getScene(), abilityButton10, abilityButton4));
//        addChild(new LevelUpHexConnection(getScene(), abilityButton10, abilityButton5));
//        addChild(new LevelUpHexConnection(getScene(), abilityButton11, abilityButton5));
        addChild(new LevelUpHexConnection(getScene(), abilityButton11, abilityButton6));

        addChild(new LevelUpHexConnection(getScene(), classButton1, abilityButton7));
        addChild(new LevelUpHexConnection(getScene(), classButton1, abilityButton8));
        addChild(new LevelUpHexConnection(getScene(), classButton2, abilityButton8));
        addChild(new LevelUpHexConnection(getScene(), classButton2, abilityButton9));
        addChild(new LevelUpHexConnection(getScene(), classButton3, abilityButton10));
        addChild(new LevelUpHexConnection(getScene(), classButton3, abilityButton11));
        addChild(new LevelUpHexConnection(getScene(), classButton4, abilityButton11));
        addChild(new LevelUpHexConnection(getScene(), classButton4, abilityButton12));
        addChild(new LevelUpHexConnection(getScene(), classButton5, abilityButton12));
        addChild(new LevelUpHexConnection(getScene(), classButton5, abilityButton7));
    }

    @Override
    public Vector2f getPosition() {
        return pos;
    }

    @Override
    public int getStyle() {
        return 0;
    }

    @Override
    public float getX() {
        return pos.x;
    }

    @Override
    public void setX(float x) {
        pos.x = x - startButton.getWidth();
    }

    @Override
    public float getY() {
        return pos.y;
    }

    @Override
    public void setY(float y) {
        pos.y = y - startButton.getHeight();
    }

    @Override
    public float getWidth() {
        return 100;
    }

    @Override
    public float getHeight() {
        return 100;
    }

    public void setPawn(Pawn pawn) {
        this.pawn = pawn;

        abilityButtonList.forEach(btn -> {
            btn.setLocked(false);
            btn.setActive(false);
            btn.setDisable(true);
        });
        classButtonList.forEach(btn -> {
            btn.setLocked(false);
            btn.setActive(false);
            btn.setDisable(true);
        });
        weaponButtonList.forEach(btn -> {
            btn.setLocked(false);
            btn.setActive(false);
            btn.setDisable(true);
        });

        Random r = new Random(pawn.getAbilityOrder());

        // abilities
        List<Ability> abilities = GameDatabase.all("abilities").stream()
                .filter(a -> !a.has("class") && !a.has("weapon"))
                .map(Ability::new)
                .collect(Collectors.toList());

        for (int i = 0; i < 9; i++) {
            int index = r.nextInt(abilities.size());
            Ability a = abilities.remove(index);

            LevelUpHexButton button = abilityButtonList.get(i);
            button.setAbility(a);
        }

        // classes
        List<GameData> classes = new GameDataList(GameDatabase.all("classes"));

        for (int i = 0; i < 5; i++) {
            int index = r.nextInt(classes.size());
            GameData c = classes.remove(index);

            boolean used = getScene().getPawns().stream()
                    .filter(p -> p != pawn)
                    .anyMatch(p -> p.getCharacterClass().equals(c.getString("name")));

            LevelUpHexButton button = classButtonList.get(i);
            button.setClass(c, used);
        }

        // weapons
        List<Ability> weapon = GameDatabase.all("abilities").stream()
                .filter(a -> a.has("weapon"))
                .map(Ability::new)
                .collect(Collectors.toList());

        for (int i = 0; i < 3; i++) {
            int index = r.nextInt(weapon.size());
            Ability a = weapon.remove(index);

            LevelUpHexButton button = weaponButtonList.get(i);
            button.setAbility(a);
        }
    }

    @Override
    public void clear() {
        startButton.clear();
        startButton.setDisable(false);
        startButton.setActive(true);
        abilityButtonList.forEach(LevelUpHexButton::clear);
        classButtonList.forEach(LevelUpHexButton::clear);
    }
}