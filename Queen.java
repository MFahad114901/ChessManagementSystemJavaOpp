
package smartchess;
class Queen extends Piece {
    public Queen(boolean isWhite) {
        super(isWhite);
    }
    
    @Override
    public boolean isValidMove(Position from, Position to, ChessBoard board) {
        int rowDiff = Math.abs(to.row - from.row);
        int colDiff = Math.abs(to.col - from.col);
        
        // Move like rook or bishop
        if (from.row == to.row || from.col == to.col || rowDiff == colDiff) {
            return isPathClear(from, to, board);
        }
        
        return false;
    }
    
    @Override
    public String getName() { return "Queen"; }
    
    @Override
    public String getSymbol() { return "Q"; }
    
    @Override
    public String getUnicodeSymbol() { return isWhite ? "♕" : "♛"; }
}