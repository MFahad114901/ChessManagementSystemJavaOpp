package smartchess;
class Bishop extends Piece {
    public Bishop(boolean isWhite) {
        super(isWhite);
    }
    
    @Override
    public boolean isValidMove(Position from, Position to, ChessBoard board) {
        int rowDiff = Math.abs(to.row - from.row);
        int colDiff = Math.abs(to.col - from.col);
        
        if (rowDiff != colDiff) {
            return false;
        }
        
        return isPathClear(from, to, board);
    }
    
    @Override
    public String getName() { return "Bishop"; }
    
    @Override
    public String getSymbol() { return "B"; }
    
    @Override
    public String getUnicodeSymbol() { return isWhite ? "♗" : "♝"; }
}