package core;

import static core.BackgroundOverlay.insertPiece;

public class King extends Piece{
    boolean firstMove;

    public King(Color color) {
        super(color, PieceType.KING);
        firstMove = true;
    }

    protected boolean tryCastling(Move m){
        return false;
    }

    @Override
    void validateMove(Move m) {
        super.validateMove(m);

        boolean validMoveDetected = true;

        if(!m.isKingInCheck(m.getSourceRow(), m.getSourceColumns(), super.color))
            validMoveDetected = tryCastling(m);

        if(!super.trySingleStepMove(m) || validMoveDetected)
            throw new InvalidMoveException("Invalid Move");
    }

    @Override
    public void pieceInsertion(Board board, int r, int c) {
        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if(!Board.IndexOutOfRange(r+i, c+j))
                    insertPiece(this, r+i, c+j);
    }
}
