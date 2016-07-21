package Util;

import Board.Board;
import Piece.Piece;
import UserInput.Input;

/**
 * Move contains the methods to move each piece (ex. movePawn)
 * Prints correct error message if move trying to be executed is illegal.
 * @author Brian Wong, Laszlo Glant
 *
 */
public class Move {	

	static boolean promoted = false; // set to true if user enters e7 e8 N, etc, false for all other types of moves

	/**
	 * move a piece from (r1, c1) to (r2, c2), execute move if valid, don't execute if invalid
	 * @param board 2D array of Pieces
	 * @param p w for white's turn, b for black's turn
	 * @param r1 initial row
	 * @param c1 initial column
	 * @param r2 final row
	 * @param c2 final column
	 * @param i current turn (ex. 0 for white's first turn, 1 for black's first turn, 2 for white's second turn, etc)
	 * @return negative number if move is bad, positive number if move is good
	 */
	public static int move(Piece[][] board, char p, int r1, int c1, int r2, int c2, int i) {
		// if not moving piece at all, error
		if (r1 == r2 && c1 == c2) {
			System.out.println("Illegal move, try again");
			return -10;
		}

		// Now assuming r1, c1, r2, c2 are all valid coordinates (nothing like -1 or 9 or 100)

		if (Board.isOccupied(board, r1, c1) == false) {
			// no piece at first location, no good
			System.out.println("Illegal move, try again");
			return -11;
		}

		// make sure no own piece at destination
		if (Board.isOccupied(board, r2, c2)) {
			// destination is occupied
			if (board[r2][c2].color == p) {
				// occupied by own color, bad
				System.out.println("Illegal move, try again");
				return -12;
			}
		}

		Piece one = board[r1][c1];

		if (one.color != p) {
			// must move piece of own color
			System.out.println("Illegal move, try again");
			return -13;
		}


		// checks on piece at initial location
		int isValid;
		promoted = false;

		if (one.name == 'p') {
			// piece is pawn, move pawn

			if (canEnPassant(board, p, r1, c1, r2, c2, i)) {				
				// all conditions for en passant meet and destination is correct				
				board[r2][c2] = new Piece(one.color, one.name, one.numMoves + 1, i);	// move to destination
				board[r1][c1] = new Piece(' ', ' ', 0, -1);		// delete old position piece
				Board.initSquares(board);	
				return 3;
			}
			isValid = movePawn(board, p, r1, c1, r2, c2, i);

			if (isValid > 0) {
				// move was valid, see if can upgrade pawn
				// if reach end (r = 0 for white, r = 7 for black), promote to promo (Q by default)
				if (p == 'w') {
					if (r2 == 0) {
						promoted = true;
					}
				}

				if (p == 'b') {
					if (r2 == 7) {
						promoted = true;
					}
				}
			}

		} else if (one.name == 'N') {
			// piece is knight, move knight
			isValid = moveKnight(board, p, r1, c1, r2, c2, i);
		} else if (one.name == 'B') {
			// piece is bishop, move bishop
			isValid = moveBishop(board, p, r1, c1, r2, c2, i);
		} else if (one.name == 'R') {
			// piece is rook, move rook
			isValid = moveRook(board, p, r1, c1, r2, c2, i);
		} else if (one.name == 'Q') {
			// piece is queen, move queen
			isValid = moveQueen(board, p, r1, c1, r2, c2, i);
		} else if (one.name == 'K') {
			// piece is king, move king
//			if (Conditions.isUnderAttack(board, p, r2, c2, i)) {
//				// destination is under attack by opposing piece, King can't move there or would put self in check
//				System.out.println("Illegal move, try again");
//				return -14;
//			} 			

			if (canCastle(board, p, r1, c1, r2, c2, i) > 0) {			

				if (c2 > c1) {
					// rightwards, kingside

					// check conditions good
					board[r2][c2] = new Piece(one.color, one.name, one.numMoves + 1, i);
					board[r1][c1] = new Piece(' ', ' ', 0, -1);
					board[r1][5] = new Piece(p, 'R', board[r1][7].numMoves + 1, board[r1][7].lastMoved);
					board[r1][7] = new Piece(' ', ' ', 0, -1);
					Board.initSquares(board);					
				} else {
					// check conditions good
					board[r2][c2] = new Piece(one.color, one.name, one.numMoves + 1, i);
					board[r1][c1] = new Piece(' ', ' ', 0, -1);
					board[r1][3] = new Piece(p, 'R', board[r1][0].numMoves + 1, board[r1][0].lastMoved);
					board[r1][0] = new Piece(' ', ' ', 0, -1);
					Board.initSquares(board);
				}


				if (p == 'w'){
					Piece.whiteKing[0]=r2;
					Piece.whiteKing[1]=c2;
				}
				else {
					Piece.blackKing[0]=r2;
					Piece.blackKing[1]=c2;
				}

				return 2;
			}

			isValid = moveKing(board, p, r1, c1, r2, c2, i);
			if (isValid>0){
				if (p == 'w'){
					Piece.whiteKing[0]=r2;
					Piece.whiteKing[1]=c2;
				}
				else {
					Piece.blackKing[0]=r2;
					Piece.blackKing[1]=c2;
				}
			}
		} else {
			// shouldn't happen, only 6 pieces
			return -1;
		}

		if (isValid < 0) {
			// not ok move
			System.out.println("Illegal move, try again");
			return -1;
		}

		if (promoted == true) {
			// Piece was a pawn and reached far end of board, change name of P to Q (or promoted piece name)
			board[r2][c2] = new Piece(one.color, Input.promo, one.numMoves + 1, i);
			board[r1][c1] = new Piece(' ', ' ', 0, -1);
			Board.initSquares(board);
		} else {
			// normally move piece and delete old
			board[r2][c2] = new Piece(one.color, one.name, one.numMoves + 1, i);
			board[r1][c1] = new Piece(' ', ' ', 0, -1);
			Board.initSquares(board);
		}
		promoted = false;
		return isValid;
	}

