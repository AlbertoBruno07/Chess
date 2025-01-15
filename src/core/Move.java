package core;

public class Move {

    static private Board board;

    private int sourceRow;
    private int sourceColumns;
    private int targetRow;
    private int targetColumns;

    public int getSourceRow() {
        return sourceRow;
    }

    public int getSourceColumns() {
        return sourceColumns;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public int getTargetColumns() {
        return targetColumns;
    }


    public Piece getPiece(int r, int c){
        return board.getTile(r, c).getPiece();
    }

    public void setPiece(int r, int c, Piece P){
        board.getTile(r, c).setPiece(P);
    }

    public boolean onTarget(int r, int c){
        return (r == targetRow) && (c == targetColumns);
    }

    public boolean checkPiecePresence(int r, int c){
        return board.getTile(r, c).getPiece() != null;
    }

    public void wouldEndInKingCheck(){}

    //Punto 6 DA IMPLEMENTARE
}
