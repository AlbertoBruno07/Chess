package core;

import java.util.*;

import static java.lang.Math.signum;

public class BackgroundOverlay {

    private static BackgroundOverlay instance;
    private static ArrayList<ArrayList<ArrayList<Piece>>> blackPossibleMove;
    private static ArrayList<ArrayList<ArrayList<Piece>>> whitePossibleMove;
    private static Board board;
    private static King whiteKing;
    private static King blackKing;

    private BackgroundOverlay(Board board) {
        BackgroundOverlay.board = board;

        blackPossibleMove = new ArrayList<ArrayList<ArrayList<Piece>>>();
        whitePossibleMove = new ArrayList<ArrayList<ArrayList<Piece>>>();

        createArmies();
        initialize();
    }

    public static King getWhiteKing() {
        return whiteKing;
    }

    public static King getBlackKing() {
        return blackKing;
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

    public static boolean checkMate(Color color){
        ArrayList<ArrayList<ArrayList<Piece>>> enemyArmy = (color == Color.BLACK) ? whitePossibleMove : blackPossibleMove;
        Piece king = (color == Color.WHITE) ? whiteKing : blackKing;
        int r = king.getPosR(), c = king.getPosC();

        if(!isPieceMenaced(king))
            return false;

        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if(!Board.IndexOutOfRange(r+i, c+j)) {
                    if (tileIsNotOccupiedByAlly(r + i, c + j, color)) {
                        Move m = new Move(r, c, r + i, c + j, board);
                        board.getTile(r+i, c+j).setPiece(king);
                        board.getTile(r, c).setPiece(null);
                        try {
                            wouldEndInKingCheck(m);
                            board.getTile(r+i, c+j).setPiece(m.getTargetPiece());
                            board.getTile(r, c).setPiece(king);
                            return false;
                        } catch (Exception e) {}
                    }
                }

        //Means that we cannot eat the pawn but we are also attacked by another piece
        if(pawnIsMenacingTile(r, c, color))
            return false;

        //On the single tile I cannot resolve more than an attack
        if(enemyArmy.get(r).get(c).size() == 1)
            return !(attackFromPieceCanBeBlocked(enemyArmy.get(r).get(c).get(0), r, c));

        return true;
    }

    private static boolean tileIsNotOccupiedByAlly(int r, int c, Color color) {
        if(board.getPiece(r,c) == null)
            return true;
        return  board.getPiece(r,c).color != color;
    }

    private static boolean attackFromPieceCanBeBlocked(Piece piece, int r, int c) {
        if(isPieceMenaced(piece))
            return true;

        if(piece.type == PieceType.KNIGHT)
            return false;

        //Note that we can never block an attack of this type with a pawn, unless it directly eat the attacking piece
        ArrayList<ArrayList<ArrayList<Piece>>> army = (piece.color == Color.WHITE) ? blackPossibleMove : whitePossibleMove;
        int aR = piece.getPosR(), aC = piece.getPosC();
        if(aR != r) aR += signum(r - aR);
        if(aC != c) aC += signum(c - aC);
        while(aR != r || aC != c){
            if(!army.get(aR).get(aC).isEmpty())
                if(!(army.get(aR).get(aC).size() == 1 && (army.get(aR).get(aC).get(0).type == PieceType.KING)))
                    return true;
            if(aR != r) aR += signum(r - aR);
            if(aC != c) aC += signum(c - aC);
        }

        return false;
    }

    public static void  wouldEndInKingCheck(Move m){
        boolean res = false;
        processMove(m);

        Color movingColor = m.sourcePiece.color;
        res = isPieceMenaced(movingColor == Color.WHITE ? whiteKing : blackKing);

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
        return isPieceMenaced(kingColor == Color.WHITE ? whiteKing : blackKing);
    }

    public static boolean isPieceMenaced(Piece p){
        if(p.type == PieceType.PAWN)
            if(checkIfPawnIsMenacedByEnPassant(p))
                return true;

        return isTileMenaced(p.getPosR(), p.getPosC(), p.color);
    }

    private static boolean checkIfPawnIsMenacedByEnPassant(Piece p) {
        if(p.type != PieceType.PAWN)
            return false;

        if(p != Game.getPossibleEnPassant())
            return false;

        int r = p.getPosR(), c = p.getPosC();
        
        if(r == 0 || r == 7)
            return false;
        
        if(!Board.IndexOutOfRange(r, c+1))
            if((new Move(r, c+1, r+(p.color == Color.BLACK ? -1 : 1), c, board)).isEnPassant())
                return true;
        if(!Board.IndexOutOfRange(r, c-1))
            if((new Move(r, c-1, r+(p.color == Color.BLACK ? -1 : 1), c, board)).isEnPassant())
                return true;

        return false;
    }

    public static boolean isTileMenaced(int r, int c, Color color){
        boolean res = false;
        if(color == Color.WHITE){
            res = !blackPossibleMove.get(r).get(c).isEmpty();
            if(res)
                if(blackPossibleMove.get(r).get(c).size() == 1 &&
                        (blackPossibleMove.get(r).get(c).get(0).type == PieceType.PAWN ||
                                blackPossibleMove.get(r).get(c).get(0).type == PieceType.KING))
                    res = false;

            res |= pawnIsMenacingTile(r, c, color);
        } else{
            res = !whitePossibleMove.get(r).get(c).isEmpty();
            if(whitePossibleMove.get(r).get(c).size() == 1 &&
                    (whitePossibleMove.get(r).get(c).get(0).type == PieceType.PAWN ||
                            whitePossibleMove.get(r).get(c).get(0).type == PieceType.KING))
                res = false;
            res |= pawnIsMenacingTile(r, c, color);
        }
        return res;
    }

    private static boolean pawnIsMenacingTile(int r, int c, Color movingColor) {
        if(Board.IndexOutOfRange(r, c))
            return false;

        if(movingColor == Color.WHITE){
            if(!Board.IndexOutOfRange(r-1, c+1)) {
                if (board.getPiece(r - 1, c + 1) != null) {
                    if (board.getPiece(r - 1, c + 1).type == PieceType.PAWN && board.getPiece(r - 1, c + 1).color == Color.BLACK)
                        return true;
                }
            }
            if(!Board.IndexOutOfRange(r-1, c-1)) {
                if (board.getPiece(r - 1, c - 1) != null) {
                    if (board.getPiece(r - 1, c - 1).type == PieceType.PAWN && board.getPiece(r - 1, c - 1).color == Color.BLACK)
                        return true;
                }
            }
        } else{
            if(!Board.IndexOutOfRange(r+1, c+1)) {
                if (board.getPiece(r + 1, c + 1) != null) {
                    if (board.getPiece(r + 1, c + 1).type == PieceType.PAWN && board.getPiece(r + 1, c + 1).color == Color.WHITE)
                        return true;
                }
            }
            if(!Board.IndexOutOfRange(r+1, c-1)) {
                if (board.getPiece(r + 1, c - 1) != null) {
                    if (board.getPiece(r + 1, c - 1).type == PieceType.PAWN && board.getPiece(r + 1, c - 1).color == Color.WHITE)
                        return true;
                }
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

    public static void processPawnPromotion(Move m, Piece newPiece){
        processMove(m);
        pieceRemotion(m.getSourcePiece(), m.getTargetRow(), m.getTargetColumns());
        pieceInsertion(newPiece, m.getTargetRow(), m.getTargetColumns());
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

    public static void processEnPassant(Move m, Piece removedPiece) {
        pieceRemotion(m.getSourcePiece(), m.getSourceRow(), m.getSourceColumns());
        pieceRemotion(removedPiece, m.getSourceRow(), m.getTargetColumns());
        pieceInsertion(m.getSourcePiece(), m.getTargetRow(), m.getTargetColumns());
        return;
    }
}
