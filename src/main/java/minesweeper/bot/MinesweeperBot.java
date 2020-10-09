
package minesweeper.bot;

// import java.util.HashSet;
// import java.util.ArrayDeque;
import java.util.ArrayList;
// import java.util.HashSet;
import minesweeper.collections.ArrayDeque;
import minesweeper.collections.HashSet;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Highlight;
import minesweeper.model.Pair;
import minesweeper.model.Square;    

public class MinesweeperBot implements Bot {
    public ArrayDeque<Square> flag_s = new ArrayDeque<Square>();
    public ArrayDeque<Square> open_s = new ArrayDeque<Square>();

    private int[][] odds;
    private int combination_count;

    /**
     * Make a single decision based on the given Board state
     * @param board The current board state
     * @return Move to be made onto the board
     */
    @Override
    public Move makeMove(Board board) {
        if(board.firstMove){
            return new Move(MoveType.OPEN, 5, 5);
        }

        //Flag all squares in flagging que
        if(flag_s.size() != 0) {
            Square temp_sq = flag_s.pop();
            return new Move(MoveType.FLAG, temp_sq.getX(), temp_sq.getY());
        }

        //Open all squares in opening que
        if(open_s.size() != 0) {
            Square temp_sq = open_s.pop();
            return new Move(MoveType.OPEN, temp_sq.getX(), temp_sq.getY());
        }

        HashSet<Square> opened_squares = new HashSet(board.getOpenSquares().toArray());
        // HashSet<Square> opened_squares = board.getOpenSquares(); 

        for(Square square : opened_squares) {
            if(!(square.surroundingMines() == 0)) {
                //If square value is same than near undiscovered squares, push all near undicovered squares into flagging que
                if(square.surroundingMines() == amountOfUndiscoveredSquaresNear(square, board)) {
                    flagNearSquares(square, board);
                    if(flag_s.size() != 0) {
                        Square temp_sq = flag_s.pop();
                        return new Move(MoveType.FLAG, temp_sq.getX(), temp_sq.getY());
                    }
                }
                //If square value and amount near flags are equal, open all near undiscovered nonflagged squares
                if(square.surroundingMines() == amountOfFlaggedSquaresNear(square, board)) {
                    openNearSquares(square, board);
                    if(open_s.size() != 0) {
                        Square temp_sq = open_s.pop();
                        return new Move(MoveType.OPEN, temp_sq.getX(), temp_sq.getY());
                    }
                }
            }
        }
        
        ArrayDeque<Square> fringe_squares = findFringeSquares(board);
        odds = new int[board.width][board.height];

        combination_count = 0;
        findMineSubsets(board, fringe_squares, new HashSet<Square>(), new HashSet<Square>());

        int max = 0;

        Square max_square = board.getSquareAt(0, 0);

        for(int i = 0; i < board.width; i++) {
            for(int j = 0; j < board.height; j++) {
                if(odds[i][j] ==  combination_count) {
                    return new Move(MoveType.FLAG, i, j);
                }
                if(odds[i][j] > max) {
                    max = odds[i][j];
                    max_square = board.getSquareAt(i, j);
                }
            }
        }

        return new Move(MoveType.FLAG, max_square.getX(), max_square.getY());
    }

    /**
     * Return multiple possible moves to make based on current board state.
     * Suggested to be used for a "helper" bot to provide multiple highlights at once.
     * @param board The current board state.
     * @return List of moves for current board.
     */
    @Override
    public ArrayList<Move> getPossibleMoves(Board board) {
        ArrayList<Move> movesToMake = new ArrayList<>();
        return movesToMake;
    }

    /**
     * Used to pass the bot the gameStats object, useful for tracking previous moves
     */
    @Override
    public void setGameStats(GameStats gameStats) {
    }
    
