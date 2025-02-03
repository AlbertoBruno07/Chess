package gui;

import core.*;
import core.Color;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

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
        main = new JPanel(new GridLayout(Board.getRows(), Board.getColumns()));
        main.setBounds(0, 0, TILE_DIMENSION * Board.getRows(), TILE_DIMENSION *
                Board.getColumns());
        setPreferredSize(new Dimension(800, 800));
        add(main, BorderLayout.CENTER);
        listener = new GameDynamicsListener(this);
        addMouseListener(listener);
        addMouseMotionListener(listener);
        tiles = new JPanel[Board.getRows()][Board.getColumns()];

        for (int i = 0; i < Board.getRows(); i++) {
            for (int j = 0; j < Board.getColumns(); j++) {
                tiles[i][j] = new JPanel(new GridLayout(1, 1));
                tiles[i][j].setPreferredSize(new Dimension(TILE_DIMENSION,
                        TILE_DIMENSION));
                tiles[i][j].setSize(new Dimension(TILE_DIMENSION,
                        TILE_DIMENSION));
                tiles[i][j].setBackground(determineTileColor(i, j));
                tiles[i][j].setVisible(true);
                main.add(tiles[i][j]);
            }
        }
    }

    private void initializeGame() {
        for(int i = 0; i < Board.getRows(); i++)
            for(int j = 0; j < Board.getColumns(); j++) {
                Piece piece = game.getBoard().getPiece(i, j);
                if (piece != null)
                    drawPiece(i, j, piece.getType(),
                            piece.getColor());
            }
    }

    public java.awt.Color determineTileColor(int r, int c){
        return game.getBoard().getTile(r, c).getColor() == Color.WHITE ?
                java.awt.Color.WHITE : java.awt.Color.DARK_GRAY;
    }

    public void highlightSourceTile(int r, int c){
        tiles[r][c].setBackground(java.awt.Color.GREEN);
    }

    public void unhighlightSourceTile(int r, int c){
        tiles[r][c].setBackground(determineTileColor(r, c));
    }

    public void clearPiece(int r, int c){
        tiles[r][c].removeAll();
    }

    public void drawPiece(int r, int c, PieceType pT, Color color){
        String iconName = "icons/" + (color == Color.WHITE ? "W" : "B") + letterForPiece(pT) + ".png";
        System.out.println(r + " " + c + " " + iconName);
        URL url = getClass().getClassLoader().getResource(iconName);
        Image image = new ImageIcon(url).getImage();
        image = image.getScaledInstance(TILE_DIMENSION, TILE_DIMENSION,
                Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(image));
        tiles[r][c].removeAll();
        tiles[r][c].add(label);
        tiles[r][c].updateUI();
    }

    private String letterForPiece(PieceType pT) {
        return switch (pT){
            case KING -> "K";
            case PAWN -> "P";
            case ROOK -> "R";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
        };
    }

    public void onMove(int r, int c){
        if(!moveIsOnGoing){
            if(game.isMoveSourceValid(r, c)){
                moveIsOnGoing = true;
                highlightSourceTile(r, c);
                sourceRow = r;
                sourceColumn = c;
            } else{
                moveIsOnGoing = false;
                processMove(sourceRow, sourceColumn, r, c);
                unhighlightSourceTile(sourceRow, sourceColumn);
            }
        }
    }

    private void processMove(int sourceRow, int sourceColumn, int tileRow, int tileColumn) {
        if(game.processMove(sourceRow, sourceColumn, tileRow, tileColumn)){
            Piece sP = game.getBoard().getPiece(sourceRow, sourceColumn);
            clearPiece(sourceRow, sourceColumn);
            drawPiece(tileRow, tileColumn, sP.getType(), sP.getColor());
        }
    }
}
