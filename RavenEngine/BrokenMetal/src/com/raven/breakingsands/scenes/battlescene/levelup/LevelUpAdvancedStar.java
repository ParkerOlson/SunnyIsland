package com.raven.breakingsands.scenes.battlescene.levelup;

import com.raven.breakingsands.character.Ability;
import com.raven.breakingsands.scenes.battlescene.pawn.Pawn;
import com.raven.engine2d.database.GameDatabase;
import com.raven.engine2d.util.math.Vector2f;

import java.util.*;
import java.util.stream.Collectors;

public class LevelUpAdvancedStar extends LevelUpStar {

    private Vector2f pos = new Vector2f();

    private LevelUpHexButton startButton,
            abilityButton1, abilityButton2,
            abilityButton3, abilityButton4,
            abilityButton5, abilityButton6, abilityButton7;

    private List<LevelUpHexButton> abilityButtonList = new ArrayList<>();

    private Pawn pawn;

    public LevelUpAdvancedStar(UILevelUp uiLevelUp) {
        super(uiLevelUp.getScene());

        startButton = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.CLASS);
        startButton.setY(-20);
        addChild(startButton);
        startButton.setDisable(false);
        startButton.setActive(true);

        // abilities
        abilityButton1 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        abilityButton1.setY(-10);
        abilityButton1.setX(-15);
        addChild(abilityButton1);
        abilityButtonList.add(abilityButton1);

        abilityButton2 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        addChild(abilityButton2);
        abilityButtonList.add(abilityButton2);

        abilityButton3 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        abilityButton3.setY(-10);
        abilityButton3.setX(15);
        addChild(abilityButton3);
        abilityButtonList.add(abilityButton3);

        abilityButton4 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        abilityButton4.setY(10);
        abilityButton4.setX(-15);
        addChild(abilityButton4);
        abilityButtonList.add(abilityButton4);

        abilityButton5 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        abilityButton5.setY(20);
        addChild(abilityButton5);
        abilityButtonList.add(abilityButton5);

        abilityButton6 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        abilityButton6.setY(10);
        abilityButton6.setX(15);
        addChild(abilityButton6);
        abilityButtonList.add(abilityButton6);

        abilityButton7 = new LevelUpHexButton(uiLevelUp, LevelUpHexButton.Type.ABILITY);
        abilityButton7.setY(40);
        addChild(abilityButton7);
        abilityButtonList.add(abilityButton7);

        addChild(new LevelUpHexConnection(getScene(), startButton, abilityButton1));
        addChild(new LevelUpHexConnection(getScene(), startButton, abilityButton2));
        addChild(new LevelUpHexConnection(getScene(), startButton, abilityButton3));
        addChild(new LevelUpHexConnection(getScene(), abilityButton1, abilityButton4));
        addChild(new LevelUpHexConnection(getScene(), abilityButton2, abilityButton5));
        addChild(new LevelUpHexConnection(getScene(), abilityButton3, abilityButton6));
        addChild(new LevelUpHexConnection(getScene(), abilityButton2, abilityButton1));
        addChild(new LevelUpHexConnection(getScene(), abilityButton2, abilityButton3));
        addChild(new LevelUpHexConnection(getScene(), abilityButton2, abilityButton4));
        addChild(new LevelUpHexConnection(getScene(), abilityButton2, abilityButton6));
        addChild(new LevelUpHexConnection(getScene(), abilityButton5, abilityButton4));
        addChild(new LevelUpHexConnection(getScene(), abilityButton5, abilityButton6));
        addChild(new LevelUpHexConnection(getScene(), abilityButton5, abilityButton7));
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

        startButton.setLocked(false);
        startButton.setDisable(false);
        startButton.setActive(true);
        if (pawn.getCharacterClass().equals("amateur")) {
            startButton.setDisable(true);
            startButton.setLocked(true);
            return;
        }

        Random r = new Random(pawn.getAbilityOrder());

        String startingAbilityName = GameDatabase.all("classes").stream()
                .filter(c -> c.getString("name").equals(pawn.getCharacterClass()) && c.getData("bonus").has("ability"))
                .map(c -> c.getData("bonus").getString("ability"))
                .findFirst().orElse(null);

        List<Ability> abilities = GameDatabase.all("abilities").stream()
                .filter(a -> a.has("class") && a.getString("class").equals(pawn.getCharacterClass()))
                .filter(a -> startingAbilityName == null || !a.getString("name").equals(startingAbilityName))
                .map(Ability::new)
                .collect(Collectors.toList());

        List<Ability> requiresNeeded = abilities.stream()
                .filter(a -> a.replace != null || a.requires != null)
                .collect(Collectors.toList());
        Map<Ability, Ability> needed = abilities.stream()
                .filter(a ->
                        requiresNeeded.stream()
                                .anyMatch(n ->
                                        n.replace != null && n.replace.equals(a.name) ||
                                        n.requires != null && n.requires.equals(a.name)))
                .collect(Collectors.toMap(
                        a -> a,
                        a -> requiresNeeded.stream().filter(n ->
                                n.replace != null && n.replace.equals(a.name) ||
                                n.requires != null && n.requires.equals(a.name)).findFirst().get()));

        List<Ability> remaining = new ArrayList<>(abilities);
        remaining.removeAll(requiresNeeded);
        remaining.removeAll(needed.keySet());

        List<LevelUpHexButton> neededCanGo = new ArrayList<>(Arrays.asList(
                abilityButton1,
                abilityButton3,
                abilityButton4,
                abilityButton5,
                abilityButton6));

        List<LevelUpHexButton> remainingCanGo = new ArrayList<>(abilityButtonList);

        // add needed

        List<Ability> neededKeysSorted = needed.keySet().stream().sorted(Comparator.comparing(a -> a.name)).collect(Collectors.toList());

        for (int i = 0; i < neededKeysSorted.size(); i++) {

            int index = r.nextInt(neededCanGo.size());
            Ability ar = neededKeysSorted.get(i);
            Ability an = needed.get(ar);
            LevelUpHexButton button = neededCanGo.remove(index);
            button.setAbility(ar);

            remainingCanGo.remove(button);

            if (button == abilityButton1) {
                abilityButton4.setAbility(an);
                neededCanGo.remove(abilityButton4);
                remainingCanGo.remove(abilityButton4);

            } else if (button == abilityButton3) {
                abilityButton6.setAbility(an);
                neededCanGo.remove(abilityButton6);
                remainingCanGo.remove(abilityButton6);

            } else if (button == abilityButton4) {
                abilityButton1.setAbility(an);
                neededCanGo.remove(abilityButton1);
                remainingCanGo.remove(abilityButton1);

            } else if (button == abilityButton5) {
                abilityButton7.setAbility(an);
                remainingCanGo.remove(abilityButton7);

            } else if (button == abilityButton6) {
                abilityButton3.setAbility(an);
                neededCanGo.remove(abilityButton3);
                remainingCanGo.remove(abilityButton3);

            }
        }

        for (int i = 0; i < remainingCanGo.size(); i++) {
            int index = r.nextInt(remaining.size());
            Ability a = remaining.remove(index);
            LevelUpHexButton button = remainingCanGo.get(i);
            button.setAbility(a);
        }
    }

    @Override
    public void clear() {
        startButton.clear();
        startButton.setDisable(false);
//        startButton.setActive(true);
        abilityButtonList.forEach(LevelUpHexButton::clear);
    }
}
