package gui.gameFrame;

import core.Game;

import javax.swing.*;
import java.awt.*;

public class GuiLauncher {

    public static GameFrame gameFrame;

    public static void main(String[] args){
        launchGui((new ImageIcon(GuiLauncher.class.getClassLoader().getResource("icons/General/CheckRain_Black.png"))).getImage(), new IconManager());
    }

    public static void launchGui(Image icon, IconManager iconManager){
        gameFrame = new GameFrame(icon, iconManager);
    }

}
