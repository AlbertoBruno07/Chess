package gui.GameFrame;

import core.*;
import engine.Engine;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    public BoardPanel bP;
    public core.Game game;

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(bP);
        setResizable(false);
        pack();
    }

    public void makeVisible(){
        AsideWindow.makeVisible();
        setVisible(true);
    }

    @Override
    public void dispose() {
        AsideWindow.getInstance().dispose();
        super.dispose();
    }

    public void makeOnlineGame(int resOfCreation, OnlineComunicationManager comunicationManager) {
        comunicationManager.setBoardPanel(bP);
        game.makeOnlineGame(resOfCreation, comunicationManager);
        if(resOfCreation == 1) bP.reverseBoard();
    }

    public void makeStokfishPlay() {
        game.makeStockfishPlay(new Engine(game.getBoard(), bP));
    }
}