	/**
	 * Checks given the source and destination if it is a valid move for a pawn  
	 * @param board 2D array of Pieces
	 * @param p w or b
	 * @param r1 initial row
	 * @param c1 initial column
	 * @param r2 final row
	 * @param c2 final column	 
	 * @param i turn number
	 * @return -1 if invalid move, >0 if valid move
	 */
	public static int movePawn(Piece[][] board, char p, int r1, int c1, int r2, int c2, int i) {
		// Piece at (r1, c1) is a pawn of color p, return 1 if ok to move to destination

		if (c1 == c2) {
			// user aims to move pawn forward
			// move forward by 2
			if (board[r1][c1].numMoves == 0) {
				// this pawn has not moved yet, can move 2 squares if player wants to
				if (i % 2 == 0 && p == 'w') {
					// white pawn, can move up 2 spaces (r2 is 1 or 2 less than r1) if nothing in way
					if (Board.isOccupied(board, r2, c2) == false && Board.isOccupied(board, r1 - 1, c2) == false) {					
						// nothing in way
						if (r1 - r2 == 1 || r1 - r2 == 2) {
							// one or two squares up and same column
							return 1;
						}
					}
				} else if (i % 2 == 1 && p == 'b'){
					// black pawn, can move down (r2 is one or two more than r1) if nothing in way
					if (Board.isOccupied(board, r2, c2) == false && Board.isOccupied(board, r1 + 1, c2) == false) {
						// nothing in way
						if (r2 - r1 == 1 || r2 - r1 == 2) {
							// one square down and same column
							return 1;
						}
					}
				} else {
					// not white or black piece, bad

					return -1;
				}


			} else {
				// has moved already, but can still advance 1 space

				// make sure cannot capture forward
				if (p == 'w') {
					// white pawn
					if (Board.isOccupied(board, r1 - 1, c1)) {
						// square to north is occupied, can't move there no matter what color
						return -1;
					}
				}

				if (p == 'b') {
					// black pawn
					if (Board.isOccupied(board, r1 + 1, c1)) {
						// square to north is occupied, can't move there no matter what color
						return -1;
					}
				}

				// now only one square up/down is ok
				if (p == 'w') {
					if (r2 == r1 - 1) {						
						return 1;
					}
				} 

				if (p == 'b') {
					if (r2 == r1 + 1) {
						return 1;
					}
				} 

				// if didn't move up/down 1 square (above check failed), no good				
				return -1;
			}
		}//end if (c1 == c2)
		else {
			// user aims to capture diagonally
			// capture diagonally white
			if (p == 'w') {
				if (r2 == r1 - 1) {
					if (c1 == 0) {// column a
						if (c2 != 1) {							
							return -1;
						}
						//my turn if black ok else not
						if (i % 2 == 0 && board[r1 - 1][1].color == 'b') {//wp capture right from column a
							return 11;
						}//not my turn but still report that can capture there	
						else if (i % 2 == 1 && board[r1 - 1][1].color != 'w'){
							return 12;
						}else {
							return -1;
						}
					} else if (c1 == 7) {// column h
						if (c2 != 6) {							
							return -1;
						}
						//my turn if black ok else not
						if (i % 2 == 0 && board[r1 - 1][c1 - 1].color == 'b') {//wp capture left from column h
							return 13;
						} 
						//not my turn but still report that can capture there
						else if (i % 2 == 1 && board[r1 - 1][c1 - 1].color != 'w'){							
							return 14;
						}
						else{
							return -1;
						}
					} else {//center board
						if (c2 == c1 - 1) {//wp capture left; center board
							//my turn if black ok else not
							if (i % 2 == 0 && board[r2][c2].color == 'b') {
								return 15;
							}
							//not my turn but still report that can capture there
							else if (i % 2 == 1 && board[r2][c2].color != 'w'){	
								return 16;
							}
							else{
								return -1;
							}
						} else if (c2 == c1 + 1) {//wp capture right; center board
							//my turn if black ok else not
							if (i % 2 == 0 && board[r2][c2].color == 'b') {
								return 17;
							}//not my turn but still report that can capture there
							else if (i % 2 == 1 && board[r2][c2].color != 'w'){								
								return 18;
							}
							else{
								return -1;
							}
						} else {
							// not one of the 2 spaces							
							return -1;
						}
					}
				} else {

					return -1;
				}
			} else if (p == 'b') {
				// capture diagonally black
				if (r2 == r1 + 1) {
					if (c1 == 0) {// column a
						if (c2 != 1) {//bp capture right from column a							
							return -1;
						}//my turn if white ok else not
						if (i % 2 == 1 && board[r1 + 1][1].color == 'w') {
							// can capture diagonally inward
							return 19;
						}//not my turn but still report that can capture there
						else if (i % 2 == 0 && board[r1 + 1][1].color != 'b'){						
							return 20;
						}else{
							return -1;
						}
					} else if (c1 == 7) {// column h
						if (c2 != 6) {							
							return -1;
						}//my turn if white ok else not
							if (i % 2 == 1 && board[r1 + 1][c1 - 1].color == 'w') {//wp capture left from column h
								return 21;
							}//not my turn but still report that can capture there
							else if (i % 2 == 0 && board[r1 + 1][c1 - 1].color != 'b'){
								return 22;
							} else{
								return -1;
							}
						} else {//center board
							if (c2 == c1 - 1) {//wp capture left; center board
								//my turn if white ok else not
								if (i % 2 == 1 && board[r2][c2].color == 'w') {
									return 23;
								}//not my turn but still report that can capture there
								else if (i % 2 == 0 && board[r2][c2].color != 'b'){
									return 24;
								}
								else {								
									return -1;
								}
							} else if (c2 == c1 + 1) {//wp capture right; center board
								//my turn if white ok else not
								if (i % 2 == 1 && board[r2][c2].color == 'w') {
									return 25;
								}//not my turn but still report that can capture there
								else if (i % 2 == 0 && board[r2][c2].color != 'b') {
									return 26;
								}
								else {								
									return -1;
								}
							} else {
								// not one of the 2 spaces							
								return -1;
							}
						}
					} else {					
						return -1;
					}
				} else {
					return -1;
				}
			}
		// shouldn't happen		
		return -1;
	}

