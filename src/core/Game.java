package core;

import java.util.ArrayList;

public class Game {

    public Color turn;
    private ArrayList<Piece> blackArmy, whiteArmy;
    private Board board;

    public Game() {
        turn = Color.WHITE;
        blackArmy = new ArrayList<>();
        whiteArmy = new ArrayList<>();
        board = new Board();
        setup();
    }

    private void createArmy(ArrayList<Piece> arrayList, Color color){
        arrayList.add(new Rook(color));
        board.getTile(color == Color.BLACK ? 0 : Board.getRows()-1,0).setPiece(arrayList.getLast());
        arrayList.add(new Rook(color));
        board.getTile(color == Color.BLACK ? 0 : Board.getRows()-1,7).setPiece(arrayList.getLast());

        arrayList.add(new Knight(color));
        board.getTile(color == Color.BLACK ? 0 : Board.getRows()-1,1).setPiece(arrayList.getLast());
        arrayList.add(new Knight(color));
        board.getTile(color == Color.BLACK ? 0 : Board.getRows()-1,6).setPiece(arrayList.getLast());

        arrayList.add(new Bishop(color));
        board.getTile(color == Color.BLACK ? 0 : Board.getRows()-1,2).setPiece(arrayList.getLast());
        arrayList.add(new Bishop(color));
        board.getTile(color == Color.BLACK ? 0 : Board.getRows()-1,5).setPiece(arrayList.getLast());

        arrayList.add(new King(color));
        board.getTile(color == Color.BLACK ? 0 : Board.getRows()-1,3).setPiece(arrayList.getLast());
        arrayList.add(new Queen(color));
        board.getTile(color == Color.BLACK ? 0 : Board.getRows()-1,4).setPiece(arrayList.getLast());

        for(int i = 0; i < Board.getColumns(); i++){
            arrayList.add(new Pawn(color));
            board.getTile(color == Color.BLACK ? 1 : Board.getRows()-2,3).setPiece(arrayList.getLast());
        }
    }

    private void setup(){
        createArmy(blackArmy, Color.BLACK);
        createArmy(whiteArmy, Color.WHITE);
    }

    public void clear(){
        for(int i = 0; i < Board.getRows(); i++)
            for(int j = 0; j < Board.getColumns(); j++)
                board.getTile(i, j).setPiece(null);
    }

    public Color getTurn() {
        return turn;
    }

    public Board getBoard() {
        return board;
    }

    public void removePieceFromArmy(Piece piece, Color color){
        if(color == Color.WHITE)
            whiteArmy.remove(piece);
        else
            blackArmy.remove(piece);
    }

    public void addPieceToArmy(Piece piece, Color color){
        if(color == Color.WHITE)
            whiteArmy.add(piece);
        else
            blackArmy.add(piece);
    }
}