    /**
     * Checks amount of undiscovered squares near given square
     * @param square The inspected square
     * @param board The current board
     * @return Amount of near undiscovered squares
     */
    public Integer amountOfUndiscoveredSquaresNear(Square square, Board board) {
        int count = 0;

        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                int x = square.getX()+i;
                int y = square.getY()+j;
                if(x < 0 || x == board.width) continue;
                if(y < 0 || y == board.height) continue;
                Square temp_sq= board.getSquareAt(x, y);
                if(!temp_sq.isOpened()) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Checks amount of undiscovered squares near given square, used in backtracking
     * @param square The inspected square
     * @param board The current board
     * @param opened_squares Opened squares in backtracking search
     * @return Amount of near undiscovered squares
     */
    public Integer amountOfUndiscoveredSquaresNear(Square square, Board board, HashSet<Square> opened_squares) {
        int count = 0;

        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                int x = square.getX()+i;
                int y = square.getY()+j;
                if(x < 0 || x == board.width) continue;
                if(y < 0 || y == board.height) continue;
                Square temp_sq= board.getSquareAt(x, y);
                if(!temp_sq.isOpened()) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Pushes near undiscovered squares to flagging queue
     * @param square The inspected square
     * @param board The current board
     */
    public void flagNearSquares(Square square, Board board) {
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                int x = square.getX()+i;
                int y = square.getY()+j;
                if(x < 0 || x == board.width) continue;
                if(y < 0 || y == board.height) continue;
                Square temp_sq= board.getSquareAt(x, y);
                if(!temp_sq.isOpened()) {
                    if(!temp_sq.isFlagged()) {
                        flag_s.push(temp_sq);
                    }
                }
            }
        }
    }

