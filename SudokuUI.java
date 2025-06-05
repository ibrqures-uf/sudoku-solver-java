import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class SudokuUI {

    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final Random random = new Random();
    private static char[][] board = new char[SIZE][SIZE];

    private static JTextField[][] textFields = new JTextField[SIZE][SIZE];
    private static boolean[][] isUserFilled = new boolean[SIZE][SIZE];

    private static boolean isSafe(int row, int col, char[][] board, int number) {
        char numChar = (char) (number + '0');
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == numChar || board[i][col] == numChar) {
                return false;
            }
        }
        int startRow = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int startCol = (col / SUBGRID_SIZE) * SUBGRID_SIZE;
        for (int i = startRow; i < startRow + SUBGRID_SIZE; i++) {
            for (int j = startCol; j < startCol + SUBGRID_SIZE; j++) {
                if (board[i][j] == numChar) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean solveSudoku(char[][] board, int row, int col) {
        if (row == SIZE) {
            return true;
        }
        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SIZE - 1) ? 0 : col + 1;
        if (board[row][col] != '.') {
            return solveSudoku(board, nextRow, nextCol);
        }
        int[] numbers = getShuffledNumbers();
        for (int num : numbers) {
            if (isSafe(row, col, board, num)) {
                board[row][col] = (char) (num + '0');
                if (solveSudoku(board, nextRow, nextCol)) {
                    return true;
                }
                board[row][col] = '.'; // Backtrack
            }
        }
        return false;
    }

    private static int[] getShuffledNumbers() {
        int[] numbers = new int[9];
        for (int i = 0; i < 9; i++) {
            numbers[i] = i + 1;
        }
        for (int i = 0; i < numbers.length; i++) {
            int randomIndex = random.nextInt(numbers.length);
            int temp = numbers[i];
            numbers[i] = numbers[randomIndex];
            numbers[randomIndex] = temp;
        }
        return numbers;
    }

    private static void generateCompleteBoard(char[][] board) {
        solveSudoku(board, 0, 0);
    }

public static void generateRandomBoard() {
    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            board[i][j] = '.';
            isUserFilled[i][j] = false;
        }
    }
    generateCompleteBoard(board);
    int cellsToRemove = 40;
    while (cellsToRemove > 0) {
        int row = random.nextInt(SIZE);
        int col = random.nextInt(SIZE);
        if (board[row][col] != '.') {
            board[row][col] = '.';
            cellsToRemove--;
        }
    }

    // After generating board, mark user-filled cells (non-empty ones)
    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            isUserFilled[i][j] = board[i][j] != '.';
        }
    }

    updateUI();
}


private static void updateUI() {
    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            if (board[i][j] == '.') {
                textFields[i][j].setText("");
                textFields[i][j].setForeground(Color.WHITE);
            } else {
                textFields[i][j].setText(String.valueOf(board[i][j]));
                textFields[i][j].setForeground(isUserFilled[i][j] ? Color.WHITE : Color.CYAN);
            }
        }
    }
}


    private static boolean isValidBoard(char[][] board) {
        for (int i = 0; i < SIZE; i++) {
            boolean[] rowCheck = new boolean[SIZE];
            boolean[] colCheck = new boolean[SIZE];

            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != '.' && rowCheck[board[i][j] - '1']) {
                    return false; // Duplicate in row
                }
                rowCheck[board[i][j] - '1'] = true;

                if (board[j][i] != '.' && colCheck[board[j][i] - '1']) {
                    return false; // Duplicate in column
                }
                colCheck[board[j][i] - '1'] = true;
            }
        }

        for (int blockRow = 0; blockRow < SUBGRID_SIZE; blockRow++) {
            for (int blockCol = 0; blockCol < SUBGRID_SIZE; blockCol++) {
                boolean[] gridCheck = new boolean[SIZE];
                for (int i = 0; i < SUBGRID_SIZE; i++) {
                    for (int j = 0; j < SUBGRID_SIZE; j++) {
                        char num = board[blockRow * SUBGRID_SIZE + i][blockCol * SUBGRID_SIZE + j];
                        if (num != '.' && gridCheck[num - '1']) {
                            return false; // Duplicate in 3x3 grid
                        }
                        gridCheck[num - '1'] = true;
                    }
                }
            }
        }

        return true; // All checks passed
    }


