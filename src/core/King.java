package core;

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
}