	/**
	 * Checks if (r1, c1) can capture (r2, c2) via En Passant
	 * does all checks, but final capture
	 * @param board 2D array of Pieces
	 * @param p w or b
	 * @param r1 capturer row
	 * @param c1 capturer column
	 * @param r2 capturee row
	 * @param c2 capturee column
	 * @param i current turn number
	 * @return true if can caputure via en passant, false otherwise
	 */
	public static boolean canEnPassant(Piece[][] board, char p, int r1, int c1, int r2, int c2, int i) {
		char oppC;
		int rowAdv;

		if (p == 'w') {
			// moving a white pawn
			if (r1 != 3) {
				// must be on fifth rank			
				return false;
			}
			oppC = 'b';
			rowAdv = r1 - 1;
		} else {
			// moving a black pawn
			if (r1 != 4) {
				// must be on fifth rank				
				return false;
			}
			oppC = 'w';
			rowAdv = r1 + 1;
		}

		if (c2 > c1) {
			// moving to the right

			if (board[r1][c1 + 1].color == oppC && board[r1][c1 + 1].name == 'p') {
				// good, next to opp pawn

				if (board[r1][c1 + 1].numMoves == 1 && (board[r1][c1 + 1].lastMoved + 1 == i)) {
					// this pawn has just moved and has moved up 2 and done nothing else
					if (r2 == rowAdv && c2 == c1 + 1) {
						// good destination

						board[r1][c1 + 1] = new Piece(' ', ' ', 0, -1);						
						return true;
					} else {						
						return false;
					}
				} else {					
					return false;
				}
			} else {				
				return false;
			}
		} else if (c1 > c2) {
			// moving to the left

			if (board[r1][c1 - 1].color == oppC && board[r1][c1 - 1].name == 'p') {
				// good, next to opp pawn
				if (board[r1][c1 - 1].numMoves == 1 && (board[r1][c1 - 1].lastMoved + 1 == i)) {
					// this pawn has just moved and has moved up 2 and done nothing else
					if (r2 == rowAdv && c2 == c1 - 1) {
						// good destination
						board[r1][c1-1] = new Piece(' ', ' ', 0, -1);						
						return true;
					} else {						
						return false;
					}
				} else {					
					return false;
				}
			} else {				
				return false;
			}
		} else {				
			return false;
		}

	}

