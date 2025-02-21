package core;

import java.util.*;

import static java.lang.Math.abs;

public class BackgroundOverlay {

    private static BackgroundOverlay instance;
    private static ArrayList<ArrayList<ArrayList<Piece>>> blackPossibleMove;
    private static ArrayList<ArrayList<ArrayList<Piece>>> whitePossibleMove;
    private static Board board;
    private static King whiteKing;
    private static King blackKing;

    private BackgroundOverlay(Board board) {
        this.board = board;

        blackPossibleMove = new ArrayList<ArrayList<ArrayList<Piece>>>();
        whitePossibleMove = new ArrayList<ArrayList<ArrayList<Piece>>>();

        createArmies();
        initialize();
    }

    public static BackgroundOverlay getInstance(Board board){
        if(instance == null)
            instance = new BackgroundOverlay(board);
        return instance;
    }

    public static BackgroundOverlay getInstance(){
        return instance; //Can return null
    }

    public static void setKing(King king, Color c){
        if(c == Color.WHITE)
            whiteKing = king;
        else
            blackKing = king;
    }

    public static int getKingR(Color c){
        if(c == Color.WHITE)
            return whiteKing.getPosR();
        else
            return blackKing.getPosR();
    }

    public static int getKingC(Color c){
        if(c == Color.WHITE)
            return whiteKing.getPosC();
        else
            return blackKing.getPosC();
    }

    public static void  wouldEndInKingCheck(Move m){
        boolean res = false;
        processMove(m);

        Color movingColor = m.sourcePiece.color;
        if(movingColor == Color.WHITE){
            res = !blackPossibleMove.get(whiteKing.getPosR()).get(whiteKing.getPosC()).isEmpty();
            res |= pawnIsMenacingTile(whiteKing.getPosR(), whiteKing.getPosC(), movingColor);
        } else{
            res = !whitePossibleMove.get(blackKing.getPosR()).get(blackKing.getPosC()).isEmpty();
            res |= pawnIsMenacingTile(blackKing.getPosR(), blackKing.getPosC(), movingColor);
        }

        if(res){
            board.getTile(m.getTargetRow(), m.getTargetColumns()).setPiece(m.getTargetPiece());
            board.getTile(m.getSourceRow(), m.getSourceColumns()).setPiece(m.getSourcePiece());
        }
        unprocessMove(m);
        if(res){
            throw new InvalidMoveException("Move would end in king check");
        }
    }

    public static boolean isKingInCheck(Color kingColor){
        boolean res = false;
        if(kingColor == Color.WHITE){
            res = !blackPossibleMove.get(whiteKing.getPosR()).get(whiteKing.getPosC()).isEmpty();
            res |= pawnIsMenacingTile(whiteKing.getPosR(), whiteKing.getPosC(), kingColor);
        } else{
            res = !whitePossibleMove.get(blackKing.getPosR()).get(blackKing.getPosC()).isEmpty();
            res |= pawnIsMenacingTile(blackKing.getPosR(), blackKing.getPosC(), kingColor);
        }
        return res;
    }

    private static boolean pawnIsMenacingTile(int r, int c, Color movingColor) {
        if(movingColor == Color.WHITE){
            if(board.getPiece(r-1, c+1) != null){
                if(board.getPiece(r-1, c+1).type == PieceType.PAWN && board.getPiece(r-1, c+1).color == Color.BLACK)
                    return true;
            }
            if(board.getPiece(r-1, c-1) != null){
                if(board.getPiece(r-1, c-1).type == PieceType.PAWN && board.getPiece(r-1, c-1).color == Color.BLACK)
                    return true;
            }
        } else{
            if(board.getPiece(r+1, c+1) != null){
                if(board.getPiece(r+1, c+1).type == PieceType.PAWN && board.getPiece(r+1, c+1).color == Color.WHITE)
                    return true;
            }
            if(board.getPiece(r+1, c-1) != null) {
                if (board.getPiece(r + 1, c - 1).type == PieceType.PAWN && board.getPiece(r + 1, c - 1).color == Color.WHITE)
                    return true;
            }
        }
        return false;
    }

