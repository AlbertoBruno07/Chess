package core;

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

        if(firstMove)
            updateEp();
    }

    private void updateEp() {
        firstMove = false;
    }
}