	/**
	 * Checks given the source and destination if it is a valid move for a knight
	 * @param board 2D array of Pieces
	 * @param p w or b
	 * @param r1 initial row
	 * @param c1 initial column
	 * @param r2 final row
	 * @param c2 final column	 
	 * @param i turn number
	 * @return -1 if invalid, >0 if valid
	 */
	public static int moveKnight(Piece[][] board, char p, int r1, int c1, int r2, int c2, int i) {
		// Piece at (r1, c1) is a knight of color p, return 1 if ok to move to destination
		if (c1 == c2) {
			// no good, can't move in straight line			
			return -1;
		}
		if (r1 == r2) {
			// no good, can't move in straight line			
			return -1;
		}


		int dr = Math.abs(r1 - r2); // should be 1 or 2
		int dc = Math.abs(c1 - c2); // should be 1 or 2

		if (dr + dc != 3) {
			// must be moving exactly 3 squares (and all in one direction), so no good			
			return -1;
		}

		// otherwise, good move
		return 1;		
	}


	/**
	 * Checks given the source and destination if it is a valid move for a bishop
	 * @param board 2D array of Pieces
	 * @param p w or b
	 * @param r1 initial row
	 * @param c1 initial column
	 * @param r2 final row
	 * @param c2 final column	 
	 * @return -1 if invalid, >0 if valid
	 */
	public static int moveBishop(Piece[][] board, char p, int r1, int c1, int r2, int c2, int i) {
		// Piece at (r1, c1) is a bishop of color p, return 1 if ok to move to destination
		if (Math.abs(r1 - r2) != Math.abs(c1 - c2)) {
			// destination is not diagonal, not ok			
			return -1;
		}
		
		// make sure no obstacles in way of bishop
		if (!Board.isClear(board, r1, c1, r2, c2)){
			return -1;
		}
		// otherwise, good move
		return 1;
	}

