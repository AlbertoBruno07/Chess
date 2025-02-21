package core;

import java.util.ArrayList;

public class Game {

    public Color turn;
    private ArrayList<Piece> blackArmy, whiteArmy;
    private Board board;

    private BackgroundOverlay backgroundOverlay;

    public BackgroundOverlay getBackgroundOverlay() {
        return backgroundOverlay;
    }

    public Game() {
        turn = Color.WHITE;
        blackArmy = new ArrayList<>();
        whiteArmy = new ArrayList<>();
        board = new Board();
        setup();
        backgroundOverlay = BackgroundOverlay.getInstance(board);
        BackgroundOverlay.setKing((King)board.getPiece(0, 4), Color.BLACK);
        BackgroundOverlay.setKing((King)board.getPiece(7, 4), Color.WHITE);
    }

    private void createArmy(ArrayList<Piece> arrayList, Color color){
        
        int row = color == Color.BLACK ? 0 : Board.getRows()-1;

        arrayList.add(new Rook(color));
        board.getTile(row,0).setPiece(arrayList.getLast());
        arrayList.add(new Rook(color));
        board.getTile(row,7).setPiece(arrayList.getLast());

        arrayList.add(new Knight(color));
        board.getTile(row,1).setPiece(arrayList.getLast());
        arrayList.add(new Knight(color));
        board.getTile(row,6).setPiece(arrayList.getLast());

        arrayList.add(new Bishop(color));
        board.getTile(row,2).setPiece(arrayList.getLast());
        arrayList.add(new Bishop(color));
        board.getTile(row,5).setPiece(arrayList.getLast());

        arrayList.add(new King(color));
        board.getTile(row,4).setPiece(arrayList.getLast());
        arrayList.add(new Queen(color));
        board.getTile(row,3).setPiece(arrayList.getLast());

        row = color == Color.BLACK ? 1 : Board.getRows()-2;
        for(int i = 0; i < Board.getColumns(); i++){
            arrayList.add(new Pawn(color));
            board.getTile(row,i).setPiece(arrayList.getLast());
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

    public void removePieceFromArmy(Piece piece){
        if(piece.color == Color.WHITE)
            whiteArmy.remove(piece);
        else
            blackArmy.remove(piece);
    }

    public void addPieceToArmy(Piece piece){
        if(piece.color == Color.WHITE)
            whiteArmy.add(piece);
        else
            blackArmy.add(piece);
    }

    public boolean isMoveSourceValid(int r, int c){
        if(board.getTile(r, c).getPiece() == null)
            return false;

        if(board.getTile(r, c).getPiece().color != turn)
            return false;

        return true;
    }

    public boolean processMove(int sR, int sC,
                               int tR, int tC){
        Move move = new Move(sR, sC, tR, tC, board);

        try{
            board.getPiece(sR, sC).validateMove(move);
            //move.wouldEndInKingCheck(backgroundOverlay);
            if(board.getPiece(tR, tC) != null) {
                if (board.getPiece(sR, sC).color == board.getPiece(tR, tC).color)
                    return false;
                removePieceFromArmy(board.getPiece(tR, tC));
            }
            board.getTile(tR, tC).setPiece(board.getPiece(sR, sC));
            board.getTile(sR, sC).setPiece(null);
            BackgroundOverlay.wouldEndInKingCheck(move);
            switchTurn();
            BackgroundOverlay.processMove(move);
            if(board.getPiece(tR, tC).type == PieceType.PAWN)
                ((Pawn)board.getPiece(tR, tC)).updateEp();
            return true;
        } catch(InvalidMoveException ime){
            System.out.println(ime);
            return false;
        }
    }

    private void switchTurn() {
        turn = (turn == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

}