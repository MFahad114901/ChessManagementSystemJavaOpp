
package smartchess;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ChessGUI extends JFrame {
    private ChessBoard board;
    private BoardPanel boardPanel;
    private JPanel infoPanel;
    private JLabel statusLabel;
    private JLabel turnLabel;
    private JTextArea moveHistory;
    private JButton newGameButton;
    private JButton undoButton;
    private JButton saveButton;
    private JButton loadButton;
     
    public ChessGUI() {
        setTitle("SmartChess - Intelligent Chess Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Initialize chess board
        board = new ChessBoard();
        
        // Create components
        createBoardPanel();
        createInfoPanel();
        createMenuBar();
        
        // Add components
        add(boardPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void createBoardPanel() {
        boardPanel = new BoardPanel(board, this);
    }
    
    private void createInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(250, 600));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("SmartChess");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Turn indicator
        turnLabel = new JLabel("Turn: White");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(turnLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Status label
        statusLabel = new JLabel("Select a piece to move");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(statusLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> newGame());
        buttonPanel.add(newGameButton);
        
        undoButton = new JButton("Undo Move");
        undoButton.addActionListener(e -> undoMove());
        buttonPanel.add(undoButton);
        
        saveButton = new JButton("Save Game");
        saveButton.addActionListener(e -> saveGame());
        buttonPanel.add(saveButton);
        
        loadButton = new JButton("Load Game");
        loadButton.addActionListener(e -> loadGame());
        buttonPanel.add(loadButton);
        
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Move history
        JLabel historyLabel = new JLabel("Move History:");
        historyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        historyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(historyLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        moveHistory = new JTextArea(20, 20);
        moveHistory.setEditable(false);
        moveHistory.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(moveHistory);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(scrollPane);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> newGame());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        gameMenu.add(newGameItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        JMenuItem rulesItem = new JMenuItem("Chess Rules");
        rulesItem.addActionListener(e -> showRules());
        helpMenu.add(aboutItem);
        helpMenu.add(rulesItem);
        
        menuBar.add(gameMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }
    
    public void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    public void updateTurn() {
        turnLabel.setText("Turn: " + (board.isWhiteTurn() ? "White" : "Black"));
    }
    
    public void addMoveToHistory(String move) {
        moveHistory.append(move + "\n");
        moveHistory.setCaretPosition(moveHistory.getDocument().getLength());
    }
    
    private void newGame() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Start a new game?", "New Game",
            JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            board.initializeBoard();
            boardPanel.refresh();
            moveHistory.setText("");
            updateTurn();
            updateStatus("New game started");
        }
    }
    
    private void undoMove() {
        if (board.undoLastMove()) {
            boardPanel.refresh();
            updateTurn();
            updateStatus("Move undone");
            // Remove last line from history
            String text = moveHistory.getText();
            int lastNewline = text.lastIndexOf('\n', text.length() - 2);
            if (lastNewline > 0) {
                moveHistory.setText(text.substring(0, lastNewline + 1));
            }
        } else {
            JOptionPane.showMessageDialog(this, "No moves to undo!");
        }
    }
    
    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getAbsolutePath();
            if (board.saveGame(filename)) {
                JOptionPane.showMessageDialog(this, "Game saved successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error saving game!");
            }
        }
    }
    
    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getAbsolutePath();
            if (board.loadGame(filename)) {
                boardPanel.refresh();
                updateTurn();
                JOptionPane.showMessageDialog(this, "Game loaded successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error loading game!");
            }
        }
    }
    
    private void showAbout() {
        JOptionPane.showMessageDialog(this,
            "SmartChess v1.0\n" +
            "An Intelligent Chess Management System\n\n" +
            "Developed with Java OOP\n" +
            "Features:\n" +
            "- Full chess rules implementation\n" +
            "- Move validation\n" +
            "- Check and checkmate detection\n" +
            "- Save/Load games\n" +
            "- Move history\n" +
            "- Undo functionality",
            "About SmartChess",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showRules() {
        JOptionPane.showMessageDialog(this, """
                                            Basic Chess Rules:
                                            
                                            - Each piece moves in a specific pattern
                                            - Capture opponent pieces by moving to their square
                                            - Protect your King from check
                                            - Checkmate wins the game
                                            - Stalemate results in a draw
                                            
                                            Special Moves:
                                            - Castling: King moves 2 squares toward Rook
                                            - En Passant: Special pawn capture
                                            - Pawn Promotion: Pawn reaches opposite end""",
            "Chess Rules",
            JOptionPane.INFORMATION_MESSAGE);
    }
}