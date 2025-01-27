package core;

import java.util.Objects;

public abstract class Piece {

    protected Color color;
    protected PieceType type;

    public Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
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
        if(m.getSourceColumns() == m.getTargetColumns() || m.getSourceRow() == m.getTargetRow())
            throw new InvalidMoveException("User specified a move with same source and destination");
        if(m.isTargetOccupiedByAlly())
            throw new InvalidMoveException("User specified a move that would end on another piece of its army!")
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
                getType() == piece.getType();
    }

    @Override
    public String toString() {
        return "Piece{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }
}
