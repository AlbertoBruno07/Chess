package core;

public class Board {

    private Tile[][] board;
    static final int Rows = 8;
    static final int Columns = 8;
    static final int LastRow = Rows-1;
    static final int LastColumn = Columns -1;

    public static int getRows(){
        return Rows;
    }

    public static int getColumns(){
        return Columns;
    }

    public Board() {
        board = new Tile[Rows][Columns];

        for(int i = 0; i < Rows; i++)
            for(int j = 0; j < Columns; j++)
                board[i][j] = new Tile(i, j, ((i*(Columns-1))+j)%2==0?Color.WHITE:Color.BLACK);
    }

    public static boolean IndexOutOfRange(int r, int c){
        if(r < 0 || r > LastRow)
            return true;
        if(c < 0 || c > LastColumn)
            return true;
        return false;
    }

    public Tile getTile(int r, int c){
        if(IndexOutOfRange(r, c))
            throw new IllegalArgumentException();

        return board[r][c];
    }

    public Piece getPiece(int r, int c){
        return getTile(r, c).getPiece();
    }
}
