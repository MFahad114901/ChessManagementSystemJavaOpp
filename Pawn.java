package smartchess;

class Pawn extends Piece {
    public Pawn(boolean isWhite) {
        super(isWhite);
    }
    
    @Override
    public boolean isValidMove(Position from, Position to, ChessBoard board) {
        int direction = isWhite ? -1 : 1;
        int rowDiff = to.row - from.row;
        int colDiff = Math.abs(to.col - from.col);
        
        // Move forward one square
        if (colDiff == 0 && rowDiff == direction) {
            return board.getPiece(to.row, to.col) == null;
        }
        
        // Move forward two squares from starting position
        if (colDiff == 0 && rowDiff == 2 * direction && moveCount == 0) {
            int middleRow = from.row + direction;
            return board.getPiece(middleRow, from.col) == null && 
                   board.getPiece(to.row, to.col) == null;
        }
        
        // Capture diagonally
        if (colDiff == 1 && rowDiff == direction) {
            Piece target = board.getPiece(to.row, to.col);
            return target != null && target.isWhite() != isWhite;
        }
        
        return false;
    }
    
    @Override
    public String getName() { return "Pawn"; }
    
    @Override
    public String getSymbol() { return ""; }
    
    @Override
    public String getUnicodeSymbol() { return isWhite ? "♙" : "♟"; }
}