package chess;

import Board.Board;
import Piece.Piece;
import Util.Conditions;
//import Util.Move;
import UserInput.Input;

/**
 * Chess contains the main method, which is used to run the game.
 * @author Brian Wong, Laszlo Glant
 *
 */
/*
 * Note on Design: Having any of our classes extend or implement another class, abstract class, or interface would not enhance our code.
 * We thought that our project would work better without these, so we intentionally left them out.
 */
public class Chess {

	/**
	 * main method that runs chess program
	 * @param args String array that main requires to run
	 */
	public static void main(String[] args) {
		// create and initialize board
		Piece[][] board = new Piece[8][8]; 		// create board
		Board.initWhite(board);					// set white tiles ('  ')
		Board.initBoard(board);					// set all 32 Pieces or initBoard1 - initBoard11 for testing
		Board.initSquares(board);				// set black tiles (##)

		Piece[][] boardCopy = new Piece[8][8];

		int okMove;

		for (int i = 0; ; i++) {
			Board.displayBoard(board);
			System.out.println();

			copy(board, boardCopy); // copy of board at turn i

			if (i % 2 == 0) {
				// even, white's turn, execute move with w as p


				// check if wK is in check or not
				if (Conditions.isCheck(board, 'w', i)) {
					// white is in check
					if (Conditions.isCheckmate(board, 'w', Piece.whiteKing[0], Piece.whiteKing[1], i)) {
						// checkmate
						System.out.println("Checkmate");
						System.out.println("Black wins");
						System.exit(0);
					} else {
						System.out.println("Check");
					}
				} else {
					// not in check, check if white in stalemate or not					
					if (Conditions.isStalemate(board, 'w', i)) {
						System.out.println("Stalemate");
						System.exit(0);
					}
				}

				System.out.print("White's move: ");
				Input.readEntry('w');
				okMove = Input.callMove(board, 'w', i);

				while (okMove < 0 || Conditions.isCheck(board, 'w', i)) {
					// move is illegal or move was good, but in check
					if (okMove > 0) {
						// move was good, but King in check, take back
						System.out.println("Illegal move, try again");
						copy(boardCopy, board);
					}

					// prompt player again
					System.out.print("White's move: ");
					Input.readEntry('w');
					okMove = Input.callMove(board, 'w', i);
				}

			} else {
				// odd, black's turn, execute move with b as p


				// check if bK is in check or not				
				if (Conditions.isCheck(board, 'b', i)) {
					// black is in check
					if (Conditions.isCheckmate(board, 'b', Piece.blackKing[0], Piece.blackKing[1], i)) {
						// checkmate
						System.out.println("Checkmate");
						System.out.println("White wins");
						System.exit(0);
					} else {
						System.out.println("Check");
					}
				} else {
					// not in cehck, check if black in stalemate or not					
					if (Conditions.isStalemate(board, 'b', i)) {
						System.out.println("Stalemate");
						System.exit(0);
					}
				}

				System.out.print("Black's move: ");
				Input.readEntry('b');
				okMove = Input.callMove(board, 'b', i);

				while (okMove < 0 || Conditions.isCheck(board, 'b', i)) {
					// move is illegal or move was good, but in check					
					if (okMove > 0) {
						// move was good, but King in check, take back
						System.out.println("Illegal move, try again");
						copy(boardCopy, board);
					}

					// prompt player again
					System.out.print("Black's move: ");
					Input.readEntry('b');
					okMove = Input.callMove(board, 'b', i);
				}

			}

			System.out.println();			
		}
	}

	/**
	 * copy contents from board, put in board2
	 * @param board 2D array of Pieces
	 * @param board2 secondary 2D array of Pieces
	 */
	public static void copy(Piece[][] board, Piece[][] board2) {

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board2[i][j] = board[i][j];
			}
		}
	}

}
