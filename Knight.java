package smartchess;

class Knight extends Piece {
    public Knight(boolean isWhite) {
        super(isWhite);
    }
    
    @Override
    public boolean isValidMove(Position from, Position to, ChessBoard board) {
        int rowDiff = Math.abs(to.row - from.row);
        int colDiff = Math.abs(to.col - from.col);
        
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
    
    @Override
    public String getName() { return "Knight"; }
    
    @Override
    public String getSymbol() { return "N"; }
    
    @Override
    public String getUnicodeSymbol() { return isWhite ? "♘" : "♞"; }
}