package core;

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
}
