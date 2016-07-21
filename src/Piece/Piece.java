package Piece;

/**
 * Piece objects are placed in the game board.
 * @author Brian Wong, Laszlo Glant
 *
 */
public class Piece {

	/**
	 * w or b (white or black), first of 2 chars that represent piece on board, used to identify side
	 */
	public char color;

	/**
	 * P, N, B, R, Q, K, second of 2 chars that represent piece on board, used to identify type of piece
	 */
	public char name;

	/**
	 * the number of moves that a piece has made, used when moving a pawn up 2 squares, castling
	 */
	public int numMoves;

	/**
	 * the turn number for the last turn that piece was moved, used for en passant
	 */
	public int lastMoved;
	/**
	 * Keeps track of the kings movements
	 */
	public static int blackKing [] = {0,4}; // bK starts at 0 4 
	public static int whiteKing [] = {7,4}; // wK starts at 7 4

	/**
	 * concatenation of chars color and name (ex. wR), used when displaying pieces on board
	 */
	public String toString() {
		return color + "" + name;
	}

	/**
	 * Constructor
	 * @param color w or b
	 * @param name p, N, B, R, Q, K
	 * @param numMoves number of moves piece has made
	 * @param lastMoved last turn that piece was moved
	 */
	public Piece(char color, char name, int numMoves, int lastMoved) {
		this.color = color;
		this.name = name;
		this.numMoves = numMoves;
		this.lastMoved = lastMoved;
	}
} 