	/**
	 * Checks given the source and destination if it is a valid move for a rook
	 * @param board 2D array of Pieces
	 * @param p w or b
	 * @param r1 initial row
	 * @param c1 initial column
	 * @param r2 final row
	 * @param c2 final column	 
	 * @param i turn number
	 * @return -1 if invalid, >0 if valid
	 */
	public static int moveRook(Piece[][] board, char p, int r1, int c1, int r2, int c2, int i) {
		// Piece at (r1, c1) is a rook of color p, return 1 if ok to move to destination

		// make sure Rook moves in straight line
		int dr = Math.abs(r1 - r2);
		int dc = Math.abs(c1 - c2);

		if (dr > 0 && dc > 0) {
			// not in straight line		
			return -1;
		}

		// make sure no obstacles in way of rook

		if (!Board.isClear(board, r1, c1, r2, c2)){
			return -1;
		}

		// otherwise, good move
		return 1;
	}

	/**
	 * Checks given the source and destination if it is a valid move for a queen
	 * @param board 2D array of Pieces
	 * @param p w or b
	 * @param r1 initial row
	 * @param c1 initial column
	 * @param r2 final row
	 * @param c2 final column	 
	 * @param i turn number
	 * @return -1 if invalid, >0 if valid
	 */
	public static int moveQueen(Piece[][] board, char p, int r1, int c1, int r2, int c2, int i) {
		// Piece at (r1, c1) is a queen of color p, return 1 if ok to move to destination

		//there is a piece in the way, in black's turn check for the white queen
		if (!Board.isClear(board, r1, c1, r2, c2)){
			return -1;
		}
		
		// now anywhere same row, same column, same diagonal is ok for this queen to move

		// same row
		if (r1 == r2) {
			return 10;
		}

		// same column
		if (c1 == c2) {
			return 11;
		}

		int dr = Math.abs(r1 - r2);
		int dc = Math.abs(c1 - c2);

		// same diagonal
		if (dr == dc) {
			return 12;
		}

		// otherwise no good		
		return -1;
	}

	/**
	 * Checks given the source and destination if it is a valid move for a king
	 * @param board 2D array of Pieces
	 * @param p w or b
	 * @param r1 initial row
	 * @param c1 initial column
	 * @param r2 final row
	 * @param c2 final column	 
	 * @param i turn number
	 * @return -1 if invalid, >0 if valid
	 */
	public static int moveKing(Piece[][] board, char p, int r1, int c1, int r2, int c2, int i) {
		// Piece at (r1, c1) is a king of color p, return 1 if ok to move to destination

		// not castling, move no more than 1 square away

		int dr = Math.abs(r1 - r2);		// should be 0 or 1
		int dc = Math.abs(c1 - c2);		// should be 0 or 1

		if (dr < 2 && dc < 2) {
			// only moving one square around King		

			return 1;
		}

		// otherwise, no good
		return -1;

	}

	public static int canCastle(Piece[][] board, char p, int r1, int c1, int r2, int c2, int i) {
		// if not moving only one square, must be castling

		Piece one = board[r1][c1];

		int rookR = -1;
		int rookC = -1;

		int midR = -1;
		int midC = -1;

		boolean kingSide = true;

		// attempting to castle
		if (r1 == 0 && c1 == 4) {
			// black castle

			if (r2 == 0 && c2 == 6) {
				// black castle king's side

				rookR = 0;
				rookC = 7;

				midR = 0;
				midC = 5;

				kingSide = true;
			} else if (r2 == 0 && c2 == 2) {
				// black castle queen's side
				rookR = 0;
				rookC = 0;

				midR = 0;
				midC = 3;

				kingSide = false;
			} else {
				// invalid castle				
				return -1;
			}
		} else if (r1 == 7 && c1 == 4) {
			// white castle

			if (r2 == 7 && c2 == 6) {
				// white castle king's side
				rookR = 7;
				rookC = 7;

				midR = 7;
				midC = 5;

				kingSide = true;
			} else if (r2 == 7 && c2 == 2) {
				// white castle queen's side
				rookR = 7;
				rookC = 0;

				midR = 7;
				midC = 3;

				kingSide = false;
			} else {
				// invalid castle				
				return -1;
			}
		} else {
			// not moving 1 square, not castling, shouldn't be doing something else than above moves
			return -1;
		}

		if (board[rookR][rookC].name != 'R') {
			// no rook to castle with
			return -1;
		}

		if (one.numMoves != 0) {
			// King has moved, invalid castle			
			return -1;
		}

		if (board[rookR][rookC].numMoves > 0) {
			// Rook has moved, invalid castle			
			return -1;
		}

		if (Conditions.isUnderAttack(board, p, r1, c1, i)) {
			// King is under attack, cannot castle out of check			
			return -1;
		}

		if (Conditions.isUnderAttack(board, p, midR, midC, i)) {
			// square next to King is under attack, King cannot pass through			
			return -1;
		}

		if (Conditions.isUnderAttack(board, p, r2, c2, i)) {
			// destination square (where King will end up) is under attack, cannot move King here			
			return -1;
		}

		if (kingSide) {
			if (Board.isOccupied(board, r1, 5) || Board.isOccupied(board, r1, 6)) {
				// there are pieces in way
				return -1;
			}
		} else {
			if (Board.isOccupied(board, r1, 3) || Board.isOccupied(board, r1, 2) || Board.isOccupied(board, r1, 1)) {
				// there are pieces in way
				return -1;
			}
		}

		// otherwise, good
		return 1;
	}

