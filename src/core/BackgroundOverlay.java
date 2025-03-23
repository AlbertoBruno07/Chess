package core;

import java.util.*;

import static java.lang.Math.signum;

public class BackgroundOverlay {

    private static BackgroundOverlay instance;
    private ArrayList<ArrayList<ArrayList<Piece>>> blackPossibleMove;
    private ArrayList<ArrayList<ArrayList<Piece>>> whitePossibleMove;
    private Board board;
    private King whiteKing;
    private King blackKing;

    private BackgroundOverlay(Board board) {
        this.board = board;

        blackPossibleMove = new ArrayList<ArrayList<ArrayList<Piece>>>();
        whitePossibleMove = new ArrayList<ArrayList<ArrayList<Piece>>>();
    }

    public BackgroundOverlay makeInstance(Board board, King whiteKing, King blackKing){
        BackgroundOverlay newInstance = new BackgroundOverlay(board);
        newInstance.setKing(whiteKing, Color.WHITE);
        newInstance.setKing(blackKing, Color.BLACK);
        return newInstance;
    }

    private void initialize(){
        createArmies();
        initializeArmies();
    }

    public King getWhiteKing() {
        return whiteKing;
    }

    public King getBlackKing() {
        return blackKing;
    }

    public static BackgroundOverlay getStaticInstance(Board board){
        instance = new BackgroundOverlay(board);
        instance.initialize();
        return instance;
    }

    public static BackgroundOverlay getStaticInstance(){
        return instance; //Can return null
    }

    public void setKing(King king, Color c){
        if(c == Color.WHITE)
            whiteKing = king;
        else
            blackKing = king;
    }

    public int getKingR(Color c){
        if(c == Color.WHITE)
            return whiteKing.getPosR();
        else
            return blackKing.getPosR();
    }

    public int getKingC(Color c){
        if(c == Color.WHITE)
            return whiteKing.getPosC();
        else
            return blackKing.getPosC();
    }

