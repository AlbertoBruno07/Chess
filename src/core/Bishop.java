package core;

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
}