    private static void createArmies() {
        for(int i = 0; i < Board.getRows(); i++){
            ArrayList<ArrayList<Piece>> al = new ArrayList<ArrayList<Piece>>();
            for(int j = 0; j < Board.getColumns(); j++){
                ArrayList<Piece> alp = new ArrayList<Piece>();
                al.add(alp);
            }
            blackPossibleMove.add(al);
        }
        for(int i = 0; i < Board.getRows(); i++){
            ArrayList<ArrayList<Piece>> al = new ArrayList<ArrayList<Piece>>();
            for(int j = 0; j < Board.getColumns(); j++){
                ArrayList<Piece> alp = new ArrayList<Piece>();
                al.add(alp);
            }
            whitePossibleMove.add(al);
        }
    }

    private static void initialize(){
        for(int i = 0; i < Board.getRows(); i++){
            for(int j = 0; j < Board.getColumns(); j++){
                Piece p = board.getPiece(i, j);
                if(p != null) {
                    pieceInsertion(p, i, j);
                }
            }
        }
        return;
    }

    public static void pieceInsertion(Piece p, int r, int c){
        p.pieceInsertion(board, r, c);
    }

    public static void pieceRemotion(Piece p, int r, int c){
        for(int i = 0; i < Board.getRows(); i++)
            for(int j = 0; j < Board.getColumns(); j++)
                removePiece(p, i, j);
    }

    public static void insertPiece(Piece p, int r, int c) {
        if(p.color == Color.WHITE)
            whitePossibleMove.get(r).get(c).add(p);
        else
            blackPossibleMove.get(r).get(c).add(p);
    }

    public static void removePiece(Piece p, int r, int c){
        if(p.color == Color.WHITE)
            whitePossibleMove.get(r).get(c).remove(p);
        else
            blackPossibleMove.get(r).get(c).remove(p);
    }

    public static void processMove(Move m) {
        Piece sourcePiece = m.getSourcePiece();
        Piece targetPiece = m.getTargetPiece();

       pieceRemotion(sourcePiece, m.getSourceRow(), m.getSourceColumns());
       pieceInsertion(sourcePiece, m.getTargetRow(), m.getTargetColumns());

        if(targetPiece != null){
            pieceRemotion(targetPiece, m.getTargetRow(), m.getTargetColumns());
        }else{
            updateTrajectory(sourcePiece, m.getTargetRow(), m.getTargetColumns());
        }

        updateTrajectory(sourcePiece , m.getSourceRow(), m.getSourceColumns());
        return;
    }

    //To be executed only after a processMove(m)
    public static void unprocessMove(Move m){
        Piece sourcePiece = m.getSourcePiece();
        Piece targetPiece = m.getTargetPiece();

        pieceRemotion(sourcePiece, m.getTargetRow(), m.getTargetColumns());
        pieceInsertion(sourcePiece, m.getSourceRow(), m.getSourceColumns());

        if(targetPiece != null){
            pieceInsertion(targetPiece, m.getTargetRow(), m.getTargetColumns());
        } else{
            updateTrajectory(targetPiece, m.getTargetRow(), m.getTargetColumns());
        }
        updateTrajectory(sourcePiece, m.getSourceRow(), m.getSourceColumns());

        return;
    }

    private static void updateTrajectory(Piece ignorePiece, int r, int c) {
        ArrayList<Piece> arr = whitePossibleMove.get(r).get(c);
        for(int i = 0; i < arr.size(); i++) {
            Piece p = arr.get(0); //Getting pieces out and in we must always look at first position
            if(p != ignorePiece) {
                pieceRemotion(p, p.getPosR(), p.getPosC());
                pieceInsertion(p, p.getPosR(), p.getPosC());
            }
        }
        arr = blackPossibleMove.get(r).get(c);
        for(int i = 0; i < arr.size(); i++) {
            Piece p = arr.get(0); //Getting pieces out and in we must always look at first position
            if(p != ignorePiece) {
                pieceRemotion(p, p.getPosR(), p.getPosC());
                pieceInsertion(p, p.getPosR(), p.getPosC());
            }
        }
    }
}
