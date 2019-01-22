package com.raven.engine2d.ui;

import com.raven.engine2d.GameEngine;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDataList;
import com.raven.engine2d.graphics2d.shader.TextShader;
import com.raven.engine2d.graphics2d.sprite.SpriteSheet;
import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.util.math.Vector2i;
import org.lwjgl.system.CallbackI;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class UITextWriter {

//    private static BufferedImage alphabetImage, alphabetSmallImage;
//
//    {
//        try {
//            alphabetImage = ImageIO.read(new File(
//                    GameProperties.getMainDirectory() + File.separator +
//                            "text" + File.separator +
//                            "alphabet.png"));
//
//            alphabetSmallImage = ImageIO.read(new File(
//                    GameProperties.getMainDirectory() + File.separator +
//                            "text" + File.separator +
//                            "alphabet_small.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private SpriteSheet alphabetImage, alphabetSmallImage;

    private UIFont font;
    private UITexture uiImage;
    private GameEngine engine;
    private String text, background;
    private UITextWriterHandler handler;

    private int x = 0; // 8
    private int y = 0; // 10
    private int lines = 0;

    public UITextWriter(GameEngine engine, Scene scene) {
        this.engine = engine;

        this.font = font;

        this.handler = handler;

        alphabetImage = engine.getSpriteSheet("sprites/alphabet.png");
        alphabetImage.load(scene);
        alphabetSmallImage = engine.getSpriteSheet("sprites/alphabet_small.png");
        alphabetSmallImage.load(scene);
    }


    public void setBackground(String src) {
        background = src;
    }

    public void setImageDest(UITexture image) {
        uiImage = image;
    }

    public void setText(String text, UIFont font) {
        this.text = text;
        this.font = font;
    }

    public void setHandler(UITextWriterHandler handler) {
        this.handler = handler;
    }

    public void clearHandler() {
        handler = null;
    }

    public void write(TextShader shader) {
        x = 0; // 8
        y = 0; // 10
        lines = 0;

        engine.getWindow().printErrors("rawr");
        shader.setWriteDestination(uiImage);

        if (background != null) {
            shader.drawImage(engine.getSpriteSheet(background));
        } else {
            shader.clear();
        }

        GameDataList alphabetLocation;
        if (font.isSmall())
            alphabetLocation = engine.getGameDatabase().getTable("text").stream()
                    .filter(d -> d.getString("name").equals("alphabet small"))
                    .findFirst()
                    .map(d -> d.getList("chars"))
                    .get();
        else
            alphabetLocation = engine.getGameDatabase().getTable("text").stream()
                    .filter(d -> d.getString("name").equals("alphabet"))
                    .findFirst()
                    .map(d -> d.getList("chars"))
                    .get();

        x = font.getX();
        y = font.getY();

        if (font.getSide() == UIFont.Side.LEFT) {

            Character lastChar = null;
            char[] chars = text.toLowerCase().toCharArray();

            for (int index = 0; index < chars.length; index++) {
                char c = chars[index];

                if (font.isWrap()) {
                    // get next word length, if total is greater than width, new line;


                    if (c == '\n') {
                        x = font.getX();
                        y += 10;

                        lines += 1;
                    } else if (lastChar != null &&
                            !(Character.isAlphabetic(lastChar) || Character.isDigit(lastChar)) &&
                            (Character.isAlphabetic(c) || Character.isDigit(c))) {

                        int next = nextWordLength(index, chars, alphabetLocation);

                        if (next + x >= uiImage.getWidth()) {

                            x = font.getX();
                            y += 10;

                            lines += 1;
                        }

                    }
                }

                writeChar(shader, c, alphabetLocation);

                lastChar = c;
            }
        } else {
            x = uiImage.getWidth() - 1;

            char[] chars = new StringBuilder(text.toLowerCase()).reverse().toString().toCharArray();

            for (int index = 0; index < chars.length; index++) {
                char c = chars[index];
                writeChar(shader, c, alphabetLocation);
            }
        }

        if (handler != null) {
            handler.onFinish(lines + 1);
        }
    }

    private int nextWordLength(int i, char[] chars, GameDataList alphabetLocation) {
        AtomicInteger wordLen = new AtomicInteger();


        while (i < chars.length && (Character.isAlphabetic(chars[i]) || Character.isDigit(chars[i]))) {

            char c = chars[i];
            alphabetLocation.stream()
                    .filter(a -> a.getString("name").equals(Character.toString(c)))
                    .findFirst()
                    .ifPresent(a -> wordLen.addAndGet(a.getInteger("width") - 1));

            i++;
        }

        return wordLen.get();
    }

    private Vector2i
            size = new Vector2i(),
            src = new Vector2i(),
            des = new Vector2i();

    private void writeChar(TextShader shader, Character c, GameDataList alphabetLocation) {
        engine.getWindow().printErrors("meow");
        switch (c) {
            case ' ':
                if (font.isSmall())
                    if (font.getSide() == UIFont.Side.RIGHT)
                        x -= 3;
                    else
                        x += 3;
                else {
                    if (font.getSide() == UIFont.Side.RIGHT)
                        x -= 6;
                    else
                        x += 6;
                }
                break;
            default:
                Optional<GameData> optionalgdChar = alphabetLocation.stream()
                        .filter(a -> a.getString("name").equals(c.toString()))
                        .findFirst();

                des.x = x;
                des.y = y;

                if (optionalgdChar.isPresent()) {
                    GameData gdChar = optionalgdChar.get();

                    int cx = gdChar.getInteger("x");
                    int cw = gdChar.getInteger("width");

                    size.x = cw;

                    if (!font.isSmall()) {

                        size.y = 14;

                        if (font.getSide() == UIFont.Side.RIGHT) {
                            x -= cw;
                        }

                        if (font.isButton()) {

                            des.x = x;
                            des.y = y;
                            src.x = cx;
                            src.y = 0;
                            shader.write(size, src, des, alphabetImage);
//                            imgGraphics.drawImage(alphabetImage,
//                                    x, y, x + cw, y + 14,
//                                    cx, 0, cx + cw, 14,
//                                    null);

                            Vector2i o = font.getButtonOffset();
                            des.x += o.x;
                            des.y += o.y;
                            src.y = 14;
                            shader.write(size, src, des, alphabetImage);

//                            imgGraphics.drawImage(alphabetImage,
//                                    x + o.x, y + o.y, x + cw + o.x, y + o.y + 14,
//                                    cx, 14, cx + cw, 28,
//                                    null);
                        } else {
                            if (font.isHighlight()) {

                                des.x = x;
                                des.y = y;
                                src.x = cx;
                                src.y = 14;
                                shader.write(size, src, des, alphabetImage);

//                                imgGraphics.drawImage(alphabetImage,
//                                        x, y, x + cw, y + 14,
//                                        cx, 14, cx + cw, 28,
//                                        null);
                            } else {

                                des.x = x;
                                des.y = y;
                                src.x = cx;
                                src.y = 0;
                                shader.write(size, src, des, alphabetImage);

//                                imgGraphics.drawImage(alphabetImage,
//                                        x, y, x + cw, y + 14,
//                                        cx, 0, cx + cw, 14,
//                                        null);
                            }
                        }

                        if (font.getSide() == UIFont.Side.LEFT) {
                            x += cw;
                        }
                    } else {

                        size.y = 8;

                        if (font.getSide() == UIFont.Side.RIGHT) {
                            x -= cw - 1;
                        }

                        if (font.isButton()) {

                            des.x = x;
                            des.y = y;
                            src.x = cx;
                            src.y = 0;
                            shader.write(size, src, des, alphabetSmallImage);

//                            imgGraphics.drawImage(alphabetSmallImage,
//                                    x, y, x + cw, y + 8,
//                                    cx, 8, cx + cw, 16,
//                                    null);

                            Vector2i o = font.getButtonOffset();
                            des.x += o.x;
                            des.y += o.y;
                            src.y = 8;
                            shader.write(size, src, des, alphabetSmallImage);

//                            imgGraphics.drawImage(alphabetSmallImage,
//                                    x + o.x, y + o.y, x + cw + o.x, y + o.y + 8,
//                                    cx, 8, cx + cw, 16,
//                                    null);
                        } else {

                            des.x = x;
                            des.y = y;
                            src.x = cx;
                            src.y = 8;
                            shader.write(size, src, des, alphabetSmallImage);
//                            imgGraphics.drawImage(alphabetSmallImage,
//                                    x, y, x + cw, y + 8,
//                                    cx, 8, cx + cw, 16,
//                                    null);
                        }

                        if (font.getSide() == UIFont.Side.LEFT) {
                            x += cw - 1;
                        }
                    }
                }
                break;
        }

    }
}
