package com.raven.breakingsands.scenes.battlescene;

public class SelectionDetails {
    public boolean canAttack;
    public String name = "-";
    public String level = "";
    public String hp = "-";
    public String movement = "-";
    public String resistance = "-";
    public String shield = "-";
    public String weapon = "-";
    public String damage = "-";
    public String piercing = "-";
    public String range = "-";
    public String shots = "-";
    public String attacks = "-";

    public void clear() {
        canAttack = true;
        name = "-";
        level = "-";
        hp = "-";
        movement = "-";
        resistance = "-";
        shield = "-";
        weapon = "-";
        attacks = "-";
        damage = "-";
        piercing = "-";
        range = "-";
        shots = "-";
    }
}
