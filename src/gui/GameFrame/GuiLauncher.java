package gui.GameFrame;

import javax.swing.*;
import java.awt.*;

public class GuiLauncher {

    public static void main(String[] args){
        launchGui((new ImageIcon(GuiLauncher.class.getClassLoader().getResource("icons/General/CheckRain_Black.png"))).getImage());
    }

    public static void launchGui(Image icon){
        GameFrame gf = new GameFrame(icon);
    }

}
