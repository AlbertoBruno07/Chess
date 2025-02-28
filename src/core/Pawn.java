package core;

import static core.BackgroundOverlay.insertPiece;

public class Pawn extends Piece{
    private boolean firstMove;
    private Game gameInstance;

    public Pawn(Color color) {
        super(color, PieceType.PAWN);
        firstMove = true;
    }

    @Override
    void validateMove(Move m) {
        super.validateMove(m);

        if(!super.pawnMove(m, firstMove))
            throw new InvalidMoveException("Invalid Move!");
    }

    public boolean isFirstMove(){
        return firstMove;
    }

    public void updateFM() {
        firstMove = false;
    }

    @Override
    public void pieceInsertion(Board board, int r, int c) {
        insertPiece(this, r, c);

        if(color == Color.WHITE)
            if (!Board.IndexOutOfRange(r - 1, c))
                insertPiece(this, r - 1, c);

        if(color == Color.BLACK)
            if (!Board.IndexOutOfRange(r + 1, c))
                insertPiece(this, r + 1, c);

        if(getPosR() == ((color == Color.WHITE) ? 6 : 1))
            insertPiece(this, r + ((color == Color.WHITE) ? -2 : 2), c);
    }
}
