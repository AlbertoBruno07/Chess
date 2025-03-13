package gui.GameFrame;

import core.*;
import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private BoardPanel bP;
    private core.Game game;

    public GameFrame(Image icon, IconManager iconManager){
        long iniT = System.nanoTime();
        game = new Game();
        System.out.println("[MakeGame] exT = " + (System.nanoTime() - iniT));
        iniT = System.nanoTime();
        bP = new BoardPanel(game, iconManager);
        System.out.println("[BoardPanel] exT = " + (System.nanoTime() - iniT));
        iniT = System.nanoTime();
        AsideWindow.initializeAsideWindow(bP, game, game.getMovesHistory(), iconManager, icon);

        System.out.println("[AsideWindow] exT = " + (System.nanoTime() - iniT));
        iniT = System.nanoTime();
        setTitle("JavaChess");
        setIconImage(icon);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(bP);
        setResizable(false);
        pack();
        setVisible(true);
    }

    @Override
    public void dispose() {
        AsideWindow.getInstance().dispose();
        super.dispose();
    }
}
