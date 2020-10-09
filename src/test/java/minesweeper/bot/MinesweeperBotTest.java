
package minesweeper.bot;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import minesweeper.bot.Bot;
import minesweeper.bot.BotSelect;
import minesweeper.bot.MinesweeperBot;
import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.Move;
import minesweeper.model.Highlight;
import minesweeper.model.MoveType;
import minesweeper.model.Square;
import minesweeper.collections.ArrayDeque;
import minesweeper.collections.HashSet;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MinesweeperBotTest {
    private MinesweeperBot bot;
    private MinefieldGenerator generator;
    private Board board;
    @Before
    public void setUp() {
        // this.bot = BotSelect.getBot();
        this.bot = new MinesweeperBot();
        this.generator = new MinefieldGenerator();
        this.board = new Board(generator, 10, 10, 3);
    }
    
    @After
    public void tearDown() {
    // empty method
    }

    @Test
    public void testBotCanMakeValidMoves() {
        Move move = this.bot.makeMove(this.board);
        assertTrue(move.x >= 0 && move.x < 10);
        assertTrue(move.y >= 0 && move.y < 10);
    }

    @Test
    public void testBotCanProvideListOfValidMoves() {
        ArrayList<Move> moves = this.bot.getPossibleMoves(this.board);
        for (Move m : moves) {
            assertTrue(m.x >= 0 && m.x < 10);
            assertTrue(m.y >= 0 && m.y < 10);
            assertTrue(m.highlight != Highlight.NONE);
        }
    }

    @Test
    public void testFirstMoveIsAlwaysMadeOnOpenFiveFive() {
        Move move = this.bot.makeMove(this.board);
        assertTrue(move.x == 5);
        assertTrue(move.y == 5);
        assertTrue(move.type == MoveType.OPEN);
    }

    @Test
    public void moveIsTakenFromTestQue() {
        Square square = new Square(5, 5);
        this.bot.flag_s.push(square);
        assertTrue(!this.bot.flag_s.isEmpty());
        Move move = this.bot.makeMove(this.board);
        assertTrue(move.x == square.getX());
        assertTrue(move.y == square.getY());
        System.out.println(move.type);
        // assertTrue(move.type == MoveType.FLAG);
        // assertTrue(this.bot.flag_s.isEmpty());
    }

    @Test
    public void amountOfUndiscoveredSquaresNearReturnsCorrectInteger() {
        MinefieldGenerator generator = new MinefieldGenerator(2);
        Board board = new Board(generator, 4, 4, 1);
        board.makeMove(new Move(MoveType.OPEN, 1, 1));

        int count = bot.amountOfUndiscoveredSquaresNear(new Square(2, 2), board);

        assertTrue(count == 2);
    }

    @Test
    public void amountOfUndiscoveredSquaresNearBorderDetectionWorks() {
        MinefieldGenerator generator = new MinefieldGenerator(2);
        Board board = new Board(generator, 4, 4, 1);
        
        int count = bot.amountOfUndiscoveredSquaresNear(new Square(3, 3), board);

        count = bot.amountOfUndiscoveredSquaresNear(new Square(0, 0), board);
    }

    @Test
    public void amountOfFlaggedSquaresNearReturnsCorrectInteger() {
        Board board = new Board(generator, 3, 3, 0);
        board.makeMove(new Move(MoveType.FLAG, 0, 0));
        board.makeMove(new Move(MoveType.FLAG, 0, 1));

        int count = bot.amountOfFlaggedSquaresNear(new Square(1, 1), board);

        System.err.println(count);

        assertTrue(count == 2);
    }

    @Test
    public void amountOfFlaggedSquaresNearBorderDetectionWorks() {
        Board board = new Board(generator, 3, 3, 0);
        
        int count = bot.amountOfFlaggedSquaresNear(new Square(2, 2), board);

        count = bot.amountOfFlaggedSquaresNear(new Square(0, 0), board);
    }

    @Test
    public void recursionAmountOfUndiscoveredSquaresNearReturnsCorrectInteger() {
        MinefieldGenerator generator = new MinefieldGenerator(2);
        Board board = new Board(generator, 4, 4, 1);
        board.makeMove(new Move(MoveType.OPEN, 1, 1));

        int count = bot.amountOfUndiscoveredSquaresNear(new Square(2, 2), board, new HashSet<Square>());

        assertTrue(count == 2);
    }

    @Test
    public void recursionAmountOfUndiscoveredSquaresNearBorderDetectionWorks() {
        MinefieldGenerator generator = new MinefieldGenerator(2);
        Board board = new Board(generator, 4, 4, 1);
        
        int count = bot.amountOfUndiscoveredSquaresNear(new Square(3, 3), board, new HashSet<Square>());

        count = bot.amountOfUndiscoveredSquaresNear(new Square(0, 0), board, new HashSet<Square>());
    }

    @Test
    public void recursionAmountOfFlaggedSquaresNearReturnsCorrectInteger() {
        Board board = new Board(generator, 3, 3, 0);
        board.makeMove(new Move(MoveType.FLAG, 0, 0));
        board.makeMove(new Move(MoveType.FLAG, 0, 1));

        int count = bot.amountOfFlaggedSquaresNear(new Square(1, 1), board, new HashSet<Square>());

        System.err.println(count);

        assertTrue(count == 2);
    }

    @Test
    public void recursionAmountOfFlaggedSquaresNearBorderDetectionWorks() {
        Board board = new Board(generator, 3, 3, 0);
        
        int count = bot.amountOfFlaggedSquaresNear(new Square(2, 2), board, new HashSet<Square>());

        count = bot.amountOfFlaggedSquaresNear(new Square(0, 0), board, new HashSet<Square>());
    }
}