	/**
	 * Determines if piece at r1, c1 can move to r2, c2
	 * @param board 2D array of Pieces
	 * @param p w or b
	 * @param r1 row of piece starting location
	 * @param c1 column of piece starting location
	 * @param r2 row of destination
	 * @param c2 column of destination
	 * @param i current turn number
	 * @return true if piece can move from source to destination, false otherwise
	 */
	public static boolean movePiece(Piece[][] board, char p, int r1, int c1, int r2, int c2, int i) {
		// if not moving piece at all, not good
		if (r1 == r2 && c1 == c2) {
			return false;
		}

		if (Board.isOccupied(board, r1, c1) == false) {
			// no piece at first location, no good
			return false;
		}

		// make sure no own piece at destination
		if (Board.isOccupied(board, r2, c2)) {
			// destination is occupied
			if (board[r2][c2].color == p) {
				// occupied by own color, bad
				return false;
			}
		}

		Piece one = board[r1][c1];

		if (one.color != p) {
			// must move piece of own color
			return false;
		}

		if (one.color != p) {
			// must move piece of own color
			return false;
		}

		// checks on piece at initial location
		if (one.name == 'p') {
			// piece is pawn, move pawn
			if (movePawn(board, p, r1, c1, r2, c2, i) > 0) {
				return true;
			}

		} else if (one.name == 'N') {
			// piece is knight, move knight
			if (moveKnight(board, p, r1, c1, r2, c2, i) > 0) {
				return true;
			}
		} else if (one.name == 'B') {
			// piece is bishop, move bishop
			if (moveBishop(board, p, r1, c1, r2, c2, i) > 0) {
				return true;
			}
		} else if (one.name == 'R') {
			// piece is rook, move rook
			if (moveRook(board, p, r1, c1, r2, c2, i) > 0) {
				return true;
			}
		} else if (one.name == 'Q') {
			// piece is queen, move queen
			if (moveQueen(board, p, r1, c1, r2, c2, i) > 0) {
				return true;
			}
		} else if (one.name == 'K') {
			// piece is king, move king

			if (Conditions.isUnderAttack(board, p, r2, c2, i)) {
				// destination is under attack by opposing piece, King can't move there or would put self in check
				return false;
			} 			

			if (moveKing(board, p, r1, c1, r2, c2, i) > 0) {
				return true;
			}

		} else {
			// shouldn't happen, only 6 pieces
			return false;
		}
		return false;
	}

	public static void printCoord(int r1, int c1, int r2, int c2) {
		System.out.println("r1 " + r1 + " c2 " + c1 + " r2 " + r2 + " c2 " + c2);
	}

	/**
	 * Prints out O for every piece on board, and . for every blank square, only for testing purposes
	 * @param board 2D array of Pieces
	 */
	public static void occupancyCheck(Piece[][] board) {
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {		
				if (Board.isOccupied(board, r, c)) {
					System.out.print("O");
				} else {
					System.out.print(".");
				}
			}
			System.out.println();			
		}
	}

	/**
	 * Prints out number of moves each piece has made for testing only
	 * @param board 2D array of Pieces
	 */
	public static void numMovesCheck(Piece[][] board) {
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				System.out.print(board[r][c].numMoves);
			}
			System.out.println();
		}
	}
}
