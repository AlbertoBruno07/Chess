package gui;

import core.*;
import javax.swing.*;

public class BoardPanel extends JPanel {
    static final int TILE_DIMENSION = 100;

    private JPanel main;
    private JPanel[][] tiles;
    private Game game;
    private GameDynamicsListener listener;
    private int sourceRow, sourceColumn;
    private boolean moveIsOnGoing;


    public BoardPanel(Game game) {
        super();
        this.game = game;
        moveIsOnGoing = false;
        initializeLayout();
        initializeGame();
    }

    private void initializeLayout() {

    }

    private void initializeGame() {

    }

    public java.awt.Color determineTileColor(int r, int c){
        return game.getBoard().getTile(r, c).getColor() == Color.WHITE ?
                java.awt.Color.WHITE : java.awt.Color.DARK_GRAY;
    }

    public void highlightSourceTile(int r, int c){
        tiles[r][c].setBackground(java.awt.Color.GREEN);
    }

    public void clearPiece(int r, int c){
        tiles[r][c].removeAll();
    }

    public void drawPiece(int r, int c, PieceType pT, Color color){

    }
}
