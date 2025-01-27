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

    public Move(int sourceRow, int sourceColumns, int targetRow, int targetColumns, Board board) {
        this.sourceRow = sourceRow;
        this.sourceColumns = sourceColumns;
        this.targetRow = targetRow;
        this.targetColumns = targetColumns;
        if(Move.board != null)
            Move.board = board;
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
        return getPiece(r, c) != null;
    }

    public void wouldEndInKingCheck(){}

    public boolean isTargetOccupiedByAlly(){
        Piece p = getPiece(targetRow, targetColumns);
        if(p != null)
            return getPiece(sourceRow, sourceColumns).getColor() == p.getColor();
        return false;
    }

    public boolean isKingInCheck(int kingRow, int kingColumn, Color kingColor){
        return false;
    }

    public boolean checkObstacles(){
        int r = sourceRow;
        int c = sourceColumns;

        boolean rCheck = r < targetRow,
                cCheck = c < targetColumns;

        while(!onTarget(r, c)){
            if(getPiece(r, c) != null)
                return true;

            r += rCheck && (r != targetRow) ? 1 : -1;
            c += cCheck && (c != targetColumns) ? 1 : -1;
        }

        return false;
    }
}
