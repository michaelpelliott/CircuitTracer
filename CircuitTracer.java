import java.awt.Point;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Search for shortest paths between start and end points on a circuit board
 * as read from an input file using either a stack or queue as the underlying
 * search state storage structure and displaying output to the console or to
 * a GUI according to options specified via command-line arguments.
 * 
 * @author mvail, Michael Elliott
 */
public class CircuitTracer {
	ArrayList<TraceState> recursBestPaths = null;
	boolean firstTimeFlag = false;
	/** launch the program
	 * @param args three required arguments:
	 *  first arg: -s for stack or -q for queue
	 *  second arg: -c for console output or -g for GUI output
	 *  third arg: input file name 
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			printUsage();
			System.exit(1);
		}
		try {

			new CircuitTracer(args); //create this with args
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/** Print instructions for running CircuitTracer from the command line. */
	private static void printUsage() {
		System.out.println("The usage of CircuitTracer.java is: $"
				+ " java CircuitTracer [ -s || -q ] [ -c || -g ] [ filename ] "
				+ "\n [-s] for Stack or [-q] for Queue Storage container."
				+ "\n [-c] for Console output or [-g] for GUI output."
				+ "\n filename must be a valid file and path.");
	}
	
	/** 
	 * Set up the CircuitBoard and all other components based on command
	 * line arguments.
	 * Prints out the circuit boards with the shortest possible traces between
	 * starting component and ending component.
	 * @param args command line arguments passed through from main()
	 */
	private CircuitTracer(String[] args) {
		CircuitBoard circuit;
		Storage<TraceState> stateStore = null;
		try {
			if(args[0].equals("-s")) {
				stateStore = new Storage<TraceState>(Storage.DataStructure.stack);
			} else if (args[0].equals("-q")) {
				stateStore = new Storage<TraceState>(Storage.DataStructure.queue);
			} else {
				printUsage();
				System.exit(1);
			} 
			if (args[1].equals("-c")) {
				ArrayList<TraceState> tracePaths = new ArrayList<TraceState>();
				circuit = new CircuitBoard(args[2]);
				//finds the best possible paths, returns the bestPaths arraylist for printing
				tracePaths = sort(circuit, stateStore);			
				//prints out the bestPaths
				for(TraceState state : tracePaths) {
					System.out.println(state.toString());
				}	
			} else if (args[1].equals("-g")) {
				System.out.println("GUI Mode is currently unsupported.");
				System.exit(1);
			} else {
				printUsage();
				System.exit(1);
			}
		}
		catch (FileNotFoundException e) {
			printUsage();
			System.exit(1);
		}
		catch (InvalidFileFormatException e) {
			System.out.println(e.toString());
			System.exit(1);
		}
	}
	
	/**
	 * Private sorting method to find the shortest traces of the circuit board
	 * @param board - the circuit board that needs to be solved
	 * @return bestPaths - an arrayList containing all of the shortest possible paths
	 * 			possible for the between the starting and end components of the board.
	 */
	private ArrayList<TraceState> sort(CircuitBoard board, Storage<TraceState> store) {
		ArrayList<TraceState> bestPaths = new ArrayList<TraceState>();
		Point starting = new Point(board.getStartingPoint());

		// check if moving up is allowed, if so create and store the opening traceState
		if(board.isOpen((int)starting.getX()+1, (int)starting.getY())) {
			store.store(new TraceState(board, (int)starting.getX()+1, (int)starting.getY()));
		}
		// check if moving down is allowed, if so create and store the opening traceState
		if(board.isOpen((int)starting.getX()-1, (int)starting.getY())) {  
			store.store(new TraceState(board, (int)starting.getX()-1, (int)starting.getY()));
		}
		// check if moving right is allowed, if so create and store the opening traceState
		if(board.isOpen((int)starting.getX(), (int)starting.getY()+1)){
			store.store(new TraceState(board, (int)starting.getX(), (int)starting.getY()+1));
		}
		// check if moving left is allowed, if so create and store the opening traceState
		if(board.isOpen((int)starting.getX(), (int)starting.getY()-1)) {
			store.store(new TraceState(board, (int)starting.getX(), (int)starting.getY()-1));
		}
		while(!store.isEmpty()) {
			TraceState tempTrace = store.retrieve();
			if(tempTrace.isComplete()) {
				if(bestPaths.isEmpty() || tempTrace.pathLength() == bestPaths.get(0).pathLength()) {
					bestPaths.add(tempTrace);
				} else if (tempTrace.pathLength() < bestPaths.get(0).pathLength()) {
					bestPaths.clear();
					bestPaths.add(tempTrace);
				}
			} else {
				// try moving up
				if(tempTrace.isOpen(tempTrace.getRow()+1, tempTrace.getCol())) {
					store.store(new TraceState(tempTrace, tempTrace.getRow()+1, tempTrace.getCol()));
				}
				// try moving down
				if(tempTrace.isOpen(tempTrace.getRow()-1, tempTrace.getCol())) {
					store.store(new TraceState(tempTrace, tempTrace.getRow()-1, tempTrace.getCol()));
				}
				// try moving right
				if(tempTrace.isOpen(tempTrace.getRow(), tempTrace.getCol()+1)) {
					store.store(new TraceState(tempTrace, tempTrace.getRow(), tempTrace.getCol()+1));
				}
				// trying moving left
				if(tempTrace.isOpen(tempTrace.getRow(), tempTrace.getCol()-1)) {
					store.store(new TraceState(tempTrace, tempTrace.getRow(), tempTrace.getCol()-1));
				}
			}
		}
		return bestPaths;
	}
} // class CircuitTracer

