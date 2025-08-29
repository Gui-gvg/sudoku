package br.com.dio.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {
    private final List<List<Space>> spaces;
    private GameStatus status;

    public enum GameStatus {
        IN_PROGRESS("Em Andamento"),
        COMPLETED("Concluído"),
        WITH_ERRORS("Com Erros");

        private final String label;

        GameStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public Board(List<List<Space>> spaces) {
        this.spaces = spaces;
        this.status = GameStatus.IN_PROGRESS;
        updateStatus();
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatus getStatus() {
        return status;
    }

    public boolean changeValue(int col, int row, int value) {
        Space space = spaces.get(col).get(row);
        if (space.isFixed()) {
            return false;
        }

        // Verifica se o movimento é válido
        if (!isValidMove(col, row, value)) {
            return false;
        }

        space.setActual(value);
        updateStatus();
        return true;
    }

    public boolean clearValue(int col, int row) {
        Space space = spaces.get(col).get(row);
        if (space.isFixed()) {
            return false;
        }
        space.clear();
        updateStatus();
        return true;
    }

    public void reset() {
        for (List<Space> row : spaces) {
            for (Space space : row) {
                if (!space.isFixed()) {
                    space.clear();
                }
            }
        }
        updateStatus();
    }

    public boolean hasErrors() {
        return status == GameStatus.WITH_ERRORS;
    }

    public boolean gameIsFinished() {
        return status == GameStatus.COMPLETED;
    }

    private boolean isValidMove(int col, int row, int value) {
        // Verifica linha
        for (int c = 0; c < 9; c++) {
            if (c != col && spaces.get(c).get(row).getActual() != null && 
                spaces.get(c).get(row).getActual() == value) {
                return false;
            }
        }

        // Verifica coluna
        for (int r = 0; r < 9; r++) {
            if (r != row && spaces.get(col).get(r).getActual() != null && 
                spaces.get(col).get(r).getActual() == value) {
                return false;
            }
        }

        // Verifica quadrante 3x3
        int quadrantColStart = (col / 3) * 3;
        int quadrantRowStart = (row / 3) * 3;
        
        for (int c = quadrantColStart; c < quadrantColStart + 3; c++) {
            for (int r = quadrantRowStart; r < quadrantRowStart + 3; r++) {
                if (c != col && r != row && spaces.get(c).get(r).getActual() != null && 
                    spaces.get(c).get(r).getActual() == value) {
                    return false;
                }
            }
        }

        return true;
    }

    private void updateStatus() {
        boolean hasErrors = false;
        boolean allFilled = true;

        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 9; row++) {
                Space space = spaces.get(col).get(row);
                
                // Verifica se está preenchido
                if (space.getActual() == null) {
                    allFilled = false;
                    continue;
                }

                // Verifica se é válido (não viola regras)
                if (!isValidMove(col, row, space.getActual())) {
                    hasErrors = true;
                }
            }
        }

        if (hasErrors) {
            status = GameStatus.WITH_ERRORS;
        } else if (allFilled) {
            status = GameStatus.COMPLETED;
        } else {
            status = GameStatus.IN_PROGRESS;
        }
    }

    public boolean validateCompleteSolution() {
        // Verifica se todas as células estão preenchidas corretamente
        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 9; row++) {
                Space space = spaces.get(col).get(row);
                if (space.getActual() == null || !isValidMove(col, row, space.getActual())) {
                    return false;
                }
            }
        }
        return true;
    }
}