    public boolean checkMate(Color color){
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
                        try {
                            wouldEndInKingCheck(m);
                            return false;
                        } catch (Exception e) {
                        }
                    }
                }

        //Check pawn
        if(pawnIsMenacingTile(r, c, color) && enemyArmy.get(r).get(c).isEmpty())
            return !(attackFromPieceCanBeBlocked(getPawnMenacingTile(r, c, color), r, c));

        //On the single tile I cannot resolve more than an attack
        int notToBeCounted = 0;
        for(Piece i : enemyArmy.get(r).get(c))
            if(i.type == PieceType.KING || i.type == PieceType.PAWN)
                notToBeCounted++;
        if(enemyArmy.get(r).get(c).size()-notToBeCounted == 1 && !pawnIsMenacingTile(r, c, color))
            return !(attackFromPieceCanBeBlocked(enemyArmy.get(r).get(c).get(0), r, c));

        return true;
    }

    private Piece getPawnMenacingTile(int r, int c, Color color) {
        if(Board.IndexOutOfRange(r, c))
            return null;

        if(color == Color.WHITE){
            if(!Board.IndexOutOfRange(r-1, c+1)) {
                if (board.getPiece(r - 1, c + 1) != null) {
                    if (board.getPiece(r - 1, c + 1).type == PieceType.PAWN && board.getPiece(r - 1, c + 1).color == Color.BLACK)
                        return board.getPiece(r - 1, c + 1);
                }
            }
            if(!Board.IndexOutOfRange(r-1, c-1)) {
                if (board.getPiece(r - 1, c - 1) != null) {
                    if (board.getPiece(r - 1, c - 1).type == PieceType.PAWN && board.getPiece(r - 1, c - 1).color == Color.BLACK)
                        return board.getPiece(r - 1, c - 1);
                }
            }
        } else{
            if(!Board.IndexOutOfRange(r+1, c+1)) {
                if (board.getPiece(r + 1, c + 1) != null) {
                    if (board.getPiece(r + 1, c + 1).type == PieceType.PAWN && board.getPiece(r + 1, c + 1).color == Color.WHITE)
                        return board.getPiece(r + 1, c + 1);
                }
            }
            if(!Board.IndexOutOfRange(r+1, c-1)) {
                if (board.getPiece(r + 1, c - 1) != null) {
                    if (board.getPiece(r + 1, c - 1).type == PieceType.PAWN && board.getPiece(r + 1, c - 1).color == Color.WHITE)
                        return board.getPiece(r + 1, c - 1);
                }
            }
        }

        return null;
    }

    private boolean tileIsNotOccupiedByAlly(int r, int c, Color color) {
        if(board.getPiece(r,c) == null)
            return true;
        return  board.getPiece(r,c).color != color;
    }

    private boolean attackFromPieceCanBeBlocked(Piece piece, int r, int c) {
        ArrayList<ArrayList<ArrayList<Piece>>> army = (piece.color == Color.WHITE) ? blackPossibleMove : whitePossibleMove;

        //We do not wanna count pawns and king for eating a piece
        int notToBeCounted = 0;
        for(Piece i : army.get(piece.getPosR()).get(piece.getPosC()))
            if(i.type == PieceType.KING || i.type == PieceType.PAWN)
                notToBeCounted++;

        if(piece.getType() == PieceType.PAWN){
            if(checkIfPawnIsMenacedByEnPassant(piece))
                return true;
            return army.get(piece.getPosR()).get(piece.getPosC()).size()-notToBeCounted > 0;
        }

        if(isPieceMenaced(piece)) {
            if (pawnIsMenacingTile(piece.getPosR(), piece.getPosC(), piece.color))
                return true;
            if (army.get(piece.getPosR()).get(piece.getPosC()).size() - notToBeCounted > 0)
                return true;
            else
                return false;
        }

        if(piece.type == PieceType.KNIGHT)
            return false;

        int aR = piece.getPosR(), aC = piece.getPosC();
        if(aR != r) aR += signum(r - aR);
        if(aC != c) aC += signum(c - aC);
        while(aR != r || aC != c){
            if(!army.get(aR).get(aC).isEmpty()) {
                if(army.get(aR).get(aC).size() == 1)
                    if(army.get(aR).get(aC).get(0).type != PieceType.KING)
                        return true;
                if(army.get(aR).get(aC).size() > 1)
                    return true;
            }
            if(aR != r) aR += signum(r - aR);
            if(aC != c) aC += signum(c - aC);
        }

        return false;
    }

    //Should be an isolated simulation of a move
    public void  wouldEndInKingCheck(Move m){
        boolean res = false;

        //Just create a fake move
        if(m.isEnPassant())
            m = new Move(m.getSourceRow(), m.getSourceColumns(), m.getSourceRow(), m.getTargetColumns(), board);

        board.getTile(m.getTargetRow(), m.getTargetColumns()).setPiece(m.getSourcePiece());
        board.getTile(m.getSourceRow(), m.getSourceColumns()).setPiece(null);

        processMove(m);

        Color movingColor = m.sourcePiece.color;
        res = isPieceMenaced(movingColor == Color.WHITE ? whiteKing : blackKing);

        board.getTile(m.getTargetRow(), m.getTargetColumns()).setPiece(m.getTargetPiece());
        board.getTile(m.getSourceRow(), m.getSourceColumns()).setPiece(m.getSourcePiece());

        unprocessMove(m);
        if(res){
            throw new InvalidMoveException("Move would end in king check");
        }
    }

    public boolean isKingInCheck(Color kingColor){
        return isPieceMenaced(kingColor == Color.WHITE ? whiteKing : blackKing);
    }

    public boolean isPieceMenaced(Piece p){
        if(p.type == PieceType.PAWN)
            if(checkIfPawnIsMenacedByEnPassant(p))
                return true;

        return isTileMenaced(p.getPosR(), p.getPosC(), p.color);
    }

    private boolean checkIfPawnIsMenacedByEnPassant(Piece p) {
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

    //If the king is menacing a tile we should check it can really go there, but for what is used it's ok
    public boolean isTileMenaced(int r, int c, Color color){
        boolean res = false;
        if(color == Color.WHITE){
            res = !blackPossibleMove.get(r).get(c).isEmpty();
            if(res)
                if(blackPossibleMove.get(r).get(c).size() == 1 &&
                        (blackPossibleMove.get(r).get(c).get(0).type == PieceType.PAWN))
                    res = false;

            res |= pawnIsMenacingTile(r, c, color);
        } else{
            res = !whitePossibleMove.get(r).get(c).isEmpty();
            if(whitePossibleMove.get(r).get(c).size() == 1 &&
                    (whitePossibleMove.get(r).get(c).get(0).type == PieceType.PAWN))
                res = false;
            res |= pawnIsMenacingTile(r, c, color);
        }
        return res;
    }

    private boolean pawnIsMenacingTile(int r, int c, Color movingColor) {
        return getPawnMenacingTile(r, c, movingColor) != null;
    }

    private void createArmies() {
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

    private void initializeArmies(){
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

    public void pieceInsertion(Piece p, int r, int c){
        p.pieceInsertion(board, r, c);
    }

    public void pieceRemotion(Piece p, int r, int c){
        for(int i = 0; i < Board.getRows(); i++)
            for(int j = 0; j < Board.getColumns(); j++)
                removePiece(p, i, j);
    }

    public void insertPieceNotStatic(Piece p, int r, int c) {
        if(p.color == Color.WHITE)
            whitePossibleMove.get(r).get(c).add(p);
        else
            blackPossibleMove.get(r).get(c).add(p);
    }

    public static void insertPiece(Piece p, int r, int c){
        getStaticInstance().insertPieceNotStatic(p, r, c);
    }

    public void removePiece(Piece p, int r, int c){
        if(p.color == Color.WHITE)
            whitePossibleMove.get(r).get(c).remove(p);
        else
            blackPossibleMove.get(r).get(c).remove(p);
    }

    public void processMove(Move m) {
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

    public void processPawnPromotion(Move m, Piece newPiece){
        processMove(m);
        pieceRemotion(m.getSourcePiece(), m.getTargetRow(), m.getTargetColumns());
        pieceInsertion(newPiece, m.getTargetRow(), m.getTargetColumns());
        return;
    }

    //To be executed only after a processMove(m)
    public void unprocessMove(Move m){
        Piece sourcePiece = m.getSourcePiece();
        Piece targetPiece = m.getTargetPiece();

        pieceRemotion(sourcePiece, m.getTargetRow(), m.getTargetColumns());
        pieceInsertion(sourcePiece, m.getSourceRow(), m.getSourceColumns());

        if(targetPiece != null){
            pieceInsertion(targetPiece, m.getTargetRow(), m.getTargetColumns());
        } else{
            updateTrajectory(sourcePiece, m.getTargetRow(), m.getTargetColumns());
        }
        updateTrajectory(sourcePiece, m.getSourceRow(), m.getSourceColumns());

        return;
    }

    private void updateTrajectory(Piece ignorePiece, int r, int c) {
        ArrayList<Piece> arr = whitePossibleMove.get(r).get(c);
        int v = 0;
        for(int i = 0; i < arr.size(); i++) {
            Piece p = arr.get(v); //Getting pieces out and in we must always look at first position
            if(p != ignorePiece) {
                pieceRemotion(p, p.getPosR(), p.getPosC());
                pieceInsertion(p, p.getPosR(), p.getPosC());
            } else{
                v++; //But if we skip a piece, we need to look at the next position
            }
        }
        arr = blackPossibleMove.get(r).get(c);
        v = 0;
        for(int i = 0; i < arr.size(); i++) {
            Piece p = arr.get(v); //Getting pieces out and in we must always look at first position
            if(p != ignorePiece) {
                pieceRemotion(p, p.getPosR(), p.getPosC());
                pieceInsertion(p, p.getPosR(), p.getPosC());
            } else{
                v++;//But if we skip a piece, we need to look at the next position
            }
        }
    }

    public void processEnPassant(Move m, Piece removedPiece) {
        pieceRemotion(m.getSourcePiece(), m.getSourceRow(), m.getSourceColumns());
        pieceRemotion(removedPiece, m.getSourceRow(), m.getTargetColumns());
        pieceInsertion(m.getSourcePiece(), m.getTargetRow(), m.getTargetColumns());
        return;
    }

    public ArrayList<Tile> getPossibleMoves(Piece p) { //Using tiles as markers; the color is not important
        ArrayList<Tile> possibleTiles = new ArrayList<Tile>();
        ArrayList<ArrayList<ArrayList<Piece>>> army = p.getColor() == Color.WHITE ? whitePossibleMove : blackPossibleMove;

        //EnPassant ; Pawn is considered as a special piece
        if(p.type == PieceType.PAWN){
            if(p.getPosR() != 0 && p.getPosR() != 7) { //Could be expanded, but not really usefull
                if(!Board.IndexOutOfRange(p.getPosR(), p.getPosC()+1))
                    if((new Move(p.getPosR(), p.getPosC(), p.getPosR() + (p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()+1, board)).isEnPassant())
                        try {
                            wouldEndInKingCheck((new Move(p.getPosR(), p.getPosC(), p.getPosR() + (p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()+1, board)));
                            possibleTiles.add(new Tile(p.getPosR() + (p.getColor() == Color.BLACK ? 1 : -1), p.getPosC() + 1, null));
                        }catch(Exception e){}
                if(!Board.IndexOutOfRange(p.getPosR(), p.getPosC()-1))
                    if((new Move(p.getPosR(), p.getPosC(), p.getPosR() + (p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()-1, board)).isEnPassant())
                        try {
                            wouldEndInKingCheck((new Move(p.getPosR(), p.getPosC(), p.getPosR() + (p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()-1, board)));
                            possibleTiles.add(new Tile(p.getPosR() + (p.getColor() == Color.BLACK ? 1 : -1), p.getPosC() - 1, null));
                        }catch(Exception e){}
            }
            //Finish of EnPassant
            if(!Board.IndexOutOfRange(p.getPosR()+(p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()+1))
                if(board.getPiece(p.getPosR()+(p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()+1) != null &&
                        board.getPiece(p.getPosR()+(p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()+1).getColor() != p.getColor())
                    try{
                        wouldEndInKingCheck(new Move(p.getPosR(), p.getPosC(), p.getPosR()+(p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()+1, board));
                        possibleTiles.add(new Tile(p.getPosR()+(p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()+1, null));
                    }catch (Exception e){}
            if(!Board.IndexOutOfRange(p.getPosR()+(p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()-1))
                if(board.getPiece(p.getPosR()+(p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()-1) != null &&
                        board.getPiece(p.getPosR()+(p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()-1).getColor() != p.getColor())
                    try{
                        wouldEndInKingCheck(new Move(p.getPosR(), p.getPosC(), p.getPosR()+(p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()-1, board));
                        possibleTiles.add(new Tile(p.getPosR()+(p.getColor() == Color.BLACK ? 1 : -1), p.getPosC()-1, null));
                    }catch (Exception e){}
        }

        for(int i = 0; i < Board.getRows(); i++)
            for(int j = 0; j < Board.getColumns(); j++) {
                if(army.get(i).get(j).contains(p)) {
                    if (board.getPiece(i, j) != null) {
                        if (board.getPiece(i, j).getColor() != p.getColor() && p.getType() != PieceType.PAWN) {
                            Move m = new Move(p.getPosR(), p.getPosC(), i, j, board);
                            try {
                                wouldEndInKingCheck(m);
                                possibleTiles.add(new Tile(i, j, null));
                            } catch (Exception e) {}
                        }
                    } else{
                        Move m = new Move(p.getPosR(), p.getPosC(), i, j, board);
                        try{
                            wouldEndInKingCheck(m);
                            possibleTiles.add(new Tile(i, j, null));
                        }catch (Exception e){}
                    }
                }
            }

        //Castling
        if(p.type == PieceType.KING){
            if((new Move(p.getPosR(), p.getPosC(), p.getPosR(), 0, board)).isACastlingMove())
                possibleTiles.add(new Tile(p.getPosR(), 0, null));
            if((new Move(p.getPosR(), p.getPosC(), p.getPosR(), 7, board)).isACastlingMove())
                possibleTiles.add(new Tile(p.getPosR(), 7, null));
        }
        if(p.type == PieceType.ROOK){
            if((new Move(p.getPosR(), p.getPosC(), p.getPosR(), 4, board)).isACastlingMove())
                possibleTiles.add(new Tile(p.getPosR(), 4, null));
        }

        return possibleTiles;
    }
}
