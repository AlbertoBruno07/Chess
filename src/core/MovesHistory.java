package core;

import gui.GameFrame.AsideWindow;
import gui.GameFrame.BoardPanel;

import java.util.ArrayList;

public class MovesHistory {
    private static ArrayList<Move> history;

    public MovesHistory() {
        history = new ArrayList<Move>();
    }

    public Board getMoveBoard(int id){
        if(id == -1)
            return history.getLast().getBoard();
        return history.get(id).getBoard();
    }

    public void insertAMove(Move m){
        m.makeCopyOfBoard();
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
}
