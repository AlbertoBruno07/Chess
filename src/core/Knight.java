package core;

import static core.BackgroundOverlay.insertPiece;

public class Knight extends Piece{
    public Knight(Color color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    void validateMove(Move m) {
        super.validateMove(m);

        if(!super.jumpMove(m))
            throw new InvalidMoveException("Invalid Move");
    }

    @Override
    public void pieceInsertion(Board board, int r, int c) {
        insertPiece(this, r, c);

        if(!Board.IndexOutOfRange(r+1, c+2))
            insertPiece(this, r+1, c+2);
        if(!Board.IndexOutOfRange(r-1, c+2))
            insertPiece(this, r-1, c+2);
        if(!Board.IndexOutOfRange(r+1, c-2))
            insertPiece(this, r+1, c-2);
        if(!Board.IndexOutOfRange(r-1, c-2))
            insertPiece(this, r-1, c-2);

        if(!Board.IndexOutOfRange(r+2, c+1))
            insertPiece(this, r+2, c+1);
        if(!Board.IndexOutOfRange(r-2, c+1))
            insertPiece(this, r-2, c+1);
        if(!Board.IndexOutOfRange(r+2, c-1))
            insertPiece(this, r+2, c-1);
        if(!Board.IndexOutOfRange(r-2, c-1))
            insertPiece(this, r-2, c-1);
    }
}
