package core;

import java.util.*;

import static java.lang.Math.abs;

public class BackgroundOverlay {
    private ArrayList<ArrayList<ArrayList<Piece>>> blackPossibleMove;
    private ArrayList<ArrayList<ArrayList<Piece>>> whitePossibleMove;
    private Board board;
    private King whiteKing;
    private King blackKing;

    public BackgroundOverlay(Board board) {
        this.board = board;

        blackPossibleMove = new ArrayList<ArrayList<ArrayList<Piece>>>();
        whitePossibleMove = new ArrayList<ArrayList<ArrayList<Piece>>>();

        createArmies();
        initialize();
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

    public void  wouldEndInKingCheck(Move m){
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

    public boolean isKingInCheck(Color kingColor){
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

    private boolean pawnIsMenacingTile(int r, int c, Color movingColor) {
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

    private void initialize(){
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
        if(p.type == PieceType.BISHOP || p.type == PieceType.QUEEN)
            diagonalPieceInsertion(p, r , c);
        if(p.type == PieceType.ROOK || p.type == PieceType.QUEEN)
            orthogonalPieceInsertion(p, r, c);
        if(p.type == PieceType.PAWN)
            pawnInsertion(p, r, c);
        if(p.type == PieceType.KNIGHT)
            knightInsertion(p, r, c);
        if(p.type == PieceType.KING)
            kingInsertion(p, r, c);
    }

    public void pieceRemotion(Piece p, int r, int c){

        for(int i = 0; i < Board.getRows(); i++)
            for(int j = 0; j < Board.getColumns(); j++)
                removePiece(p, i, j);
    }

    public void kingInsertion(Piece p, int r, int c){
        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if(!Board.IndexOutOfRange(r+i, c+j))
                    insertPiece(p, r+i, c+j);
    }

    public void kingRemotion(Piece p, int r, int c){
        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if(!Board.IndexOutOfRange(r+i, c+j))
                    removePiece(p, r+i, c+j);
    }

    public void knightInsertion(Piece p, int r, int c){
        insertPiece(p, r, c);

        if(!Board.IndexOutOfRange(r+1, c+2))
            insertPiece(p, r+1, c+2);
        if(!Board.IndexOutOfRange(r-1, c+2))
            insertPiece(p, r-1, c+2);
        if(!Board.IndexOutOfRange(r+1, c-2))
            insertPiece(p, r+1, c-2);
        if(!Board.IndexOutOfRange(r-1, c-2))
            insertPiece(p, r-1, c-2);

        if(!Board.IndexOutOfRange(r+2, c+1))
            insertPiece(p, r+2, c+1);
        if(!Board.IndexOutOfRange(r-2, c+1))
            insertPiece(p, r-2, c+1);
        if(!Board.IndexOutOfRange(r+2, c-1))
            insertPiece(p, r+2, c-1);
        if(!Board.IndexOutOfRange(r-2, c-1))
            insertPiece(p, r-2, c-1);
    }

    public void knightRemotion(Piece p, int r, int c){
        removePiece(p, r, c);

        if(!Board.IndexOutOfRange(r+1, c+2))
            removePiece(p, r+1, c+2);
        if(!Board.IndexOutOfRange(r-1, c+2))
            removePiece(p, r-1, c+2);
        if(!Board.IndexOutOfRange(r+1, c-2))
            removePiece(p, r+1, c-2);
        if(!Board.IndexOutOfRange(r-1, c-2))
            removePiece(p, r-1, c-2);

        if(!Board.IndexOutOfRange(r+2, c+1))
            removePiece(p, r+2, c+1);
        if(!Board.IndexOutOfRange(r-2, c+1))
            removePiece(p, r-2, c+1);
        if(!Board.IndexOutOfRange(r+2, c-1))
            removePiece(p, r+2, c-1);
        if(!Board.IndexOutOfRange(r-2, c-1))
            removePiece(p, r-2, c-1);
    }

    public void diagonalPieceInsertion(Piece p, int r, int c){
        insertPiece(p, r, c);
        diagonalPieceInsertionDirection(p, r, c, PropagationDirection.BOTTOM_LEFT);
        diagonalPieceInsertionDirection(p, r, c, PropagationDirection.TOP_LEFT);
        diagonalPieceInsertionDirection(p, r, c, PropagationDirection.BOTTOM_RIGHT);
        diagonalPieceInsertionDirection(p, r, c, PropagationDirection.TOP_RIGHT);
    }

    public void orthogonalPieceInsertion(Piece p, int r, int c){
        if(p.type != PieceType.QUEEN)
            insertPiece(p, r, c);
        orthogonalMoveInsertionDirection(p, r, c, PropagationDirection.LEFT);
        orthogonalMoveInsertionDirection(p, r, c, PropagationDirection.TOP);
        orthogonalMoveInsertionDirection(p, r, c, PropagationDirection.BOTTOM);
        orthogonalMoveInsertionDirection(p, r, c, PropagationDirection.RIGHT);
    }

    public void diagonalPieceInsertionDirection(Piece p, int r, int c, PropagationDirection pD){
        int ar = r+1, ac = c+1;

        while(pD == PropagationDirection.BOTTOM_RIGHT && (!(Board.IndexOutOfRange(ar, ac)))){
            insertPiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar++;
            ac++;
        }
        ar = r+1; ac = c-1;
        while(pD == PropagationDirection.BOTTOM_LEFT && !(Board.IndexOutOfRange(ar, ac))){
            insertPiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar++;
            ac--;
        }
        ar = r-1; ac = c+1;
        while(pD == PropagationDirection.TOP_RIGHT && !(Board.IndexOutOfRange(ar, ac)) ){
            insertPiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar--;
            ac++;
        }
        ar = r-1; ac = c-1;
        while(pD == PropagationDirection.TOP_LEFT && !(Board.IndexOutOfRange(ar, ac))){
            insertPiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar--;
            ac--;
        }
    }

    public void orthogonalMoveInsertionDirection(Piece p, int r, int c, PropagationDirection pD){
        int ar = r+1, ac = c;

        while(pD == PropagationDirection.BOTTOM && !(Board.IndexOutOfRange(ar, ac)) ){
            insertPiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar++;
        }
        ar = r-1;
        while(pD == PropagationDirection.TOP && !(Board.IndexOutOfRange(ar, ac))){
            insertPiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar--;
        }
        ar = r; ac = c+1;
        while(pD == PropagationDirection.RIGHT && !(Board.IndexOutOfRange(ar, ac))){
            insertPiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ac++;
        }
        ac = c-1;
        while(pD == PropagationDirection.LEFT && !(Board.IndexOutOfRange(ar, ac))){
            insertPiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ac--;
        }
    }

    public void pawnInsertion(Piece p, int r, int c){
        insertPiece(p, r, c);
        if(p.color == Color.WHITE)
            if(!Board.IndexOutOfRange(r-1, c))
                insertPiece(p, r-1, c);
        if(p.color == Color.BLACK)
            if(!Board.IndexOutOfRange(r+1, c))
                insertPiece(p, r+1, c);
        if(p.getPosR() == ((p.color == Color.WHITE) ? 6 : 1))
            insertPiece(p, r + ((p.color == Color.WHITE) ? -2 : 2), c);
    }

    public void diagonalPieceRemotion(Piece p, int r, int c){
        removePiece(p, r, c);
        diagonalPieceRemotionDirection(p, r, c, PropagationDirection.BOTTOM_LEFT);
        diagonalPieceRemotionDirection(p, r, c, PropagationDirection.TOP_LEFT);
        diagonalPieceRemotionDirection(p, r, c, PropagationDirection.BOTTOM_RIGHT);
        diagonalPieceRemotionDirection(p, r, c, PropagationDirection.TOP_RIGHT);
    }

    public void orthogonalPieceRemotion(Piece p, int r, int c){
        if(p.type != PieceType.QUEEN)
            removePiece(p, r, c);
        orthogonalPieceRemotionDirection(p, r, c, PropagationDirection.LEFT);
        orthogonalPieceRemotionDirection(p, r, c, PropagationDirection.TOP);
        orthogonalPieceRemotionDirection(p, r, c, PropagationDirection.BOTTOM);
        orthogonalPieceRemotionDirection(p, r, c, PropagationDirection.RIGHT);
    }

    public void diagonalPieceRemotionDirection(Piece p, int r, int c, PropagationDirection pD){

        int ar = r+1, ac = c+1;

        while(pD == PropagationDirection.BOTTOM_RIGHT && (!(Board.IndexOutOfRange(ar, ac)))){
            removePiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar++;
            ac++;
        }
        ar = r+1; ac = c-1;
        while(pD == PropagationDirection.BOTTOM_LEFT && !(Board.IndexOutOfRange(ar, ac))){
            removePiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar++;
            ac--;
        }
        ar = r-1; ac = c+1;
        while(pD == PropagationDirection.TOP_RIGHT && !(Board.IndexOutOfRange(ar, ac))){
            removePiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar--;
            ac++;
        }
        ar = r-1; ac = c-1;
        while(pD == PropagationDirection.TOP_LEFT && !(Board.IndexOutOfRange(ar, ac))){
            removePiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar--;
            ac--;
        }
    }

    public void orthogonalPieceRemotionDirection(Piece p, int r, int c, PropagationDirection pD){

        int ar = r+1, ac = c;

        while(pD == PropagationDirection.BOTTOM && !(Board.IndexOutOfRange(ar, ac))){
            removePiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar++;
        }
        ar = r-1;
        while(pD == PropagationDirection.TOP && !(Board.IndexOutOfRange(ar, ac))){
            removePiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar--;
        }
        ar = r; ac = c+1;
        while(pD == PropagationDirection.RIGHT && !(Board.IndexOutOfRange(ar, ac))){
            removePiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ac++;
        }
        ac = c-1;
        while(pD == PropagationDirection.LEFT && !(Board.IndexOutOfRange(ar, ac))){
            removePiece(p, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ac--;
        }
    }

    public void pawnRemotion(Piece p, int r, int c){
        removePiece(p, r, c);
        if(p.color == Color.WHITE) {
            if (!Board.IndexOutOfRange(r+1, c ))
                removePiece(p, r-1, c );
            if(((Pawn)p).getPosR() == 6)
                removePiece(p, r-2, c);
        }
        if(p.color == Color.BLACK) {
            if (!Board.IndexOutOfRange(r-1, c))
                removePiece(p, r+1, c);
            if (((Pawn)p).getPosR() == 1)
                removePiece(p, r+2, c);
        }
    }

    private void insertPiece(Piece p, int r, int c) {
        if(p.color == Color.WHITE)
            whitePossibleMove.get(r).get(c).add(p);
        else
            blackPossibleMove.get(r).get(c).add(p);
    }

    private void removePiece(Piece p, int r, int c){
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

    //To be executed only after a processMove(m)
    public void unprocessMove(Move m){
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

    private void updateTrajectory(Piece ignorePiece, int r, int c) {
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
