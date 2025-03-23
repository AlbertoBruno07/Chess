package core;

import engine.Engine;
import gui.asideWindow.AsideWindow;
import gui.gameFrame.BoardPanel;
import movesHistory.MovesHistory;
import onlineComunicationManager.OnlineComunicationManager;
import scoreBoard.ScoreBoard;

import javax.swing.*;
import java.util.ArrayList;

public class Game {

    public Color turn;
    private ArrayList<Piece> blackArmy, whiteArmy;
    private Board board;
    private static Piece possibleEnPassant;
    private static int timeFromEnPassantUpdate;
    private BackgroundOverlay backgroundOverlay;
    private MovesHistory mH;
    private ScoreBoard scoreBoard;

    //For online
    private boolean isAnOnlineGame;
    private Color player;
    private OnlineComunicationManager comunicationManager;
    private boolean isYourTurn;
    private timer.Timer timer;
    private Thread timeThread;

    //Stockfish game
    private Engine stockfish;
    private boolean isStockfishPlaying;

    public static Piece getPossibleEnPassant() {
        return possibleEnPassant;
    }

    public static void setPossibleEnPassant(Piece possibleEnPassant) {
        Game.possibleEnPassant = possibleEnPassant;
        if(possibleEnPassant != null) timeFromEnPassantUpdate = 0;
    }

    public BackgroundOverlay getBackgroundOverlay() {
        return backgroundOverlay;
    }

    public Game() {
        turn = Color.WHITE;
        isAnOnlineGame = false;
        isStockfishPlaying = false;
        isYourTurn = true;
        stockfish = null;
        blackArmy = new ArrayList<>();
        whiteArmy = new ArrayList<>();
        board = new Board();
        mH = new MovesHistory(this);
        setup();
        backgroundOverlay = BackgroundOverlay.getStaticInstance(board);
        BackgroundOverlay.getStaticInstance().setKing((King)board.getPiece(0, 4), Color.BLACK);
        BackgroundOverlay.getStaticInstance().setKing((King)board.getPiece(7, 4), Color.WHITE);
        scoreBoard = new ScoreBoard();
    }

    private void createArmy(ArrayList<Piece> arrayList, Color color){
        
        int row = color == Color.BLACK ? 0 : Board.getRows()-1;

        arrayList.add(new Rook(color));
        board.getTile(row,0).setPiece(arrayList.getLast());
        arrayList.add(new Rook(color));
        board.getTile(row,7).setPiece(arrayList.getLast());

        arrayList.add(new Knight(color));
        board.getTile(row,1).setPiece(arrayList.getLast());
        arrayList.add(new Knight(color));
        board.getTile(row,6).setPiece(arrayList.getLast());

        arrayList.add(new Bishop(color));
        board.getTile(row,2).setPiece(arrayList.getLast());
        arrayList.add(new Bishop(color));
        board.getTile(row,5).setPiece(arrayList.getLast());

        arrayList.add(new King(color));
        board.getTile(row,4).setPiece(arrayList.getLast());
        arrayList.add(new Queen(color));
        board.getTile(row,3).setPiece(arrayList.getLast());

        row = color == Color.BLACK ? 1 : Board.getRows()-2;
        for(int i = 0; i < Board.getColumns(); i++){
            arrayList.add(new Pawn(color));
            board.getTile(row,i).setPiece(arrayList.getLast());
        }
    }

    private void setup(){
        createArmy(blackArmy, Color.BLACK);
        createArmy(whiteArmy, Color.WHITE);
    }

    public void clear(){
        for(int i = 0; i < Board.getRows(); i++)
            for(int j = 0; j < Board.getColumns(); j++)
                board.getTile(i, j).setPiece(null);
    }

    public Color getTurn() {
        return turn;
    }

