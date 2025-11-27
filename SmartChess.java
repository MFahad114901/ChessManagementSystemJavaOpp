package smartchess;

// SmartChess.java - Main Application Entry Point
import javax.swing.*;

public class SmartChess {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ChessGUI();
        });
    }
}