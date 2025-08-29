package br.com.dio;

import br.com.dio.model.Board;
import br.com.dio.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import static br.com.dio.util.BoardTemplate.BOARD_TEMPLATE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);
    private static Board board;
    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        final var positions = Stream.of(args)
                .collect(toMap(
                        k -> k.split(";")[0],
                        v -> v.split(";")[1]
                ));
        
        var option = -1;
        while (true) {
            printMenu();
            option = scanner.nextInt();

            switch (option) {
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opção inválida, selecione uma das opções do menu");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== SUDOKU ===");
        System.out.println("1 - Iniciar um novo Jogo");
        System.out.println("2 - Colocar um novo número");
        System.out.println("3 - Remover um número");
        System.out.println("4 - Visualizar jogo atual");
        System.out.println("5 - Verificar status do jogo");
        System.out.println("6 - Limpar jogo");
        System.out.println("7 - Finalizar jogo");
        System.out.println("8 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)) {
            System.out.println("O jogo já foi iniciado");
            return;
        }

        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                var positionConfig = positions.get("%s,%s".formatted(i, j));
                if (positionConfig == null) {
                    // Célula vazia não fixa
                    spaces.get(i).add(new Space(null, false));
                } else {
                    var parts = positionConfig.split(",");
                    var expected = parts[0].equals("null") ? null : Integer.parseInt(parts[0]);
                    var fixed = Boolean.parseBoolean(parts[1]);
                    spaces.get(i).add(new Space(expected, fixed));
                }
            }
        }

        board = new Board(spaces);
        System.out.println("O jogo está pronto para começar!");
    }

    private static void inputNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.println("Informe a coluna (0-8):");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha (0-8):");
        var row = runUntilGetValidNumber(0, 8);
        System.out.printf("Informe o número (1-9) para a posição [%s,%s]:\n", col, row);
        var value = runUntilGetValidNumber(1, 9);
        
        if (board.changeValue(col, row, value)) {
            System.out.printf("Número %d colocado na posição [%s,%s]\n", value, col, row);
        } else {
            System.out.printf("Não foi possível colocar o número na posição [%s,%s]. ");
            System.out.println("Pode ser célula fixa ou movimento inválido.");
        }
    }

    private static void removeNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.println("Informe a coluna (0-8):");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha (0-8):");
        var row = runUntilGetValidNumber(0, 8);
        
        if (board.clearValue(col, row)) {
            System.out.printf("Número removido da posição [%s,%s]\n", col, row);
        } else {
            System.out.printf("Não foi possível remover o número da posição [%s,%s] (célula fixa)\n", col, row);
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        var args = new Object[81];
        var argPos = 0;
        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (int col = 0; col < BOARD_LIMIT; col++) {
                Space space = board.getSpaces().get(col).get(row);
                String displayValue = " ";
                if (space.getActual() != null) {
                    displayValue = String.valueOf(space.getActual());
                    if (space.isFixed()) {
                        displayValue = "\u001B[1m" + displayValue + "\u001B[0m"; // Negrito para fixos
                    }
                }
                args[argPos++] = displayValue;
            }
        }
        
        System.out.println("\nTabuleiro atual:");
        System.out.printf(BOARD_TEMPLATE + "\n", args);
    }

    private static void showGameStatus() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.printf("Status: %s\n", board.getStatus().getLabel());
        if (board.hasErrors()) {
            System.out.println("⚠️  O jogo contém erros (números repetidos)");
        } else if (board.gameIsFinished()) {
            System.out.println("✅ Parabéns! Jogo concluído corretamente!");
        } else {
            System.out.println("➡️  Continue preenchendo o tabuleiro");
        }
    }

    private static void clearGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo? (sim/não)");
        scanner.nextLine(); // Limpar buffer
        var confirm = scanner.nextLine().trim().toLowerCase();
        
        while (!confirm.equals("sim") && !confirm.equals("não") && !confirm.equals("nao")) {
            System.out.println("Informe 'sim' ou 'não'");
            confirm = scanner.nextLine().trim().toLowerCase();
        }

        if (confirm.equals("sim")) {
            board.reset();
            System.out.println("Jogo limpo. Todos os números não-fixos foram removidos.");
        }
    }

    private static void finishGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        if (board.gameIsFinished()) {
            System.out.println("🎉 Parabéns! Você concluiu o jogo corretamente!");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("❌ Seu jogo contém erros. Corrija os números repetidos.");
            showCurrentGame();
        } else {
            System.out.println("📝 Você ainda precisa preencher algumas células.");
            showCurrentGame();
        }
    }

    private static int runUntilGetValidNumber(final int min, final int max) {
        while (true) {
            try {
                var current = scanner.nextInt();
                if (current >= min && current <= max) {
                    return current;
                }
                System.out.printf("Informe um número entre %s e %s:\n", min, max);
            } catch (Exception e) {
                System.out.printf("Entrada inválida. Informe um número entre %s e %s:\n", min, max);
                scanner.next(); // Limpar input inválido
            }
        }
    }
}
