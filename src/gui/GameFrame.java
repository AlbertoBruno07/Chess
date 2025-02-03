package gui;

import core.*;
import javax.swing.*;

public class GameFrame extends JFrame {
    private BoardPanel bP;
    private core.Game game;

    public GameFrame(){
        game = new Game();
        bP = new BoardPanel(game);
        add(bP);
        setResizable(false);
        pack();
        setVisible(true);
    }
}
