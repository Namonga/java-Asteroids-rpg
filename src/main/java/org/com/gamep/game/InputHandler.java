package org.com.gamep.game;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;

public class InputHandler {
    private ArrayList<String> keyPressedList;
    private ArrayList<String> keyJustPressedList;

    public InputHandler() {
        keyPressedList = new ArrayList<>();
        keyJustPressedList = new ArrayList<>();
    }

    public void setupInputHandlers(Scene scene) {
        scene.setOnKeyPressed((KeyEvent event) -> {
            String keyName = event.getCode().toString();
            if (!keyPressedList.contains(keyName)) {
                keyPressedList.add(keyName);
                keyJustPressedList.add(keyName);
            }
        });

        scene.setOnKeyReleased((KeyEvent event) -> {
            String keyName = event.getCode().toString();
            keyPressedList.remove(keyName);
        });
    }

    public boolean isKeyPressed(String key) {
        return keyPressedList.contains(key);
    }

    public boolean isKeyJustPressed(String key) {
        return keyJustPressedList.contains(key);
    }

    public void clearJustPressed() {
        keyJustPressedList.clear();
    }

    public ArrayList<String> getKeyPressedList() {
        return keyPressedList;
    }
}
