package gui.asideWindow;

import settings.Settings;

import javax.swing.*;
import java.awt.*;

public class MoveButton extends JButton {
    boolean state;
    int id;

    public void changeState(){
        state = !state;
    }

    public void reset(){
        state = false;
        super.setBackground(Settings.getColor1().equals(Color.DARK_GRAY) ? Color.GRAY : Settings.getColor1());
    }

    public void clicked(){
        state = true;
        super.setBackground(Settings.getColor3());
    }

    public MoveButton(String text, int id) {
        super(text);
        this.id = id;
        state = false;
    }
}
