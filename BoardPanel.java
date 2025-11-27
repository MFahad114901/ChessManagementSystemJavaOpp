package smartchess;

// BoardPanel.java - Visual representation of chess board
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BoardPanel extends JPanel {
    private static final int SQUARE_SIZE = 70;
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color SELECTED_COLOR = new Color(186, 202, 68);
    private static final Color VALID_MOVE_COLOR = new Color(100, 150, 100, 100);
    
    private ChessBoard board;
    private ChessGUI gui;
    private Position selectedSquare;
    private ArrayList<Position> validMoves;
    
    public BoardPanel(ChessBoard board, ChessGUI gui) {
        this.board = board;
        this.gui = gui;
        this.selectedSquare = null;
        this.validMoves = new ArrayList<>();
        
        setPreferredSize(new Dimension(SQUARE_SIZE * 8, SQUARE_SIZE * 8));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }
    
    private void handleMouseClick(MouseEvent e) {
        int row = e.getY() / SQUARE_SIZE;
        int col = e.getX() / SQUARE_SIZE;
        
        if (!board.isValidPosition(row, col)) return;
        
        Position clickedPos = new Position(row, col);
        
        if (selectedSquare == null) {
            // Select piece
            Piece piece = board.getPiece(row, col);
            if (piece != null && piece.isWhite() == board.isWhiteTurn()) {
                selectedSquare = clickedPos;
                validMoves = board.getValidMoves(selectedSquare);
                gui.updateStatus("Selected: " + piece.getName());
                repaint();
            }
        } else {
            // Try to move piece
            if (validMoves.contains(clickedPos)) {
                Piece piece = board.getPiece(selectedSquare.row, selectedSquare.col);
                String moveNotation = getMoveNotation(selectedSquare, clickedPos, piece);
                
                if (board.movePiece(selectedSquare, clickedPos)) {
                    gui.addMoveToHistory(moveNotation);
                    gui.updateTurn();
                    
                    // Check game state
                    if (board.isCheckmate(!board.isWhiteTurn())) {
                        String winner = board.isWhiteTurn() ? "Black" : "White";
                        gui.updateStatus("Checkmate! " + winner + " wins!");
                        JOptionPane.showMessageDialog(this, "Checkmate! " + winner + " wins!");
                    } else if (board.isStalemate(!board.isWhiteTurn())) {
                        gui.updateStatus("Stalemate! Game is a draw!");
                        JOptionPane.showMessageDialog(this, "Stalemate! Game is a draw!");
                    } else if (board.isInCheck(!board.isWhiteTurn())) {
                        gui.updateStatus("Check!");
                    } else {
                        gui.updateStatus("Move made successfully");
                    }
                }
            }
            
            selectedSquare = null;
            validMoves.clear();
            repaint();
        }
    }
    
    private String getMoveNotation(Position from, Position to, Piece piece) {
        String moveNum = "";
        if (board.isWhiteTurn()) {
            int moveCount = (gui != null) ? 1 : 1; // Simplified
            moveNum = moveCount + ". ";
        }
        
        String pieceSymbol = piece.getSymbol();
        String fromSquare = positionToNotation(from);
        String toSquare = positionToNotation(to);
        
        Piece captured = board.getPiece(to.row, to.col);
        String captureSymbol = (captured != null) ? "x" : "-";
        
        return moveNum + pieceSymbol + fromSquare + captureSymbol + toSquare;
    }
    
    private String positionToNotation(Position pos) {
        char file = (char) ('a' + pos.col);
        int rank = 8 - pos.row;
        return "" + file + rank;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw board squares
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                drawSquare(g2d, row, col);
            }
        }
        
        // Draw coordinates
        drawCoordinates(g2d);
        
        // Draw pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null) {
                    drawPiece(g2d, piece, row, col);
                }
            }
        }
    }
    
    private void drawSquare(Graphics2D g2d, int row, int col) {
        boolean isLight = (row + col) % 2 == 0;
        Color squareColor = isLight ? LIGHT_SQUARE : DARK_SQUARE;
        
        Position pos = new Position(row, col);
        if (pos.equals(selectedSquare)) {
            squareColor = SELECTED_COLOR;
        }
        
        g2d.setColor(squareColor);
        g2d.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        
        // Highlight valid moves
        if (validMoves.contains(pos)) {
            g2d.setColor(VALID_MOVE_COLOR);
            if (board.getPiece(row, col) != null) {
                // Draw ring for capture
                g2d.setStroke(new BasicStroke(4));
                g2d.drawOval(col * SQUARE_SIZE + 5, row * SQUARE_SIZE + 5, 
                           SQUARE_SIZE - 10, SQUARE_SIZE - 10);
            } else {
                // Draw circle for empty square
                g2d.fillOval(col * SQUARE_SIZE + SQUARE_SIZE/2 - 10, 
                           row * SQUARE_SIZE + SQUARE_SIZE/2 - 10, 20, 20);
            }
        }
    }
    
    private void drawCoordinates(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.BLACK);
        
        // Draw file letters (a-h)
        for (int col = 0; col < 8; col++) {
            char file = (char) ('a' + col);
            g2d.drawString(String.valueOf(file), col * SQUARE_SIZE + SQUARE_SIZE - 15, 
                         8 * SQUARE_SIZE - 5);
        }
        
        // Draw rank numbers (1-8)
        for (int row = 0; row < 8; row++) {
            int rank = 8 - row;
            g2d.drawString(String.valueOf(rank), 5, row * SQUARE_SIZE + 15);
        }
    }
    
    private void drawPiece(Graphics2D g2d, Piece piece, int row, int col) {
        String symbol = piece.getUnicodeSymbol();
        g2d.setFont(new Font("Serif", Font.PLAIN, 50));
        
        FontMetrics fm = g2d.getFontMetrics();
        int x = col * SQUARE_SIZE + (SQUARE_SIZE - fm.stringWidth(symbol)) / 2;
        int y = row * SQUARE_SIZE + (SQUARE_SIZE + fm.getAscent()) / 2 - 5;
        
        // Draw shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.drawString(symbol, x + 2, y + 2);
        
        // Draw piece
        g2d.setColor(piece.isWhite() ? Color.WHITE : Color.BLACK);
        g2d.drawString(symbol, x, y);
        
        // Draw outline
        g2d.setColor(piece.isWhite() ? Color.BLACK : Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.setFont(new Font("Serif", Font.PLAIN, 50));
    }
    
    public void refresh() {
        selectedSquare = null;
        validMoves.clear();
        repaint();
    }
}