private static void solveBoard() {
    char[][] boardArray = new char[SIZE][SIZE];
    boolean[][] isOriginal = new boolean[SIZE][SIZE];

    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            String text = textFields[i][j].getText().trim();

            if (text.isEmpty()) {
                boardArray[i][j] = '.';
                isOriginal[i][j] = false;
            } else if (text.matches("[1-9]")) {
                boardArray[i][j] = text.charAt(0);
                isOriginal[i][j] = true;
            } else {
                JOptionPane.showMessageDialog(null,
                        "Invalid input at cell (" + (i + 1) + "," + (j + 1) + "). Please enter digits 1-9 only.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    if (solveSudoku(boardArray, 0, 0)) {
        board = boardArray;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                textFields[i][j].setText(String.valueOf(board[i][j]));

                if (!isOriginal[i][j]) {
                    // Style for newly solved numbers (e.g., green and italic)
                    textFields[i][j].setForeground(Color.GREEN);
                    textFields[i][j].setFont(new Font("Arial", Font.ITALIC, 20));
                } else {
                    // Retain style for original numbers (white and bold)
                    textFields[i][j].setForeground(Color.WHITE);
                    textFields[i][j].setFont(new Font("Arial", Font.BOLD, 20));
                }
            }
        }

    } else {
        JOptionPane.showMessageDialog(null, "No solution found for the current board.");
    }
}



private static void checkSolution() {
    char[][] boardArray = new char[SIZE][SIZE];

    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            String text = textFields[i][j].getText().trim();

            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Cell (" + (i+1) + "," + (j+1) + ") is empty. Please fill all cells before checking the solution.",
                        "Empty Cell Detected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            } else if (text.matches("[1-9]")) {
                boardArray[i][j] = text.charAt(0);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Invalid input at cell (" + (i+1) + "," + (j+1) + "). Please enter digits 1-9 only.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    if (isValidBoard(boardArray)) {
        JOptionPane.showMessageDialog(null, "Yayyy! The solution is correct");
    } else {
        JOptionPane.showMessageDialog(null, "Oops! The solution is incorrect. Please try again.");
    }
}



public static void createAndShowGUI() {
    JFrame frame = new JFrame("Sudoku Solver");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());

    // Set overall frame background to black
    frame.getContentPane().setBackground(Color.BLACK);

    JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));
    gridPanel.setBackground(Color.BLACK); // Optional, makes grid background black

    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            textFields[i][j] = new JTextField(2);
            textFields[i][j].setHorizontalAlignment(JTextField.CENTER);
            textFields[i][j].setFont(new Font("Arial", Font.BOLD, 20));
            textFields[i][j].setForeground(Color.WHITE);  // white text
            textFields[i][j].setBackground(Color.BLACK);  // black background
            textFields[i][j].setCaretColor(Color.WHITE);  // white cursor
            gridPanel.add(textFields[i][j]);
        }
    }

    // Orange buttons with white text
    Color buttonOrange = new Color(255, 140, 0);

    JButton generateButton = new JButton("Generate Random Board");
    generateButton.setBackground(buttonOrange);
    generateButton.setForeground(Color.WHITE);
    generateButton.setFocusPainted(false);
    generateButton.setFont(new Font("Arial", Font.BOLD, 14));
    generateButton.addActionListener(e -> generateRandomBoard());

    JButton solveButton = new JButton("Solve");
    solveButton.setBackground(buttonOrange);
    solveButton.setForeground(Color.WHITE);
    solveButton.setFocusPainted(false);
    solveButton.setFont(new Font("Arial", Font.BOLD, 14));
    solveButton.addActionListener(e -> solveBoard());

    JButton checkButton = new JButton("Check Solution");
    checkButton.setBackground(buttonOrange);
    checkButton.setForeground(Color.WHITE);
    checkButton.setFocusPainted(false);
    checkButton.setFont(new Font("Arial", Font.BOLD, 14));
    checkButton.addActionListener(e -> checkSolution());

    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.BLACK); // black background for buttons panel
    buttonPanel.add(generateButton);
    buttonPanel.add(solveButton);
    buttonPanel.add(checkButton);

    frame.add(gridPanel, BorderLayout.CENTER);
    frame.add(buttonPanel, BorderLayout.SOUTH);

    frame.pack();
    frame.setVisible(true);
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}