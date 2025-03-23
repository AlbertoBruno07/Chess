package gui.gameFrame;

import gui.gameEndBox.GameEndBox;
import settings.Settings;
import core.*;
import core.Color;
import gui.asideWindow.AsideWindow;
import movesHistory.MovesHistory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BoardPanel extends JPanel {
    static final int TILE_DIMENSION = 100;

    private JPanel main;
    private JPanel[][] tiles;
    private Game game;
    private GameDynamicsListener listener;
    private int sourceRow, sourceColumn;
    private boolean moveIsOnGoing;
    private ArrayList<Tile> possibleMoves;
    private IconManager iconManager;
    private Color checkMateColor;
    private Board boardToBeDisplayed;
    private int checkR, checkC;
    private int lastSourceRow, lastSourceColumns;
    private GameFrame gameFrame;

    public boolean isReversed;

    //Using a class to make easier the detection of the right component
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


    public BoardPanel(Game game, IconManager iM, GameFrame gF) {
        super();
        this.gameFrame = gF;
        this.game = game;
        boardToBeDisplayed = game.getBoard();
        moveIsOnGoing = false;
        iconManager = iM;
        isReversed = false;
        checkMateColor = null;
        checkC = -1;
        checkR = -1;
        lastSourceColumns = -1;
        lastSourceRow = -1;
        long iniT = System.nanoTime();
        initializeLayout();
        System.out.println("[InitializeLayout] exT = " + (System.nanoTime() - iniT));
        iniT = System.nanoTime();
        initializeGame();
        System.out.println("[InitializeGame] exT = " + (System.nanoTime() - iniT) + "\n");
        iniT = System.nanoTime();
    }

    private void initializeLayout() {
        main = new JPanel(new GridLayout(Board.getRows(), Board.getColumns()));
        main.setBounds(0, 0, TILE_DIMENSION * Board.getRows(), TILE_DIMENSION *
                Board.getColumns());
        setPreferredSize(new Dimension(800, 800));
        add(main, BorderLayout.CENTER);
        //listener = new GameDynamicsListener(this);
        //addMouseListener(listener);
        //addMouseMotionListener(listener);
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
                tiles[i][j].addMouseListener(new GameDynamicsListener(this, i, j));
                tiles[i][j].addMouseMotionListener(new GameDynamicsListener(this, i, j));
                main.add(tiles[i][j]);
            }
        }
    }

    public void initializeGame() {
        for(int i = 0; i < Board.getRows(); i++)
            for(int j = 0; j < Board.getColumns(); j++) {
                Piece piece = boardToBeDisplayed.getPiece(i, j);
                if (piece != null)
                    drawPiece(i, j, piece.getType(),
                            piece.getColor());
            }
    }

    public java.awt.Color determineTileColor(int r, int c){
        if(isReversed){
            r = 7-r;
            c = 7-c;
        }
        return boardToBeDisplayed.getTile(r, c).getColor() == Color.WHITE ?
                Settings.getColor2() : Settings.getColor1();
    }

    public void highlightSourceTile(int r, int c){
        if(isReversed){
            r = 7-r;
            c = 7-c;
        }
        tiles[r][c].setBackground(Settings.getColor3());
    }

    public void unhighlightSourceTile(int r, int c){
        if(isReversed){
            r = 7-r;
            c = 7-c;
        }
        tiles[r][c].setBackground(determineTileColor(r, c));
    }

    public void clearPiece(int r, int c){
        if(isReversed){
            r = 7-r;
            c = 7-c;
        }
        tiles[r][c].removeAll();
        tiles[r][c].updateUI();
    }

    public void drawPiece(int r, int c, PieceType pT, Color color){
        if(isReversed){
            r = 7-r;
            c = 7-c;
        }
        tiles[r][c].removeAll();
        tiles[r][c].add(new JLabel(iconManager.getIcon(pT, color)));
        tiles[r][c].updateUI();
    }



    public void onMove(int r, int c){
        if(!game.isYourTurn())
            return;
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
            if(lastSourceRow != sourceRow || lastSourceColumns != sourceColumn)
                unhighlightSourceTile(sourceRow, sourceColumn);
            kingIsInCheck(game.getTurn());
            if(checkMate(game.getTurn()))
                drawCheckMate(game.getTurn());
            Color inverseTurn = game.turn == Color.WHITE ? Color.BLACK : Color.WHITE;
            int kingR = isReversed ? 7-BackgroundOverlay.getStaticInstance().getKingR(inverseTurn) :
                    BackgroundOverlay.getStaticInstance().getKingR(inverseTurn),
                kingC = isReversed ? 7-BackgroundOverlay.getStaticInstance().getKingC(inverseTurn) :
                        BackgroundOverlay.getStaticInstance().getKingC(inverseTurn);
            tiles[kingR][kingC].setBackground(determineTileColor(kingR, kingC));
            //The king position could have been changed, but this is not a problem since
            //that means it has been moved, so unhighlightSourceTile will clear that tile automatically
        }
    }

    private void drawCheckMate(Color turn) {
        checkMateColor = turn;
        int r = BackgroundOverlay.getStaticInstance().getKingR(turn),
                c = BackgroundOverlay.getStaticInstance().getKingC(turn);
        clearPiece(r, c);
        if(isReversed){
            r = 7-r;
            c = 7-c;
        }
        tiles[r][c].removeAll();
        tiles[r][c].add(iconManager.getCheckMateLabel(turn));
        tiles[r][c].updateUI();
    }

    private void processMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        if(!game.isYourTurn())
            return;
        boolean isEating = boardToBeDisplayed.getPiece(targetRow, targetColumn) != null;
        if(game.processMove(sourceRow, sourceColumn, targetRow, targetColumn, this)){
            Piece tP = boardToBeDisplayed.getPiece(targetRow, targetColumn);
            clearPiece(sourceRow, sourceColumn);
            drawPiece(targetRow, targetColumn, tP.getType(), tP.getColor());
            highlightSourcePiece(sourceRow, sourceColumn);
            if(isEating)
                PlaySound.playCapture();
            else
                PlaySound.playMove();
            processMoveEnd();
        }
    }

    public void processMoveEnd(){
        if(game.isAnOnlineGame() || game.isStockfishPlaying()) {
            game.setYourTurn(!game.isYourTurn(), this);
        }
        setCheckCoords(-1, -1);
    }

    //Both for online and stockfish
    public void processExternalMove(Move move){
        if(game.isYourTurn()) //Shouldn't happen, but better safe than sorry
            return;

        int sourceRow = move.getSourceRow(),
                sourceColumn = move.getSourceColumns(),
                targetRow = move.getTargetRow(),
                targetColumn = move.getTargetColumns();

        if(checkR != -1){
            unhighlightSourceTile(checkR, checkC);
            setCheckCoords(-1, -1);
        }

        boolean isEating = boardToBeDisplayed.getPiece(targetRow, targetColumn) != null;
        if(game.processMove(sourceRow, sourceColumn, targetRow, targetColumn, this)) {
            Piece tP = boardToBeDisplayed.getPiece(targetRow, targetColumn);
            clearPiece(sourceRow, sourceColumn);
            drawPiece(targetRow, targetColumn, tP.getType(), tP.getColor());
            highlightSourcePiece(sourceRow, sourceColumn);
            if (isEating)
                PlaySound.playCapture();
            else
                PlaySound.playMove();
        }

        kingIsInCheck(Color.BLACK);
        kingIsInCheck(Color.WHITE);

        if(checkMate(Color.BLACK))
            drawCheckMate(Color.BLACK);
        if(checkMate(Color.WHITE))
            drawCheckMate(Color.WHITE);

        game.setYourTurn(true, this);
    }

    private boolean checkMate(Color c){
        boolean res = BackgroundOverlay.getStaticInstance().checkMate(c);
        GameEndBox gameEndBox;
        if(res)
            gameEndBox = new GameEndBox(c, gameFrame);
        return res;
    }

    public void processCastling(int r, int kSC, int kTC, int rSC, int rTC){
        clearPiece(r, kSC);
        clearPiece(r, rSC);
        drawPiece(r, kTC, PieceType.KING, (r == 7 ? Color.WHITE : Color.BLACK));
        drawPiece(r, rTC, PieceType.ROOK, (r == 7 ? Color.WHITE : Color.BLACK));
        highlightSourcePiece(r, kSC);
        PlaySound.playMove();
    }

    private void kingIsInCheck(Color c){
        if(BackgroundOverlay.getStaticInstance().isKingInCheck(c)) {
            highlightKingCheck(BackgroundOverlay.getStaticInstance().getKingR(c),
                    BackgroundOverlay.getStaticInstance().getKingC(c));
            MovesHistory.setCheckOnLastMove(BackgroundOverlay.getStaticInstance().getKingR(c),
                    BackgroundOverlay.getStaticInstance().getKingC(c));
            setCheckCoords(BackgroundOverlay.getStaticInstance().getKingR(c),
                    BackgroundOverlay.getStaticInstance().getKingC(c));
        }
    }

    public void highlightKingCheck(int r, int c){
        if(isReversed){
            r = 7-r;
            c = 7-c;
        }
        tiles[r][c].setBackground(java.awt.Color.RED);
    }

    public void processEnPassant(Move m) {
        clearPiece(m.getSourceRow(), m.getSourceColumns());
        clearPiece(m.getSourceRow(), m.getTargetColumns());
        drawPiece(m.getTargetRow(), m.getTargetColumns(), m.getSourcePiece().getType(), m.getSourcePiece().getColor());
        highlightSourcePiece(m.getSourceRow(), m.getSourceColumns());
        PlaySound.playCapture();
    }

    public void highlightMenacedPiece(int r, int c){
        if(isReversed){
            r = 7-r;
            c = 7-c;
        }
        tiles[r][c].setBackground(java.awt.Color.GRAY);
    }

    private void highlightSourcePiece(int r, int c){
        if(lastSourceColumns != -1)
            unhighlightSourceTile(lastSourceRow, lastSourceColumns);
        lastSourceColumns = c; lastSourceRow = r;
        if(isReversed){
            r = 7-r;
            c = 7-c;
        }
        tiles[r][c].setBackground(Settings.getColor4());
    }

    public void movePreview(){
        if(GameDynamicsListener.getMouseR() != -1)
            movePreview(GameDynamicsListener.getMouseR(), GameDynamicsListener.getMouseC(), false);
    }

    public void movePreview(int r, int c, boolean isForMove) {
        if(moveIsOnGoing)
            return;

        if(isForMove) flushMovePreview();
        if(boardToBeDisplayed.getPiece(r,c) == null)
            return;

        if(boardToBeDisplayed.getPiece(r,c).getColor() != game.getTurn() || (
        boardToBeDisplayed.getPiece(r, c).getColor() != game.getPlayer() && (game.isAnOnlineGame() || game.isStockfishPlaying())))
            return;

        possibleMoves = BackgroundOverlay.getStaticInstance().getPossibleMoves(boardToBeDisplayed.getPiece(r,c));

        for(var i : possibleMoves){
            int pMR = isReversed ?  7-i.getRow() : i.getRow(),
                    pMC = isReversed ? 7-i.getColumn() : i.getColumn();
            if(boardToBeDisplayed.getPiece(i.getRow(), i.getColumn()) != null)
                highlightMenacedPiece(i.getRow(), i.getColumn());
            else
                tiles[pMR][pMC].add(new circle(isForMove ? Settings.getColor3() : java.awt.Color.GRAY));
            tiles[pMR][pMC].updateUI();
        }
    }

    //To be called before update Board and possibleMoves
    public void flushMovePreview() {
        if(moveIsOnGoing)
            return;

        if(possibleMoves != null) {
            for (var i : possibleMoves){
                int pMR = isReversed ?  7-i.getRow() : i.getRow(),
                        pMC = isReversed ? 7-i.getColumn() : i.getColumn();
                if(boardToBeDisplayed.getPiece(i.getRow(), i.getColumn()) != null) {
                    unhighlightSourceTile(i.getRow(), i.getColumn());
                    tiles[pMR][pMC].updateUI();
                    continue;
                }
                for(var c : tiles[pMR][pMC].getComponents())
                    if(c instanceof circle) {
                        tiles[pMR][pMC].remove(c);
                        //Theoretically we could break, but sometimes,
                        //if the pc is really slow it happens it looses an event
                    }
                tiles[pMR][pMC].updateUI();
            }
            possibleMoves = null;
        }
    }

    public void reverseBoard(){
        isReversed = !isReversed;

        for(int i = 0; i < Board.getRows(); i++)
            for(int j = 0; j < Board.getColumns(); j++){
                clearPiece(i, j);
                unhighlightSourceTile(i, j);
            }

        if(lastSourceRow != -1)
            highlightSourcePiece(lastSourceRow, lastSourceColumns);
        initializeGame();

        if(checkR != -1)
            highlightKingCheck(checkR, checkC);
        if(checkMateColor != null && !AsideWindow.isOnPreviewsBoard)
            drawCheckMate(checkMateColor);
    }

    public void setCheckCoords(int r, int c){
        checkC = c;
        checkR = r;
    }

    public void setLastSourceCoords(int r, int c){
        lastSourceRow = r;
        lastSourceColumns = c;
    }

    public void displayBoard(Board board) {
        boardToBeDisplayed = board;
        for(int i = 0; i < Board.getRows(); i++)
            for(int j = 0; j< Board.getColumns(); j++){
                clearPiece(i, j);
                unhighlightSourceTile(i, j);
                Piece p = board.getPiece(i, j);
                if(p != null)
                    drawPiece(i, j, p.getType(), p.getColor());
            }
        if(lastSourceRow != -1)
            highlightSourcePiece(lastSourceRow, lastSourceColumns);
        if(checkC != -1)
            highlightKingCheck(checkR, checkC);
        if(checkMateColor != null && !AsideWindow.isOnPreviewsBoard)
            drawCheckMate(checkMateColor);
    }
}
