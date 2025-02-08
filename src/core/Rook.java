package core;

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
}
