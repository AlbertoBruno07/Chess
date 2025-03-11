package core;

public class Tile {
    private int row, column;
    private Piece piece;
    private Color color;

    public Tile(int row, int column, Color color) {
        this.row = row;
        this.column = column;
        this.color = color;
        piece = null;
    }

    public Tile(Tile tile){
        row = tile.row;
        column = tile.column;
        color = tile.color;
        piece = tile.getPiece();
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        if(piece != null)
            piece.updatePos(row, column);
    }

    public Color getColor() {
        return color;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}