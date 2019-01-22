package com.raven.breakingsands.scenes.battlescene.pawn;

import com.raven.breakingsands.character.Ability;
import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDatabase;
import com.raven.engine2d.database.GameDatable;
import com.raven.engine2d.scene.Scene;

import java.util.HashMap;
import java.util.Optional;

public class Hack implements GameDatable {

    private int team, hp, shield, resistance;
    private boolean instant, transferable;
    private Pawn hacker, pawn;

    public Hack(Pawn pawn, Pawn hacker, int team, Ability ability) {
        this.hacker = hacker.getHack() != null ? hacker.getHack().getHacker() : hacker;
        this.pawn = pawn;
        this.team = team;
        if (ability.hp != null)
            this.hp = ability.hp;
        if (ability.shield != null)
            this.shield = ability.shield;
        if (ability.resistance != null)
            this.resistance = ability.resistance;
        this.instant = ability.instant_hack;
        this.transferable = ability.transferable;
        if (this.transferable) initTransferable();
    }

    public Hack(BattleScene scene, Pawn pawn, GameData data) {
        if (data.has("hacker") && data.getInteger("hacker") != -1) {
            hacker = scene.getPawns().get(data.getInteger("hacker"));
        } else {
            hacker = scene.getPawns().get(0);
        }
        this.pawn = pawn;
        this.team = data.getInteger("team");
        this.hp = data.getInteger("hp");
        this.shield = data.getInteger("shield");
        this.instant = data.getBoolean("instant");
        this.transferable = data.getBoolean("transferable");
        if (this.transferable) initTransferable();
    }

    @Override
    public GameData toGameData() {
        HashMap<String, GameData> map = new HashMap<>();

        map.put("hacker", new GameData(hacker.getScene().getPawns().indexOf(hacker)));
        map.put("team", new GameData(team));
        map.put("hp", new GameData(hp));
        map.put("shield", new GameData(shield));
        map.put("instant", new GameData(instant));
        map.put("transferable", new GameData(transferable));

        return new GameData(map);
    }

    private void initTransferable() {
        Optional<GameData> o = GameDatabase.all("abilities").stream()
                .filter(c -> c.getString("name").equals("Hack"))
                .findFirst();

        o.ifPresent(gdAbility -> { // lol...
            gdAbility.asMap().put("cure", new GameData(true));
            pawn.addAbility(new Ability(gdAbility));
        });
    }

    public int getTeam() {
        return team;
    }

    public Pawn getHacker() {
        return hacker;
    }

    public boolean isInstant() {
        return instant;
    }

    public int getHP() {
        return hp;
    }

    public int getShield() {
        return shield;
    }

    public int getResistance() {
        return resistance;
    }

    public void setInstant(boolean instant) {
        this.instant = instant;
    }
}