    /**
     * Returns flagged_squares with new flagged squares added, used in backtracking
     * @param square The inspected square
     * @param board The current board
     * @param opened_squares Opened squares in backtracking search
     * @param flagged_squares Flagged squares in backtracking search
     * @return Hashset of flagged squares
     */
    public HashSet<Square> flagNearSquares(Square square, Board board, HashSet<Square> opened_squares, HashSet<Square> flagged_squares) {
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                int x = square.getX()+i;
                int y = square.getY()+j;
                if(x < 0 || x == board.width) continue;
                if(y < 0 || y == board.height) continue;
                Square temp_sq= board.getSquareAt(x, y);
                if(!temp_sq.isOpened() && !opened_squares.contains(temp_sq)) {
                    if(!temp_sq.isFlagged() && !flagged_squares.contains(temp_sq)) {
                        flagged_squares.add(temp_sq);
                    }
                }
            }
        }

        return flagged_squares;
    }

    /**
     * Checks amount of flagged squares near given square
     * @param square The inspected square
     * @param board The current board
     * @return Amount of near flagged squares
     */
    public Integer amountOfFlaggedSquaresNear(Square square, Board board) {
        int count = 0;
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                int x = square.getX()+i;
                int y = square.getY()+j;
                if(x < 0 || x == board.width) continue;
                if(y < 0 || y == board.height) continue;
                Square temp_sq= board.getSquareAt(x, y);
                if(temp_sq.isFlagged()) {
                    count++;
                } 
                // else if (!temp_sq.isFlagged() && !temp_sq.isOpened()){
                //     open_s.push(temp_sq);
                // }
            }
        }

        return count;
    }

    /**
     * Checks amount of flagged squares near given square, used in backtracking
     * @param square The inspected square
     * @param board The current board
     * @param flagged_squares Flagged squares in backtracking search
     * @return Amount of near flagged squares
     */
    public Integer amountOfFlaggedSquaresNear(Square square, Board board, HashSet<Square> flagged_squares) {
        int count = 0;
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                int x = square.getX()+i;
                int y = square.getY()+j;
                if(x < 0 || x == board.width) continue;
                if(y < 0 || y == board.height) continue;
                Square temp_sq= board.getSquareAt(x, y);
                if(temp_sq.isFlagged() || flagged_squares.contains(temp_sq)) {
                    count++;
                } 
                // else if (!temp_sq.isFlagged() && !temp_sq.isOpened()){
                //     open_s.push(temp_sq);
                // }
            }
        }

        return count;
    }

    /**
     * Pushes near nonflagged undiscovered squares to opening queue
     * @param square The inspected square
     * @param board The current board
     */
    public void openNearSquares(Square square, Board board) {
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                int x = square.getX()+i;
                int y = square.getY()+j;
                if(x < 0 || x == board.width) continue;
                if(y < 0 || y == board.height) continue;
                Square temp_sq= board.getSquareAt(x, y);
                if (!temp_sq.isFlagged() && !temp_sq.isOpened()){
                    open_s.push(temp_sq);
                }
            }
        }
    }

    /**
     * Returns opened_square with new opened squares added, used in backtracking
     * @param square The inspected square
     * @param board The current board
     * @param opened_squares Opened squares in backtracking search
     * @param flagged_squares Flagged squares in backtracking search
     * @return Hashset of flagged squares
     */
    public HashSet<Square> openNearSquares(Square square, Board board, HashSet<Square> opened_squares, HashSet<Square> flagged_squares) {
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                int x = square.getX()+i;
                int y = square.getY()+j;
                if(x < 0 || x == board.width) continue;
                if(y < 0 || y == board.height) continue;
                Square temp_sq= board.getSquareAt(x, y);
                if (!temp_sq.isFlagged() && !temp_sq.isOpened() && !opened_squares.contains(temp_sq) && !flagged_squares.contains(temp_sq)){
                    opened_squares.add(temp_sq);
                }
            }
        }

        return opened_squares;
    }

    /**
     * Find unopened squares which have adjent opened squares
     * @param board The current board
     * @return Unopened squares near opened squares
     */
    public ArrayDeque<Square> findFringeSquares(Board board) {
        HashSet<Square> opened_squares = new HashSet(board.getOpenSquares().toArray());
        // HashSet<Square> opened_squares = board.getOpenSquares();

        ArrayDeque<Square> fringe_squares = new ArrayDeque<Square>();
        HashSet<Square> fringe_square_checker = new HashSet<Square>();
        for(Square square : opened_squares) {
            for(int i = -1; i < 2; i++){
                for(int j = -1; j < 2; j++){
                    int x = square.getX()+i;
                    int y = square.getY()+j;
                    if(x < 0 || x == board.width) continue;
                    if(y < 0 || y == board.height) continue;
                    Square temp_sq= board.getSquareAt(x, y);
                    if (!temp_sq.isFlagged() && !temp_sq.isOpened() && !fringe_square_checker.contains(temp_sq)){
                        fringe_squares.push(temp_sq);
                        fringe_square_checker.add(temp_sq);
                    }
                }
            }
        }

        return fringe_squares;
    }

    public void findMineSubsets(Board board, ArrayDeque<Square> fringe_squares, HashSet<Square> opened_squares, HashSet<Square> flagged_squares) {
        
        if(fringe_squares.isEmpty()) {
            for(Square temp : flagged_squares) odds[temp.getX()][temp.getY()]++;
            combination_count++;
            return;
        }

        Square square = fringe_squares.pop();
        
        while(opened_squares.contains(square) || flagged_squares.contains(square)){
            if(fringe_squares.isEmpty()) {
                for(Square temp : flagged_squares) odds[temp.getX()][temp.getY()]++;
                combination_count++;
                return;
            }
            square = fringe_squares.pop();
        }

        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                int x = square.getX()+i;
                int y = square.getY()+j;
                if(x < 0 || x == board.width) continue;
                if(y < 0 || y == board.height) continue;
                Square temp_sq = board.getSquareAt(x, y);
                if (temp_sq.isOpened()){
                    if(temp_sq.surroundingMines() == amountOfUndiscoveredSquaresNear(square, board, opened_squares)) {
                        flagged_squares = flagNearSquares(square, board, opened_squares, flagged_squares);
                    }
                    if(temp_sq.surroundingMines() == amountOfFlaggedSquaresNear(square, board, flagged_squares)) {
                        opened_squares = openNearSquares(square, board, opened_squares, flagged_squares);
                    }
                }
            }
        }

        if(flagged_squares.contains(square) || opened_squares.contains(square)) {
            findMineSubsets(board, fringe_squares, opened_squares, flagged_squares);
            return;
        }

        HashSet<Square> temp_opened_squares = new HashSet<Square>(opened_squares);
        temp_opened_squares.add(square);
        HashSet<Square> temp_flagged_squares = new HashSet<Square>(flagged_squares);
        ArrayDeque<Square> temp_fringe_squares = new ArrayDeque<Square>(fringe_squares);

        findMineSubsets(board, temp_fringe_squares, temp_opened_squares, temp_flagged_squares);
        
        flagged_squares.add(square);
        findMineSubsets(board, fringe_squares, opened_squares, flagged_squares);
    }
}
