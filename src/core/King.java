package core;

import static core.BackgroundOverlay.insertPiece;

public class King extends Piece{
    boolean firstMove;

    public King(Color color) {
        super(color, PieceType.KING);
        firstMove = true;
    }

    @Override
    void validateMove(Move m) {
        super.validateMove(m);

        if(!super.trySingleStepMove(m))
            throw new InvalidMoveException("Invalid Move");

        updateFM();

    }

    public void updateFM(){
        firstMove = false;
    }

    @Override
    public void pieceInsertion(Board board, int r, int c) {
        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if(!Board.IndexOutOfRange(r+i, c+j))
                    insertPiece(this, r+i, c+j);
    }
}
