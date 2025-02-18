package core;

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
}
