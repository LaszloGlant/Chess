package UserInput;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import Piece.Piece;
import Util.Move;

/**
 * Input takes the user entry and determines if it is a valid chess move.
 * @author Brian Wong, Laszlo Glant
 *
 */
public class Input {

	public static ArrayList<String> entryCode = new ArrayList<String>();

	public static final int reverse [] = {7,6,5,4,3,2,1,0}; 

	public static char promo = 'Q';		// Q by default, change if user enters a move such as h7 h8 N

	/**
	 * Takes user entry from the keyboard
	 * @param p used to determine white's or black's turn
	 */
	public static void readEntry(char p){
		int counter = 0;		
		Scanner in = new Scanner(System.in);
		String entry = in.nextLine();		

		StringTokenizer tokenizer = new StringTokenizer(entry);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if(counter == 0){//if only one token entered is it draw or resign

				if(token.equals("draw") || token.equals("resign")){
					entryCode.add(token);
				}
				else{
					fileRank(token, p);
				}
			}
			if(counter == 1){//one file rank entered, read next
				fileRank(token, p);
			}
			if(counter == 2){//check if promotion or draw
				if(token.equals("draw?")){
					entryCode.add(token);
				}
				else if(token.equals("N") || token.equals("B") || token.equals("R") || token.equals("Q")){
					entryCode.add(token);
				}
				else{
					if(p == 'w'){
						System.out.print("Illegal move, try again\nWhite's move: ");
					}
					else{
						System.out.print("Illegal move, try again\nBlack's move: ");
					}
					entryCode.clear();
				}
			}
			if(counter == 3){//add draw?
				if(token.equals("draw?")){
					entryCode.add(token);
				}
				else{
					
				}
			}
			if(counter > 3){//max 4 tokens allowed so it is an error
				entryCode.clear();
			}

			counter++;
		}//end of while
		if(entryCode.size()==1 && !(entryCode.get(0).equals("draw") || entryCode.get(0).equals("resign"))){//only one file rank entered, that is invalid, prompt the user again
			if (counter == 1){
				fileRank("false",p);
			}
			entryCode.clear();//can't have only one FileRank
		}
		//in.close();

	}//end readEntry

	/**
	 * convert an input such as a2 to (6, 0), call move, which moves the piece from source to destination
	 * @param token one token in String
	 */
	public static void fileRank(String token, char p){
		StringBuilder sB = new StringBuilder();
		StringBuilder sBreversed = new StringBuilder();

		for(int i=0; i<token.toString().length(); i++){
			int ascii = String.valueOf(token.toString().charAt(i)).codePointAt(0);			
			if(ascii>96 && ascii<105 && i == 0){//letters
				sB.append(Integer.toString(ascii-97));
			}
			else if(ascii>48 && ascii<57 && i == 1){//numbers

				sB.append(Integer.toString(reverse[ascii-49]));
				sBreversed.append(sB.charAt(1));
				sBreversed.append(sB.charAt(0));
				entryCode.add(sBreversed.toString());				
			}
			else {
				if(p == 'w'){
					System.out.print("Illegal move, try again\nWhite's move: ");
				}
				else{
					System.out.print("Illegal move, try again\nBlack's move: ");
				}
				break;
			}
		}
	}

	/**
	 * Print appropriate message and exit for resign and draw messages, otherwise set promo to Q, N, R, B based on third token
	 * @param board 2D array of Pieces
	 * @param p white or black
	 * @param i turn number
	 * @return same return as moveIt
	 */
	public static int callMove(Piece[][] board, char p, int i) {
		int ret=-1;

		while(entryCode.isEmpty()){
			Input.readEntry(p);
		}
		if(entryCode.get(0).equals("resign")){//resign
			if(p=='w'){
				System.out.println("Black wins");
				System.exit(0);
			}
			else{
				System.out.println("White wins");
				System.exit(0);
			}
		}
		else if(entryCode.get(0).equals("draw")){//draw offered
			System.out.println("Draw");
			System.exit(0);
		}
		else if(entryCode.size()==3){

			if(entryCode.get(2).charAt(0)=='N'){//check promotion
				promo = 'N';
				ret = moveIt(board, p, i);
				promo = 'Q';
			}
			else if(entryCode.get(2).charAt(0)=='Q'){
				promo = 'Q';
				ret = moveIt(board, p, i);
				promo = 'Q';
			}
			else if(entryCode.get(2).charAt(0)=='R'){
				promo = 'R';
				ret = moveIt(board, p, i);
				promo = 'Q';
			}
			else if(entryCode.get(2).charAt(0)=='B'){
				promo = 'B';
				ret = moveIt(board, p, i);
				promo = 'Q';
			}
			else if(entryCode.get(2).equals("draw?")){
				ret = moveIt(board, p, i);
			}
			else{
				
			}
		}
		else{
			ret = moveIt(board, p, i);
		}
		return ret;
	}

	/**
	 * Calls the Move.move method, pass in the appropriate parameters
	 * to move the piece on the board
	 * @param board 2D array of Pieces
	 * @param p white or black
	 * @param i current turn number
	 * @return same as move, -1 if move is bad, another number otherwise
	 */
	public static int moveIt(Piece[][] board, char p, int i){
		//check if entryCode is empty 
		int ret;
		int r1 = Character.getNumericValue(entryCode.get(0).charAt(0));
		int c1 = Character.getNumericValue(entryCode.get(0).charAt(1));
		int r2 = Character.getNumericValue(entryCode.get(1).charAt(0));
		int c2 = Character.getNumericValue(entryCode.get(1).charAt(1));		
		ret = Move.move(board, p, r1, c1, r2, c2, i);
		entryCode.clear();	
		return ret;
	}
}
