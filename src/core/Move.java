package core;

public class Move {
    private int checkR, checkC;

    private Board board;

    private int sourceRow;
    private int sourceColumns;
    private int targetRow;
    private int targetColumns;
    private int scoreBlack, scoreWhite;
    private String promotedPieceType;

    Piece sourcePiece, targetPiece;

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
        this.board = board;
        sourcePiece = board.getPiece(sourceRow, sourceColumns);
        targetPiece = board.getPiece(targetRow, targetColumns);
        setCheckTile(-1, -1);
    }

    public int getScoreBlack() {
        return scoreBlack;
    }

    public void setScoreBlack(int scoreBlack) {
        this.scoreBlack = scoreBlack;
    }

    public int getScoreWhite() {
        return scoreWhite;
    }

    public void setScoreWhite(int scoreWhite) {
        this.scoreWhite = scoreWhite;
    }

    public Board getBoard() {
        return board;
    }

    public void makeCopyOfBoard(){
        board = new Board(board);
    }

    public Piece getSourcePiece() {
        return sourcePiece;
    }

    public Piece getTargetPiece() {
        return targetPiece;
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

        if(getPiece(r, c).getType() != PieceType.KNIGHT)
            while(!onTarget(r, c)){
                r += (r != targetRow) ? (rCheck ? 1 : -1) : 0;
                c += (c != targetColumns) ? (cCheck ? 1 : -1) : 0;

                if(getPiece(r, c) != null)
                    return !((r == targetRow) && (c == targetColumns));

            }

        return false;
    }

    public boolean isACastlingMove() {
        Piece king = sourcePiece, rook = targetPiece;
        if(king == null || rook == null)
            return false;

        if(king.type == PieceType.ROOK && rook.type == PieceType.KING){
            Piece a = king;
            king = rook;
            rook = a;
        }

        if(king.type != PieceType.KING || rook.type != PieceType.ROOK)
            return false;

        if(checkObstacles())
            return false;

        if(!((King)king).firstMove)
            return false;

        if(rook.color != king.color)
            return false;

        if(!(((Rook)rook).firstMove))
            return false;

        int ac = king.getPosC();
        int s = rook.getPosC()-king.getPosC() < 0 ? -1 : 1;
        while(ac != ( (king.getPosC() + rook.getPosC()) / 2 ) + (king.getPosC() < rook.getPosC() ? 2 : -2 )){
            if(BackgroundOverlay.getStaticInstance().isTileMenaced(king.getPosR(), ac, king.getColor()))
                return false;
            ac += s;
        }

        return true;
    }

    public boolean isPawnPromotionMove() {
        if(sourcePiece.type != PieceType.PAWN)
            return false;

        try {
            sourcePiece.validateMove(this);
        } catch(Exception e){
            return false;
        }

        if(targetRow != (sourcePiece.getColor() == Color.WHITE ? 0 : 7))
            return false;

        return true;
    }

    public boolean isEnPassant(){
        if(sourcePiece == null)
            return false;
        
        if(sourcePiece.type != PieceType.PAWN)
            return false;

        if(targetPiece != null)
            return false;

        if(!Board.IndexOutOfRange(getSourceRow(), getSourceColumns()+1)){
            if(getPiece(getSourceRow(), getSourceColumns()+1) == Game.getPossibleEnPassant() && Game.getPossibleEnPassant() != null){
                return (getTargetRow() == (getSourceRow() + (getSourcePiece().color == Color.WHITE ? -1 : 1))) &&
                        (getSourceColumns()+1 == getTargetColumns()) && getPiece(getSourceRow(), getSourceColumns()+1).getColor() != sourcePiece.getColor();
            }
        }
        if(!Board.IndexOutOfRange(getSourceRow(), getSourceColumns()-1)){
            if(getPiece(getSourceRow(), getSourceColumns()-1) == Game.getPossibleEnPassant()  && Game.getPossibleEnPassant() != null){
                return (getTargetRow() == (getSourceRow() + (getSourcePiece().color == Color.WHITE ? -1 : 1))) &&
                        (getSourceColumns()-1 == getTargetColumns()) && getPiece(getSourceRow(), getSourceColumns()-1).getColor() != sourcePiece.getColor();
            }
        }
        return false;
    }

    public int getCheckR() {
        return checkR;
    }

    public int getCheckC() {
        return checkC;
    }

    public void setCheckTile(int r, int c){
        checkR = r;
        checkC = c;
    }

    public String getPromotedPieceType() {
        return promotedPieceType;
    }

    public void setPromotedPieceType(String promotedPieceType) {
        this.promotedPieceType = promotedPieceType;
    }

    @Override
    public String toString() {
        return "" +
                (char)(97+sourceRow) + sourceColumns + " " +
                (char)(97+targetRow) + targetColumns;
    }
}
