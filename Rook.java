package smartchess;

class Rook extends Piece {
    public Rook(boolean isWhite) {
        super(isWhite);
    }
    
    @Override
    public boolean isValidMove(Position from, Position to, ChessBoard board) {
        if (from.row != to.row && from.col != to.col) {
            return false;
        }
        return isPathClear(from, to, board);
    }
    
    @Override
    public String getName() { return "Rook"; }
    
    @Override
    public String getSymbol() { return "R"; }
    
    @Override
    public String getUnicodeSymbol() { return isWhite ? "♖" : "♜"; }
}