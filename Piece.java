package smartchess;

import java.io.Serializable;

public abstract class Piece implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected boolean isWhite;
    protected int moveCount;
    
    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
        this.moveCount = 0;
    }
    
    public boolean isWhite() {
        return isWhite;
    }
    
    public int getMoveCount() {
        return moveCount;
    }
    
    public void incrementMoveCount() {
        moveCount++;
    }
    
    public void decrementMoveCount() {
        if (moveCount > 0) moveCount--;
    }
    
    public abstract boolean isValidMove(Position from, Position to, ChessBoard board);
    public abstract String getName();
    public abstract String getSymbol();
    public abstract String getUnicodeSymbol();
    
    protected boolean isPathClear(Position from, Position to, ChessBoard board) {
        int rowDir = Integer.compare(to.row - from.row, 0);
        int colDir = Integer.compare(to.col - from.col, 0);
        
        int currentRow = from.row + rowDir;
        int currentCol = from.col + colDir;
        
        while (currentRow != to.row || currentCol != to.col) {
            if (board.getPiece(currentRow, currentCol) != null) {
                return false;
            }
            currentRow += rowDir;
            currentCol += colDir;
        }
        
        return true;
    }

}