    public Board getBoard() {
        return board;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    public void removePieceFromArmy(Piece piece){
        if(piece.color == Color.WHITE)
            whiteArmy.remove(piece);
        else
            blackArmy.remove(piece);
        scoreBoard.pieceHasBeenEaten(piece);
    }

    public void addPieceToArmy(Piece piece){
        if(piece.color == Color.WHITE)
            whiteArmy.add(piece);
        else
            blackArmy.add(piece);
    }

    public boolean isMoveSourceValid(int r, int c){
        if(board.getTile(r, c).getPiece() == null)
            return false;

        if(board.getTile(r, c).getPiece().color != turn)
            return false;

        return true;
    }

    public boolean processMove(int sR, int sC,
                               int tR, int tC,
                               BoardPanel bp){
        Move move = new Move(sR, sC, tR, tC, board);

        //Managing it as a completely separated case
        if(move.isACastlingMove()){
            if(turn == Color.BLACK)
                BackgroundOverlay.getStaticInstance().getBlackKing().updateFM();
            else
                BackgroundOverlay.getStaticInstance().getWhiteKing().updateFM();
            manageCastling(move, bp);
            switchTurn();
            mH.insertAMove(move);
            if(isAnOnlineGame && isYourTurn)
                comunicationManager.sendMove(move);
            if(isStockfishPlaying)
                stockfish.appendMove(move);
            bp.processMoveEnd();
            return false; //Do not wanna render normally, let BoardPanel believe the move is invalid
        }

        if(move.isPawnPromotionMove()){
            managePawnPromotion(move, bp);
            switchTurn();
            mH.insertAMove(move);
            if(isAnOnlineGame && isYourTurn)
                comunicationManager.sendMove(move);
            if(isStockfishPlaying)
                stockfish.appendMove(move);
            return true; //Wanna render normally
        }

        if(move.isEnPassant()){
            manageEnPassant(move, bp);
            mH.insertAMove(move);
            if(isAnOnlineGame && isYourTurn)
                comunicationManager.sendMove(move);
            if(isStockfishPlaying)
                stockfish.appendMove(move);
            bp.processMoveEnd();
            return false; //Do not wanna render normally, let BoardPanel believe the move is invalid
        }

        try{
            board.getPiece(sR, sC).validateMove(move);
            //move.wouldEndInKingCheck(backgroundOverlay);
            BackgroundOverlay.getStaticInstance().wouldEndInKingCheck(move);
            if(board.getPiece(tR, tC) != null) {
                if (board.getPiece(sR, sC).color == board.getPiece(tR, tC).color)
                    return false;
                removePieceFromArmy(board.getPiece(tR, tC));
            }
            board.getTile(tR, tC).setPiece(board.getPiece(sR, sC));
            board.getTile(sR, sC).setPiece(null);
            switchTurn();
            if(timeFromEnPassantUpdate < 2)
                timeFromEnPassantUpdate++;
            if(timeFromEnPassantUpdate > 1)
                setPossibleEnPassant(null);
            BackgroundOverlay.getStaticInstance().processMove(move);
            if(board.getPiece(tR, tC).type == PieceType.PAWN)
                ((Pawn)board.getPiece(tR, tC)).updateFM();
            mH.insertAMove(move);
            if(isAnOnlineGame && isYourTurn)
                comunicationManager.sendMove(move);
            if(isStockfishPlaying)
                stockfish.appendMove(move);
            return true;
        } catch(InvalidMoveException ime){
            System.out.println(ime);
            return false;
        }
    }

    private void manageEnPassant(Move m, BoardPanel bp) {
        try{
            BackgroundOverlay.getStaticInstance().wouldEndInKingCheck(m);
        } catch (Exception e){
            System.out.println(e);
            return;
        }
        //At this point we are sure of these coordinates
        board.getTile(m.getTargetRow(), m.getTargetColumns()).setPiece(m.getSourcePiece());
        board.getTile(m.getSourceRow(), m.getSourceColumns()).setPiece(null);
        removePieceFromArmy(board.getPiece(m.getSourceRow(), m.getTargetColumns()));
        Piece removedPiece = board.getPiece(m.getSourceRow(), m.getTargetColumns());
        board.getTile(m.getSourceRow(), m.getTargetColumns()).setPiece(null);
        bp.processEnPassant(m);
        BackgroundOverlay.getStaticInstance().processEnPassant(m, removedPiece);
        switchTurn();
    }

    private void managePawnPromotion(Move move, BoardPanel bp) {
        String promotedPieceType;

        if(isAnOnlineGame && !isYourTurn)
            promotedPieceType = comunicationManager.getPromotedPieceType();
        else if(isStockfishPlaying && !isYourTurn)
            promotedPieceType = stockfish.getPromotedPieceType();
        else{
            promotedPieceType = (String) JOptionPane.showInputDialog(null,
                        "Select promoted piece", "Pawn Promotion", JOptionPane.QUESTION_MESSAGE, null,
                        new String[]{"Bishop", "Rook", "Knight", "Queen"}, "Bishop");
            if(promotedPieceType == null)
                promotedPieceType = "Pawn";
        }
        Piece nP =
                switch (promotedPieceType){
                    case "Bishop" -> new Bishop(move.getSourcePiece().getColor());
                    case "Rook" -> new Rook(move.getSourcePiece().getColor());
                    case "Knight" -> new Knight(move.getSourcePiece().getColor());
                    case "Queen" -> new Queen(move.getSourcePiece().getColor());
                    case "Pawn" -> move.sourcePiece;
                    default -> throw new IllegalStateException("Unexpected value: " + promotedPieceType);
                };
        move.setPromotedPieceType(promotedPieceType);
        board.getTile(move.getTargetRow(), move.getTargetColumns()).setPiece(nP);
        board.getTile(move.getSourceRow(), move.getSourceColumns()).setPiece(null);
        removePieceFromArmy(move.sourcePiece);
        addPieceToArmy(nP);
        BackgroundOverlay.getStaticInstance().processPawnPromotion(move, nP);
    }

    public void manageCastling(Move m, BoardPanel bp){
        Piece rook = m.getSourcePiece(), king = m.getTargetPiece();
        if(rook.type == PieceType.KING){
            Piece a = rook;
            rook = king;
            king = a;
        }

        int expectedKingColumn = ( (king.getPosC() + rook.getPosC()) / 2 ) + (king.getPosC() < rook.getPosC() ? 1 : -1 );
        int expectedRookColumn = expectedKingColumn + (king.getPosC() < rook.getPosC() ? -1 : 1 );

        bp.processCastling(rook.getPosR(), king.getPosC(), expectedKingColumn, rook.getPosC(), expectedRookColumn);

        Move mR = new Move(rook.getPosR(), rook.getPosC(), rook.getPosR(), expectedRookColumn, board);
        Move mK = new Move(king.getPosR(), king.getPosC(), king.getPosR(), expectedKingColumn, board);

        board.getTile(m.getSourceRow(), expectedKingColumn).setPiece(king);
        board.getTile(m.getSourceRow(), expectedRookColumn).setPiece(rook);
        board.getTile(m.getSourceRow(), m.getSourceColumns()).setPiece(null);
        board.getTile(m.getTargetRow(), m.getTargetColumns()).setPiece(null);

        BackgroundOverlay.getStaticInstance().processMove(mR);
        BackgroundOverlay.getStaticInstance().processMove(mK);
    }

    private void switchTurn() {
        turn = (turn == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public MovesHistory getMovesHistory() {
        return mH;
    }

    public void makeOnlineGame(int color, OnlineComunicationManager comunicationManager) {
        isAnOnlineGame = true;
        player = color == 0 ? Color.WHITE : Color.BLACK;
        this.comunicationManager = comunicationManager;
        comunicationManager.setBoard(board);
        isYourTurn = (color == 0);
        makeTimer();
        AsideWindow.makeTime(timer);
        timeThread = new Thread(() -> timer.tick(Color.WHITE));
        timeThread.start();
        if(color == 1)
            new Thread(comunicationManager).start();
    }

    public boolean isYourTurn() {
        return isYourTurn;
    }

    public void makeTimer(){
        timer = new timer.Timer(comunicationManager.getTime());
    }

    private Thread t;

    public void setYourTurn(boolean yourTurn, BoardPanel bP) {
        isYourTurn = yourTurn;
        if(isAnOnlineGame) {
            if(timeThread != null)
                timeThread.interrupt();
            timeThread = new Thread(() -> timer.tick(turn));
            if(!yourTurn)
                sendTimer();
            else
                timer.updateTime(turn == Color.WHITE ? Color.BLACK : Color.WHITE, comunicationManager.getTime());
            timeThread.start();

            if(!yourTurn) {
                if (t != null)
                    t.interrupt();
                t = new Thread(comunicationManager);
                t.start();
            }
        }
        if(!yourTurn && isStockfishPlaying){
            manageStockfishMove();
        }
        if(yourTurn && (isStockfishPlaying || isAnOnlineGame)){
            bP.movePreview();
        }
    }

    private void manageStockfishMove() {
        new Thread(() -> stockfish.makeMove()).start();
    }

    public Color getPlayer() {
        return player;
    }

    public boolean isAnOnlineGame() {
        return isAnOnlineGame;
    }

    public void makeStockfishPlay(Engine stockfish) {
        isStockfishPlaying = true;
        player = Color.BLACK;
        this.stockfish = stockfish;
        setYourTurn(false, null); //Managing first move ; bP will not be necessary
    }

    public boolean isStockfishPlaying() {
        return isStockfishPlaying;
    }

    public OnlineComunicationManager getComunicationManager() {
        return comunicationManager;
    }

    public Thread getTimeThread() {
        return timeThread;
    }

    public void sendTimer(){
        comunicationManager.sendTime(timer.getTime(turn == Color.WHITE ? Color.BLACK : Color.WHITE));
    }
}