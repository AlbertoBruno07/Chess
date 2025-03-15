package core;

import gui.GameFrame.AsideWindow;

public class ScoreBoard {
    private int blackPoints, whitePoints;

    public ScoreBoard() {
        blackPoints = 0;
        whitePoints = 0;
    }

    public void updateScore(Color color, int gainedPoints){
        if(color == Color.BLACK) {
            blackPoints += gainedPoints;
            AsideWindow.updateScore(color, blackPoints);
        }
        else {
            whitePoints += gainedPoints;
            AsideWindow.updateScore(color, whitePoints);
        }
    }

    public void pieceHasBeenEaten(Piece piece) {
        updateScore(piece.getColor() == Color.WHITE ? Color.BLACK : Color.WHITE,
                pointsFromPiece(piece.getType()));
    }

    private int pointsFromPiece(PieceType type) {
        return switch(type){
            case PAWN -> 1;
            case KNIGHT, BISHOP -> 3;
            case QUEEN -> 9;
            case ROOK -> 5;
            case KING -> 4;
        };
    }

    public int getBlackPoints() {
        return blackPoints;
    }

    public int getWhitePoints() {
        return whitePoints;
    }
}
