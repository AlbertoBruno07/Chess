package core;

import static core.BackgroundOverlay.insertPiece;

public class Rook extends Piece{
    public Rook(Color color) {
        super(color, PieceType.ROOK);
    }

    @Override
    void validateMove(Move m) {
        super.validateMove(m);

        if(!super.orthogonalMove(m))
            throw new InvalidMoveException("Invalid move");
    }

    @Override
    public void pieceInsertion(Board board, int r, int c) {
        int ar = r+1, ac = c;

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
