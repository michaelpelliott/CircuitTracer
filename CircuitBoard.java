import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents a 2D circuit board as read from an input file.
 * 
 * @author mvail, Michael Elliott
 * 
 */
public class CircuitBoard {
	/** current contents of the board */
	private char[][] board;
	/** location of row,col for '1' */
	private Point startingPoint;
	/** location of row,col for '2' */
	private Point endingPoint;

	// constants you may find useful
	private final int ROWS; // initialized in constructor
	private final int COLS; // initialized in constructor
	private final char OPEN = 'O'; // capital 'o'
	private final char CLOSED = 'X';
	private final char TRACE = 'T';
	private final char START = '1';
	private final char END = '2';
	private final String ALLOWED_CHARS = "OXT12";

	/** Construct a CircuitBoard from a given board input file, where the first
	 * line contains the number of rows and columns as ints and each subsequent
	 * line is one row of characters representing the contents of that position.
	 * Valid characters are as follows:
	 *  'O' an open position
	 *  'X' an occupied, unavailable position
	 *  '1' first of two components needing to be connected
	 *  '2' second of two components needing to be connected
	 *  'T' is not expected in input files - represents part of the trace
	 *   connecting components 1 and 2 in the solution
	 * 
	 * @param filename
	 * 		file containing a grid of characters
	 * @throws FileNotFoundException if Scanner cannot read the file
	 * @throws InvalidFileFormatException for any other format or content issue that prevents reading a valid input file
	 */
	public CircuitBoard(String filename) throws FileNotFoundException {
		Scanner fileScan = new Scanner(new File(filename));
		int rowCounter = 0;
		int rowHeader = 0;
		int colHeader = 0;
		int colCounter = 0;
		boolean header = false;
		boolean validFlag = false;
		boolean firstRowFlag = false;
		boolean oneFlag = false;
		boolean twoFlag = false;
		while(fileScan.hasNextLine()) {
			String fileLine = fileScan.nextLine();
			Scanner lineScan = new Scanner(fileLine);
			colCounter = 0;
			// Scans the header, looking for 2 ints describing the size of the 2d array
			if(header == false) {
				if(lineScan.hasNextInt()) {
					try {
						rowHeader = lineScan.nextInt();
						colHeader = lineScan.nextInt();
						board = new char[rowHeader][colHeader];
						header = true;
					} catch (InputMismatchException e) {
						lineScan.close();
						throw new InvalidFileFormatException("The header contains"
								+ " non-integer values.");
					}
					if(lineScan.hasNext()) {
						lineScan.close();
						throw new InvalidFileFormatException("The header of the file is not "
								+ "in the correct format, it needs to be only 2 integers.");
					}
				} 
				else {
					lineScan.close();
					throw new InvalidFileFormatException("The header of the file is not"  
							+ " in the correct format, it needs to be only 2 integers.");
				}
			} 
			// Scans the body of the circuit, building the circuit board out of the allowed characters
			// and throwing various exceptions as they're found.
			else {
				if(firstRowFlag == true) {
					rowCounter++;
				}
				firstRowFlag = true;
				while(lineScan.hasNext()) {
					if(rowCounter >= rowHeader) {
						lineScan.close();
						throw new InvalidFileFormatException("The Circuit Board doesn't match"
								+ " the size given in the header");
					}
					String nextChar = lineScan.next();
					
					if(colCounter >= colHeader) {
						lineScan.close();
						throw new InvalidFileFormatException("The Circuit Board doesn't match"
								+ " the size given in the header");
					}
					// could probably use a CASE here
					if(ALLOWED_CHARS.contains(nextChar)) {
						if(nextChar.equals("O")) {
							board[rowCounter][colCounter] = OPEN;
						} else if (nextChar.equals("X")) {
							board[rowCounter][colCounter] = CLOSED;
						} else if (nextChar.equals("1")) {
							if(oneFlag == true) {
								lineScan.close();
								throw new InvalidFileFormatException("Too many start components in the file.");
							}
							board[rowCounter][colCounter] = START;
							startingPoint = new Point(rowCounter, colCounter);
							oneFlag = true;
						} else if (nextChar.equals("2")) {
							if(twoFlag == true) {
								lineScan.close();
								throw new InvalidFileFormatException("Too many end components in the file.");
							}
							board[rowCounter][colCounter] = END;
							endingPoint = new Point(rowCounter, colCounter);
							twoFlag = true;
						} else { // T is found
							board[rowCounter][colCounter] = TRACE;
						}
					}
					else {
						lineScan.close();
						throw new InvalidFileFormatException("The file contains"
								+ " elements that are not O, X, 1, 2, or T.");
					}
					colCounter++;	
				}
			}
			lineScan.close();
			validFlag = true;		
		}
		if(colCounter != colHeader) {
			throw new InvalidFileFormatException("The Circuit Board doesn't match"
					+ " the size given in the header");
		}
		if(rowCounter != rowHeader - 1) {
			throw new InvalidFileFormatException("The Circuit Board doesn't match"
					+ " the size given in the header");
		}
		if(oneFlag == false) {
			throw new InvalidFileFormatException("The Circuit Board doesn't have "
					+ "a starting component to connect.");
		}
		if(twoFlag == false) {
			throw new InvalidFileFormatException("The Circuit Board doesn't have "
					+ "an ending component to connect.");
		}
		if(validFlag == false) {
			throw new InvalidFileFormatException("There is nothing in the file.");
		}
		ROWS = rowHeader; //replace with initialization statements using values from file
		COLS = colHeader;	
	}

	/**
	 * Copy constructor - duplicates original board
	 * 
	 * @param original board to copy
	 */
	public CircuitBoard(CircuitBoard original) {
		board = original.getBoard();
		startingPoint = new Point(original.startingPoint);
		endingPoint = new Point(original.endingPoint);
		ROWS = original.numRows();
		COLS = original.numCols();
	}

	/**
	 * utility method for copy constructor
	 * 
	 * @return copy of board array
	 */
	private char[][] getBoard() {
		char[][] copy = new char[board.length][board[0].length];
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				copy[row][col] = board[row][col];
			}
		}
		return copy;
	}

	/**
	 * Return the char at board position x,y
	 * 
	 * @param row row coordinate
	 * @param col col coordinate
	 * @return char at row, col
	 */
	public char charAt(int row, int col) {
		return board[row][col];
	}

	/**
	 * Return whether given board position is open
	 * 
	 * @param row
	 * @param col
	 * @return true if position at (row, col) is open
	 */
	public boolean isOpen(int row, int col) {
		if (row < 0 || row >= board.length || col < 0 || col >= board[row].length) {
			return false;
		}
		return board[row][col] == OPEN;
	}

	/**
	 * Set given position to be a 'T'
	 * 
	 * @param row
	 * @param col
	 * @throws OccupiedPositionException if given position is not open
	 */
	public void makeTrace(int row, int col) {
		if (isOpen(row, col)) {
			board[row][col] = TRACE;
		} else {
			throw new OccupiedPositionException("row " + row + ", col " + col + "contains '" + board[row][col] + "'");
		}
	}

	/** @return starting Point(row,col) */
	public Point getStartingPoint() {
		return new Point(startingPoint);
	}

	/** @return ending Point(row,col) */
	public Point getEndingPoint() {
		return new Point(endingPoint);
	}

	/** @return number of rows in this CircuitBoard */
	public int numRows() {
		return ROWS;
	}

	/** @return number of columns in this CircuitBoard */
	public int numCols() {
		return COLS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				str.append(board[row][col] + " ");
			}
			str.append("\n");
		}
		return str.toString();
	}

}// class CircuitBoard
