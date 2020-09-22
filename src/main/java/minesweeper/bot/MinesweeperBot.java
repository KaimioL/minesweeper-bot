
package minesweeper.bot;

import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.ArrayList;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Highlight;
import minesweeper.model.Pair;
import minesweeper.model.Square;

public class MinesweeperBot implements Bot {

    private Random rng = new Random();
    private GameStats gameStats;
    private Deque<Square> flag_s = new ArrayDeque<Square>();
    private Deque<Square> open_s = new ArrayDeque<Square>();

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

        HashSet<Square> opened_squares = board.getOpenSquares(); 

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
        
        return new Move(MoveType.FLAG, 1, 1);
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
        this.gameStats = gameStats;
    }

    /**
     * Find the (X, Y) coordinate pair of an unopened square
     * from the current board
     * @param board The current board state
     * @return An (X, Y) coordinate pair
     */

    public Pair<Integer> findUnopenedSquare(Board board) {
        Boolean unOpenedSquare = false;

        // board.getOpenSquares allows access to already opened squares
        HashSet<Square> opened = board.getOpenSquares();
        int x;
        int y;

        Pair<Integer> pair = new Pair<>(0, 0);

        // Randomly generate X,Y coordinate pairs that are not opened
        while (!unOpenedSquare) {
            x = rng.nextInt(board.width);
            y = rng.nextInt(board.height);
            if (!opened.contains(board.board[x][y])) {
                unOpenedSquare = true;
                pair = new Pair<Integer>(x, y);
            }
        }

        // This pair should point to an unopened square now
        return pair;
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
}
