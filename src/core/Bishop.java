package core;

import static core.BackgroundOverlay.insertPiece;

public class Bishop extends Piece{
    boolean onWhite;

    public Bishop(Color color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    void validateMove(Move m) {
        super.validateMove(m);

        //Following the development guide, here should be checked
        //whether the move ends on a tile of the same color; however,
        //if the move is diagonal the check is already satisfied

        if(!super.diagonalMove(m))
            throw new InvalidMoveException("Invalid move");
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
    }
}
