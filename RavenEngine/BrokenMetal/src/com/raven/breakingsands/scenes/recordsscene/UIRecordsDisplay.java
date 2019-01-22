package com.raven.breakingsands.scenes.recordsscene;

import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.breakingsands.scenes.mainmenuscene.MainMenuScene;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDataList;
import com.raven.engine2d.ui.*;
import com.raven.engine2d.util.math.Vector2f;
import com.raven.engine2d.worldobject.Childable;

import java.util.Comparator;
import java.util.stream.Collectors;

public class UIRecordsDisplay extends UIObject<RecordsScene, UIContainer<RecordsScene>> {

    private Vector2f position = new Vector2f();

    public UIRecordsDisplay(RecordsScene scene) {
        super(scene);

        UIImage<RecordsScene> background = new UIImage<>(
                scene,
                (int) getWidth(), (int) getHeight(),
                "sprites/records.png");
        addChild(background);

        UILabel<RecordsScene> lblRecords = new UILabel<>(getScene(), "Records", 256, 14);
        UIFont font = lblRecords.getFont();
        font.setSmall(false);
        font.setHighlight(true);
        lblRecords.setX(45);
        lblRecords.setY(236);
        lblRecords.load();
        addChild(lblRecords);

        int y = 215;

        UILabel<RecordsScene> lblRank = new UILabel<>(getScene(), "Rank", 100, 10);
        font = lblRank.getFont();
        font.setSmall(true);
        font.setHighlight(true);
        lblRank.setY(y);
        lblRank.setX(10);
        lblRank.load();
        addChild(lblRank);

        UILabel<RecordsScene> lblFloor = new UILabel<>(getScene(),  "floor", 100, 10);
        font = lblFloor.getFont();
        font.setSmall(true);
        font.setHighlight(true);
        lblFloor.setY(y);
        lblFloor.setX(100);
        lblFloor.load();
        addChild(lblFloor);

        UILabel<RecordsScene> lblDate = new UILabel<>(getScene(),  "date", 100, 10);
        font = lblDate.getFont();
        font.setSmall(true);
        font.setHighlight(true);
        lblDate.setY(y);
        lblDate.setX(190);
        lblDate.load();
        addChild(lblDate);

        GameDataList records = getScene().getGame().loadRecords();

        records = records.stream()
                .sorted(Comparator.comparingInt(a -> -a.getInteger("floor")))
                .collect(Collectors.toCollection(GameDataList::new));

        for (int i = 0; i < 20; i++) {
            y -= 10;

            UILabel<RecordsScene> lbl = new UILabel<>(getScene(), (i + 1) + ")", 100, 10);
            font = lbl.getFont();
            font.setSmall(true);
            font.setHighlight(true);
            lbl.setY(y);
            lbl.setX(10);
            lbl.load();
            addChild(lbl);

            if (records.size() > i) {
                GameData record = records.get(i);
                UILabel<RecordsScene> lbl2 = new UILabel<>(getScene(),
                        Integer.toString(record.getInteger("floor")),
                        100, 10);
                font = lbl2.getFont();
                font.setSmall(true);
                font.setHighlight(true);
                lbl2.setY(y);
                lbl2.setX(100);
                lbl2.load();
                addChild(lbl2);

                UILabel<RecordsScene> lbl4 = new UILabel<>(getScene(),
                        record.getString("date"),
                        100, 10);
                font = lbl4.getFont();
                font.setSmall(true);
                font.setHighlight(true);
                lbl4.setY(y);
                lbl4.setX(190);
                lbl4.load();
                addChild(lbl4);
            } else {
                UILabel<RecordsScene> lbl2 = new UILabel<>(getScene(),
                        " -",
                        100, 10);
                font = lbl2.getFont();
                font.setSmall(true);
                font.setHighlight(true);
                lbl2.setY(y);
                lbl2.setX(100);
                lbl2.load();
                addChild(lbl2);

                UILabel<RecordsScene> lbl4 = new UILabel<>(getScene(),
                        " -",
                        100, 10);
                font = lbl4.getFont();
                font.setSmall(true);
                font.setHighlight(true);
                lbl4.setY(y);
                lbl4.setX(190);
                lbl4.load();
                addChild(lbl4);
            }
        }

        UITextButton<RecordsScene> btnClose = new UITextButton<RecordsScene>(scene, "Menu", "sprites/button.png", "mainbutton") {
            @Override
            public void handleMouseClick() {
                scene.getGame().prepTransitionScene(new MainMenuScene(scene.getGame()));
            }
        };

        btnClose.setX(-btnClose.getWidth() / 2 + getWidth() / 2);
        btnClose.setY(-btnClose.getHeight() / 2);
        btnClose.load();
        addChild(btnClose);
    }

    @Override
    public Vector2f getPosition() {
        return position;
    }

    @Override
    public int getStyle() {
        return 0;
    }

    @Override
    public float getX() {
        return position.x;
    }

    @Override
    public void setX(float x) {
        position.x = x;
    }

    @Override
    public float getY() {
        return position.y;
    }

    @Override
    public void setY(float y) {
        position.y = y;
    }

    @Override
    public float getWidth() {
        return 256;
    }

    @Override
    public float getHeight() {
        return 256;
    }
}
