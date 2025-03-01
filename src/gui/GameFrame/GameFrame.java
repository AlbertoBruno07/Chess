package gui.GameFrame;

import core.*;
import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private BoardPanel bP;
    private core.Game game;

    public GameFrame(Image icon){
        game = new Game();
        bP = new BoardPanel(game);
        setTitle("JavaChess");
        setIconImage(icon);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(bP);
        setResizable(false);
        pack();
        setVisible(true);
    }
}
