
package smartchess;

class King extends Piece {
    public King(boolean isWhite) {
        super(isWhite);
    }
    
    @Override
    public boolean isValidMove(Position from, Position to, ChessBoard board) {
        int rowDiff = Math.abs(to.row - from.row);
        int colDiff = Math.abs(to.col - from.col);
        
        // Normal king move (one square in any direction)
        if (rowDiff <= 1 && colDiff <= 1) {
            return true;
        }
        
        // Castling
        if (moveCount == 0 && rowDiff == 0 && colDiff == 2) {
            boolean kingside = to.col > from.col;
            return board.canCastle(isWhite, kingside) && 
                   !board.isInCheck(isWhite);
        }
        
        return false;
    }
    
    @Override
    public String getName() { return "King"; }
    
    @Override
    public String getSymbol() { return "K"; }
    
    @Override
    public String getUnicodeSymbol() { return isWhite ? "♔" : "♚"; }
}