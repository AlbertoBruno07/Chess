package core;

import gui.BoardPanel;

import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

public abstract class Piece {

    protected Color color;
    protected PieceType type;
    protected int r, c;

    public Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public int getPosR(){
        return r;
    }

    public int getPosC(){
        return c;
    }

    public void updatePos(int r, int c){
        this.r = r;
        this.c = c;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public PieceType getType() {
        return type;
    }

    public void setType(PieceType type) {
        this.type = type;
    }

    void validateMove(Move m){
        if(m.getSourceColumns() == m.getTargetColumns() && m.getSourceRow() == m.getTargetRow())
            throw new InvalidMoveException("User specified a move with same source and destination");
        if(m.isTargetOccupiedByAlly())
            throw new InvalidMoveException("User specified a move that would end on another piece of its army!");
    }
    void executeMove(Move m){}
    boolean validateCapture(Move m){
        try{
            validateMove(m);
            return true;
        } catch(InvalidMoveException ime){
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Piece piece)) return false;
        return getColor() == piece.getColor() &&
                getType() == piece.getType() &&
                getPosR() == piece.getPosR() &&
                getPosC() == piece.getPosC();
    }

    @Override
    public String toString() {
        return "Piece{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }

    protected boolean diagonalMove(Move m) {
        return ((abs(m.getSourceColumns() - m.getTargetColumns()) ==
                abs(m.getSourceRow() - m.getTargetRow()))
                && !m.checkObstacles());
    }

    public boolean orthogonalMove(Move m){
        return ((m.getSourceRow() == m.getTargetRow() ||
                m.getSourceColumns() == m.getTargetColumns()) &&
                !m.checkObstacles());
    }

    public boolean jumpMove(Move m){
        int dC = (abs(m.getSourceColumns() - m.getTargetColumns()))+1;
        int dR = (abs(m.getSourceRow() - m.getTargetRow()))+1;
        return ((dC*dC)+(dR*dR)) == 13;
    }

    protected boolean tryForwardMove(Move m, int offset){
        return m.getSourceColumns() == m.getTargetColumns()
                && m.getTargetRow() - m.getSourceRow() == (
                m.getPiece(m.getSourceRow(), m.getSourceColumns()).color == Color.BLACK
                ? offset : offset * -1);
    }

    protected boolean pawnMove(Move m, boolean fM) {
        if(m.getPiece(m.getTargetRow(), m.getTargetColumns()) != null)
            return ((m.getSourceRow() - m.getTargetRow()) == (m.getSourcePiece().color == Color.WHITE ? 1 : -1)
                    && abs(m.getSourceColumns() - m.getTargetColumns()) == 1);

        if(m.getTargetColumns() != m.getSourceColumns())
            return false;

        if(fM)
            if(tryForwardMove(m, 2)) {
                Game.setPossibleEnPassant(this);
                return true;
            }

        return tryForwardMove(m, 1);

    }

    protected boolean trySingleStepMove(Move m) {
        return (abs(m.getSourceRow()-m.getTargetRow()) <= 1) &&
                (abs(m.getSourceColumns() - m.getTargetColumns())) <= 1;
    }

    public void pieceInsertion(Board board, int r, int c){}

    private boolean rookIsInInitialPosition(Rook rook) {
        if(rook.color == Color.WHITE && rook.getPosR() != 7)
            return false;
        if(rook.color == Color.BLACK && rook.getPosR() != 0)
            return false;
        if(rook.getPosC() != 0 && rook.getPosC() != 7)
            return false;
        return true;
    }
}
