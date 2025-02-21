package core;

import static core.BackgroundOverlay.insertPiece;

public class Queen extends Piece{
    public Queen(Color color) {
        super(color, PieceType.QUEEN);
    }

    @Override
    void validateMove(Move m) {
        super.validateMove(m);

        if(!(orthogonalMove(m) || diagonalMove(m)))
            throw new InvalidMoveException("Invalid move detected");
    }

    @Override
    public void pieceInsertion(Board board, int r, int c) {
        insertPiece(this, r, c);
        int ar = r+1, ac = c+1;

        while((!(Board.IndexOutOfRange(ar, ac)))){
            insertPiece(this, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar++;
            ac++;
        }
        ar = r+1; ac = c-1;
        while(!(Board.IndexOutOfRange(ar, ac))){
            insertPiece(this, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar++;
            ac--;
        }
        ar = r-1; ac = c+1;
        while(!(Board.IndexOutOfRange(ar, ac)) ){
            insertPiece(this, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar--;
            ac++;
        }
        ar = r-1; ac = c-1;
        while(!(Board.IndexOutOfRange(ar, ac))){
            insertPiece(this, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar--;
            ac--;
        }

        ar = r+1; ac = c;

        while(!(Board.IndexOutOfRange(ar, ac)) ){
            insertPiece(this, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar++;
        }
        ar = r-1;
        while(!(Board.IndexOutOfRange(ar, ac))){
            insertPiece(this, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ar--;
        }
        ar = r; ac = c+1;
        while(!(Board.IndexOutOfRange(ar, ac))){
            insertPiece(this, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ac++;
        }
        ac = c-1;
        while(!(Board.IndexOutOfRange(ar, ac))){
            insertPiece(this, ar, ac);
            if((board.getPiece(ar, ac) != null))
                break;
            ac--;
        }
    }
}
