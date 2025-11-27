/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartchess;

import java.io.Serializable;

public class Position implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public final int row;
    public final int col;
    
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && col == position.col;
    }
    
    @Override
    public int hashCode() {
        return 31 * row + col;
    }
    
    @Override
    public String toString() {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }
}