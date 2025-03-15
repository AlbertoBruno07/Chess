package core;

import gui.GameFrame.AsideWindow;
import gui.GameFrame.BoardPanel;

import java.util.ArrayList;

public class MovesHistory {
    private static ArrayList<Move> history;
    private static Board originalBoard;
    private static Game game;

    public MovesHistory(Game game) {
        this.game = game;
        originalBoard = game.getBoard();
        history = new ArrayList<Move>();
    }

    public Board getMoveBoard(int id){
        if(id == -1)
            return originalBoard;
        return history.get(id).getBoard();
    }

    public void insertAMove(Move m){
        m.makeCopyOfBoard();
        m.setScoreBlack(game.getScoreBoard().getBlackPoints());
        m.setScoreWhite(game.getScoreBoard().getWhitePoints());
        history.add(m);
        AsideWindow.addAnElement(m);
    }

    public static void setCheckOnLastMove(int r, int c){
        history.getLast().setCheckTile(r, c);
    }

    public int getMoveCheckR(int id){
        if(id == -1)
            return history.getLast().getCheckR();
        return history.get(id).getCheckR();
    }
    public int getMoveCheckC(int id){
        if(id == -1)
            return history.getLast().getCheckC();
        return history.get(id).getCheckC();
    }
    public int getMoveScoreBlack(int id){
        if(id == -1)
            return game.getScoreBoard().getBlackPoints();
        return history.get(id).getScoreBlack();
    }
    public int getMoveScoreWhite(int id){
        if(id == -1)
            return game.getScoreBoard().getWhitePoints();
        return history.get(id).getScoreWhite();
    }
}
