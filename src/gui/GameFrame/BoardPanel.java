package gui.GameFrame;

import Settings.Settings;
import core.*;
import core.Color;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class BoardPanel extends JPanel {
    static final int TILE_DIMENSION = 100;

    private JPanel main;
    private JPanel[][] tiles;
    private Game game;
    private GameDynamicsListener listener;
    private int sourceRow, sourceColumn;
    private boolean moveIsOnGoing;
    private ArrayList<Tile> possibleMoves;

    //Using a class to make easier the detection fo the right component
    private class circle extends JPanel{
        java.awt.Color color;
        public circle(java.awt.Color color) {
            super.setOpaque(false);
            super.setSize(TILE_DIMENSION, TILE_DIMENSION);
            this.color = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(color);
            g.fillOval(TILE_DIMENSION/4, TILE_DIMENSION/4, TILE_DIMENSION-50, TILE_DIMENSION-50);
        }
    }


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
        tiles[r][c].updateUI();
    }

    private String makeIconName(PieceType pT, Color c){
        return "icons/" + Settings.getSelectedIconPackage() + "/" + (c == Color.WHITE ? "W" : "B") + letterForPiece(pT) + ".png";
    }

    public void drawPiece(int r, int c, PieceType pT, Color color){
        String iconName = makeIconName(pT, color);
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
                movePreview(r, c, true);
                moveIsOnGoing = true;
                highlightSourceTile(r, c);
                sourceRow = r;
                sourceColumn = c;
            }
        } else{
            moveIsOnGoing = false;
            flushMovePreview();
            processMove(sourceRow, sourceColumn, r, c);
            unhighlightSourceTile(sourceRow, sourceColumn);
            kingIsInCheck(game.getTurn());
            if(BackgroundOverlay.checkMate(game.getTurn()))
                drawCheckMate(game.getTurn());
            Color inverseTurn = game.turn == Color.WHITE ? Color.BLACK : Color.WHITE;
            tiles[BackgroundOverlay.getKingR(inverseTurn)][BackgroundOverlay.getKingC(inverseTurn)]
                    .setBackground(determineTileColor(BackgroundOverlay.getKingR(inverseTurn),
                                    BackgroundOverlay.getKingC(inverseTurn)));
            //The king position could have been changed, but this is not a problem since
            //that means it has been moved, so unhighlightSourceTile will clear that tile automatically
        }
    }

    private void drawCheckMate(Color turn) {
        int r = BackgroundOverlay.getKingR(turn), c = BackgroundOverlay.getKingC(turn);
        clearPiece(r, c);

        String iconName = makeIconName(PieceType.KING, turn);
        URL url = getClass().getClassLoader().getResource(iconName);
        try {
            BufferedImage img = ImageIO.read(url);
            BufferedImage rImg = new BufferedImage(img.getHeight(), img.getWidth(), 6); //Force to use a specific type of image
            Graphics2D g2d = rImg.createGraphics();
            g2d.rotate(Math.PI /2, img.getHeight()/2.0f, img.getWidth()/2.0f);
            g2d.drawImage(img, null, 0, 0);
            g2d.dispose();
            JLabel label = new JLabel(new ImageIcon(rImg.getScaledInstance(TILE_DIMENSION, TILE_DIMENSION, Image.SCALE_SMOOTH)));
            tiles[r][c].removeAll();
            tiles[r][c].add(label);
            tiles[r][c].updateUI();
        } catch (IOException IOe){System.out.println("Cannot read icon!!");}
    }

    private void processMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        boolean isEating = game.getBoard().getPiece(targetRow, targetColumn) != null;
        if(game.processMove(sourceRow, sourceColumn, targetRow, targetColumn, this)){
            Piece tP = game.getBoard().getPiece(targetRow, targetColumn);
            clearPiece(sourceRow, sourceColumn);
            drawPiece(targetRow, targetColumn, tP.getType(), tP.getColor());
            if(isEating)
                PlaySound.playCapture();
            else
                PlaySound.playMove();
        }
    }

    public void processCastling(int r, int kSC, int kTC, int rSC, int rTC){
        clearPiece(r, kSC);
        clearPiece(r, rSC);
        drawPiece(r, kTC, PieceType.KING, (r == 7 ? Color.WHITE : Color.BLACK));
        drawPiece(r, rTC, PieceType.ROOK, (r == 7 ? Color.WHITE : Color.BLACK));
        PlaySound.playMove();
    }

    public void kingIsInCheck(Color c){
        if(BackgroundOverlay.isKingInCheck(c))
            tiles[BackgroundOverlay.getKingR(c)][BackgroundOverlay.getKingC(c)].setBackground(java.awt.Color.RED);
    }

    public void processEnPassant(Move m) {
        clearPiece(m.getSourceRow(), m.getSourceColumns());
        clearPiece(m.getSourceRow(), m.getTargetColumns());
        drawPiece(m.getTargetRow(), m.getTargetColumns(), m.getSourcePiece().getType(), m.getSourcePiece().getColor());
        PlaySound.playCapture();
    }

    public void highlightMenacedPiece(int r, int c){
        tiles[r][c].setBackground(java.awt.Color.GRAY);
    }

    public void movePreview(int r, int c, boolean isForMove) {
        if(moveIsOnGoing)
            return;

        flushMovePreview();
        if(game.getBoard().getPiece(r,c) == null)
            return;

        if(game.getBoard().getPiece(r,c).getColor() != game.getTurn())
            return;

        possibleMoves = BackgroundOverlay.getPossibleMoves(game.getBoard().getPiece(r,c));

        for(var i : possibleMoves){
            if(game.getBoard().getPiece(i.getRow(), i.getColumn()) != null)
                highlightMenacedPiece(i.getRow(), i.getColumn());
            else
                tiles[i.getRow()][i.getColumn()].add(new circle(isForMove ? java.awt.Color.GREEN : java.awt.Color.GRAY));
            tiles[i.getRow()][i.getColumn()].updateUI();
        }
    }

    //To be called before update Board and possibleMoves
    public void flushMovePreview() {
        if(moveIsOnGoing)
            return;

        if(possibleMoves != null) {
            for (var i : possibleMoves){
                if(game.getBoard().getPiece(i.getRow(), i.getColumn()) != null) {
                    unhighlightSourceTile(i.getRow(), i.getColumn());
                    tiles[i.getRow()][i.getColumn()].updateUI();
                    continue;
                }
                for(var c : tiles[i.getRow()][i.getColumn()].getComponents())
                    if(c instanceof circle) {
                        tiles[i.getRow()][i.getColumn()].remove(c);
                        break;
                    }
                tiles[i.getRow()][i.getColumn()].updateUI();
            }
            possibleMoves = null;
        }
    }
}
