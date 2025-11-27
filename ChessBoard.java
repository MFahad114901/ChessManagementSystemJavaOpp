package smartchess;

// ChessBoard.java - Complete Chess Board Logic
import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

public class ChessBoard {
    private Piece[][] board;
    private boolean whiteTurn;
    private Stack<Move> moveHistory;
    private Position enPassantTarget;
    private boolean whiteKingMoved;
    private boolean blackKingMoved;
    private boolean whiteRookLeftMoved;
    private boolean whiteRookRightMoved;
    private boolean blackRookLeftMoved;
    private boolean blackRookRightMoved;
    
    public ChessBoard() {
        board = new Piece[8][8];
        moveHistory = new Stack<>();
        whiteTurn = true;
        initializeBoard();
    }
    
    public void initializeBoard() {
        // Clear board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
        
        // Initialize black pieces (top of board - row 0 and 1)
        board[0][0] = new Rook(false);
        board[0][1] = new Knight(false);
        board[0][2] = new Bishop(false);
        board[0][3] = new Queen(false);
        board[0][4] = new King(false);
        board[0][5] = new Bishop(false);
        board[0][6] = new Knight(false);
        board[0][7] = new Rook(false);
        
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(false);
        }
        
        // Initialize white pieces (bottom of board - row 6 and 7)
        board[7][0] = new Rook(true);
        board[7][1] = new Knight(true);
        board[7][2] = new Bishop(true);
        board[7][3] = new Queen(true);
        board[7][4] = new King(true);
        board[7][5] = new Bishop(true);
        board[7][6] = new Knight(true);
        board[7][7] = new Rook(true);
        
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Pawn(true);
        }
        
        whiteTurn = true;
        moveHistory.clear();
        enPassantTarget = null;
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteRookLeftMoved = false;
        whiteRookRightMoved = false;
        blackRookLeftMoved = false;
        blackRookRightMoved = false;
    }
    
    public Piece getPiece(int row, int col) {
        if (isValidPosition(row, col)) {
            return board[row][col];
        }
        return null;
    }
    
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
    
    public boolean isWhiteTurn() {
        return whiteTurn;
    }
    
    public boolean movePiece(Position from, Position to) {
        Piece piece = getPiece(from.row, from.col);
        
        // Check if there's a piece at the source position
        if (piece == null) {
            return false;
        }
        
        // Check if it's the correct player's turn
        if (piece.isWhite() != whiteTurn) {
            return false;
        }
        
        // Validate the move
        if (!isValidMove(from, to)) {
            return false;
        }
        
        // Store move for undo functionality
        Piece capturedPiece = getPiece(to.row, to.col);
        Move move = new Move(from, to, piece, capturedPiece);
        
        // Handle special moves (castling, en passant)
        handleSpecialMoves(from, to, piece);
        
        // Execute the move
        board[to.row][to.col] = piece;
        board[from.row][from.col] = null;
        piece.incrementMoveCount();
        
        // Check for pawn promotion
        if (piece instanceof Pawn) {
            if ((piece.isWhite() && to.row == 0) || (!piece.isWhite() && to.row == 7)) {
                board[to.row][to.col] = new Queen(piece.isWhite());
            }
        }
        
        // Add move to history
        moveHistory.push(move);
        
        // Switch turns
        whiteTurn = !whiteTurn;
        
        return true;
    }
    
    private void handleSpecialMoves(Position from, Position to, Piece piece) {
        // Track king and rook movements for castling eligibility
        if (piece instanceof King) {
            if (piece.isWhite()) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
            
            // Handle castling move
            if (Math.abs(to.col - from.col) == 2) {
                if (to.col > from.col) { 
                    // Kingside castling
                    board[from.row][5] = board[from.row][7];
                    board[from.row][7] = null;
                } else { 
                    // Queenside castling
                    board[from.row][3] = board[from.row][0];
                    board[from.row][0] = null;
                }
            }
        }
        
        // Track rook movements for castling eligibility
        if (piece instanceof Rook) {
            if (piece.isWhite()) {
                if (from.col == 0) whiteRookLeftMoved = true;
                if (from.col == 7) whiteRookRightMoved = true;
            } else {
                if (from.col == 0) blackRookLeftMoved = true;
                if (from.col == 7) blackRookRightMoved = true;
            }
        }
        
        // Handle en passant capture
        if (piece instanceof Pawn && enPassantTarget != null) {
            if (to.equals(enPassantTarget)) {
                int captureRow = piece.isWhite() ? to.row + 1 : to.row - 1;
                board[captureRow][to.col] = null;
            }
        }
        
        // Set en passant target for next move
        enPassantTarget = null;
        if (piece instanceof Pawn && Math.abs(to.row - from.row) == 2) {
            enPassantTarget = new Position((from.row + to.row) / 2, from.col);
        }
    }
    
    public boolean isValidMove(Position from, Position to) {
        Piece piece = getPiece(from.row, from.col);
        if (piece == null) return false;
        
        // Can't capture own piece
        Piece targetPiece = getPiece(to.row, to.col);
        if (targetPiece != null && targetPiece.isWhite() == piece.isWhite()) {
            return false;
        }
        
        // Check if the move is valid according to piece's movement rules
        if (!piece.isValidMove(from, to, this)) {
            return false;
        }
        
        // Check if the move would put own king in check
        return !wouldBeInCheck(from, to, piece.isWhite());
    }
    
    public ArrayList<Position> getValidMoves(Position from) {
        ArrayList<Position> validMoves = new ArrayList<>();
        Piece piece = getPiece(from.row, from.col);
        
        if (piece == null) return validMoves;
        
        // Check all possible positions on the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position to = new Position(row, col);
                if (isValidMove(from, to)) {
                    validMoves.add(to);
                }
            }
        }
        
        return validMoves;
    }
    
    private boolean wouldBeInCheck(Position from, Position to, boolean isWhite) {
        // Temporarily make the move to test if it results in check
        Piece piece = board[from.row][from.col];
        Piece captured = board[to.row][to.col];
        
        board[to.row][to.col] = piece;
        board[from.row][from.col] = null;
        
        boolean inCheck = isInCheck(isWhite);
        
        // Undo the temporary move
        board[from.row][from.col] = piece;
        board[to.row][to.col] = captured;
        
        return inCheck;
    }
    
    public boolean isInCheck(boolean isWhite) {
        Position kingPos = findKing(isWhite);
        if (kingPos == null) return false;
        
        // Check if any opponent piece can attack the king
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.isWhite() != isWhite) {
                    if (piece.isValidMove(new Position(row, col), kingPos, this)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isCheckmate(boolean isWhite) {
        // Must be in check to be checkmate
        if (!isInCheck(isWhite)) return false;
        
        // Check if any move can get out of check
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                Piece piece = board[fromRow][fromCol];
                if (piece != null && piece.isWhite() == isWhite) {
                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            Position from = new Position(fromRow, fromCol);
                            Position to = new Position(toRow, toCol);
                            if (isValidMove(from, to)) {
                                return false; // Found a legal move, not checkmate
                            }
                        }
                    }
                }
            }
        }
        return true; // No legal moves available, it's checkmate
    }
    
    public boolean isStalemate(boolean isWhite) {
        // Must NOT be in check to be stalemate
        if (isInCheck(isWhite)) return false;
        
        // Check if any legal move exists
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                Piece piece = board[fromRow][fromCol];
                if (piece != null && piece.isWhite() == isWhite) {
                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            Position from = new Position(fromRow, fromCol);
                            Position to = new Position(toRow, toCol);
                            if (isValidMove(from, to)) {
                                return false; // Found a legal move, not stalemate
                            }
                        }
                    }
                }
            }
        }
        return true; // No legal moves but not in check, it's stalemate
    }
    
    private Position findKing(boolean isWhite) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    return new Position(row, col);
                }
            }
        }
        return null;
    }
    
    public boolean undoLastMove() {
        if (moveHistory.isEmpty()) return false;
        
        Move lastMove = moveHistory.pop();
        
        // Restore the piece to its original position
        board[lastMove.from.row][lastMove.from.col] = lastMove.piece;
        board[lastMove.to.row][lastMove.to.col] = lastMove.captured;
        
        // Decrement move count
        lastMove.piece.decrementMoveCount();
        
        // Switch turns back
        whiteTurn = !whiteTurn;
        
        return true;
    }
    
    public boolean saveGame(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(board);
            oos.writeBoolean(whiteTurn);
            oos.writeObject(moveHistory);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @SuppressWarnings("unchecked")
    public boolean loadGame(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            board = (Piece[][]) ois.readObject();
            whiteTurn = ois.readBoolean();
            moveHistory = (Stack<Move>) ois.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean canCastle(boolean isWhite, boolean kingside) {
        // Check if king has moved
        if (isWhite && whiteKingMoved) return false;
        if (!isWhite && blackKingMoved) return false;
        
        int row = isWhite ? 7 : 0;
        
        if (kingside) {
            // Check if right rook has moved
            if ((isWhite && whiteRookRightMoved) || (!isWhite && blackRookRightMoved)) {
                return false;
            }
            // Check if squares between king and rook are empty
            return board[row][5] == null && board[row][6] == null;
        } else {
            // Check if left rook has moved
            if ((isWhite && whiteRookLeftMoved) || (!isWhite && blackRookLeftMoved)) {
                return false;
            }
            // Check if squares between king and rook are empty
            return board[row][1] == null && board[row][2] == null && board[row][3] == null;
        }
    }
}