package core;

public class Tile {
    private int row, column;
    private Piece piece;
    private Color color;

    public Tile(int row, int column, Piece piece, Color color) {
        this.row = row;
        this.column = column;
        this.piece = piece;
        this.color = color;
    }

    public Tile(int row, int column, Color color) {
        this.row = row;
        this.column = column;
        this.color = color;
        piece = null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}
