package smartchess;

import java.io.Serializable;

class Move implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public final Position from;
    public final Position to;
    public final Piece piece;
    public final Piece captured;
    
    public Move(Position from, Position to, Piece piece, Piece captured) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.captured = captured;
    }
    
    @Override
    public String toString() {
        String notation = piece.getSymbol() + from.toString() + 
                         (captured != null ? "x" : "-") + to.toString();
        return notation;
    }
}