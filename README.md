*************************
* Circuit Tracer
* CS 221 Summer 2019
* June 24 2019
* Michael Elliott
*************************

OVERVIEW:

	The Circuit Tracer program reads in 2 dimensional arrays that comprise a circuit, then finds
	all of the possible paths between the two components that need to be connected, and 
	reports the results of the shortest possible paths back to the user.

INCLUDED FILES:
	
	* CircuitTracer.java - main file, takes input and drives the other files
	* TraceState.java - source file
	* Storage.java - source file
	* CircuitBoard.java - source file, takes input file and makes a circuit board
	* InvalidFileFormatException.java - source file, custom exception
	* OccupiedPositionException.java - source file, custom exception
	* README - this document

COMPILING AND RUNNING:

	From the directory containing all of the files compile the driver (and all 
	other source files):
	
	```
	$ javac CircuitTracer.java
	$ javac CircuitBoard.java
	```
	
	To run the program execute the command:
	
	```
	$ java CircuitTracer [ -s || -q ] [ -c || -g ] [ filename ]
	```
	
	[ -s || -q ] is to use a Stack [ -s] or Queue [ -q ] storage container while sorting through
	the different possible traces of the circuit board in question.
	
	[ -c ] for Console Output of the most successful possible traces for that circuit board.
	[ -g ] for GUI Output, it is not implemented in this version, and will result in a message 
	confirming that, while exiting the program
	
	[ filename ] must be the filename of a circuit board with a valid format
	
	The valid format for files to be scanned is: the first row consists of 2 integer numbers, 
	denoting the rows and columns, respectively, of the circuit board to be solved. Followed by 
	white space separated characters of ONLY values: X, O, 1, 2, or T. The input file should be 
	void of T's, as those are denoting the trace attempts for solving the circuit board. The 
	characters have the following meanings:
	
	X - CLOSED POSITION, nothing can go there
	O - OPEN POSITION, allowed for soldering component 1 to component 2
	1 - STARTING COMPONENT, the component that needs connected to component 2
	2 - ENDING COMPONENT
	T - TRACE, where the path from 1 to 2 is 
	
	An example of a valid input file format for a 3 x 4 ( row x column ) circuit: 
	
	3 4
	X O 1 O
	O X O X
	2 O O O
	
	Console output will display all of the most efficient possible traces from the starting 
	component to the ending component. 
	
	An example of the output for the above input:
	
	X O 1 O
	O X T X
	2 T T O
	
PROGRAM DESIGN AND IMPORTANT CONCEPTS:

	The CircuitTracer class takes in the command line arguments, getting either a Stack Storage object 
	or a Queue Storage object depending upon user preference. The filename of the circuit board that the 
	user wants solved is also passed in as a command line argument. The filename is passed to the 
	CircuitBoard.java class, which checks for formatting issues within the file as it is building the circuit 
	board from the grid provided in the file. This circuit board is returned and passed, as parameters, to the 
	sorting algorithm along with the storage object requested by the user.
	
	The storage object and file are then used to trace out all possible solutions to get from the starting point 
	of the circuit, to the ending component of the circuit, returning only the shortest possible paths. The algorithm 
	itself is a brute-force algorithm that starts by attempting to move to all 4 adjacent positions around the 
	starting component, and if they are available, storing each new move, called a TraceState, in the storage 
	object. Each TraceState is then removed from the storage object and checked to see if it is adjacent to the 
	ending component. If the TraceState is adjacent to the ending component, then its' path length is 
	checked against any other successfully completed TraceState, and the shortest path length's are kept in a 
	list that is returned to the user. If the TraceState is not next to the end component, then the algorithm 
	repeats by trying to move in each open direction and adding any open directions to the storage object. If a 
	TraceState has no open spots around it, it is not returned to the storage object. This continues until the 
	storage object is empty.
	
TESTING: 

	The provided CircuitTracerTester.java class was used to test both the CircuitTracer and CircuitBoard classes
	until it passed all tests. The CircuitBoard class handles exceptions that arise from any improper formatting 
	(e.g. characters that are not O, X, 1, 2, or T; headers that do not have the correct size of the grid; etc.) 
	by throwing an InvalidFileFormatException with a useful message. A FileNotFoundException, if needed, is 
	thrown to the CircuitTracer class to be handled. As of now there are no known issues with this program.

DISCUSSION: 

	Getting the CircuitBoard constructor up and running was difficult. I utilized a lot of my previous code 
	from the FormatChecker assignment to get started. One of the earliest problems was, since the FormatChecker 
	didn't actually build anything while it checked the format, how to simultaneously check format and build a 
	grid for the circuit board out of only allowed characters. The idea of HOW to do it came relatively easily, 
	and wasn't hard to implement. However, it really changed the placement of all of my format checking 
	"switches". I created a lot of bugs during this phase that took a few hours to search and destroy. Examples 
	of the "switches" are where I incremented my "rowCounter" variable, that is later used to compare values 
	with the "rowHeader" (value stated in the header), and same with the column verifiers. I had to use the 
	debugger to break down where that was problematic, and even with the debugger I placed it incorrectly 
	approximately half a dozen times. After I got the constructor able to pass most of the constructor tests, 
	I was able to find out that I had missed a few more sneaky little problems. I had to add a toggle for the 
	start and end components (1 and 2), so that there couldn't be circuits with multiples of either, instead 
	they would throw an InvalidFileFormatException.
	
	There was some clumsiness initially with how to handle the Scanner scanning in lines of String objects, 
	which I needed to convert to chars. This was a nice reminder that you cannot convert from String to Char, 
	and also caused me to have to look up how to use both the String.contains() and String.charAt() methods. 
	
	By far, the most challenging aspect of this project was the CircuitBoard constructor. Between checking for 
	all of the different possible invalid format exceptions, as well as simultaneously building the circuit, 
	I had a lot of bugs to find and work out. I also tried to make the sorting and tracing method (which I 
	called shortestTraces) recursive after I had it up and running, but that proved too difficult. I kept 
	running into NullPointerExceptions and decided that I wasn't doing it properly and that there may be 
	portions that are recursive in nature ("store") and parts that aren't and I should reserve my time 
	for the final and sleep. I'd really like to know what I was doing wrong for the recursion, but it seems 
	overly complicated to go through it all in the Discussion section without the actual code I tried.
	
ANALYSIS:

	The sequence for which path to continue working on for a stack is the last viable path found.
	If we imagine the circuit tracer paths as a tree growing vertical branches, then the stack storage 
	container will build out the next possible branches connected to the starting point, and continue from
	the last branch it started. It will then continue building out that branch of possibilities 
	until the branch has exhausted its possible moves to a point of being impossible to complete 
	the path or having successfully found the 2nd component. Once that branch has been exhausted, 
	it will return down the stack to the next incomplete branch, and begin building up that, 
	returning again after exhausting each branch build out, eventually returning to the original 
	starting point and building any possible branches it may have there. Once all of the possible 
	circuit paths have been built out in this fashion the stack will be done.
	
	The sequence for a list container is more complicated in that it will build out the possible branches
	from the starting point, then return to the first possible path that it made. From that point, it will 
	build the next step of each possible path (from that specific point), returning back to the 2nd original
	possible path. Then, building each possible next step from the 2nd point passed the starting position, 
	and returning to the 3rd possible step, and continuing in that fashion until the paths are exhausted 
	or impossible. Using the above tree analogy, it would be like drawing the tree one layer farther 
	away from the trunk each time.
	
	The total number of search states will be constant, and be the same total number of possibilities 
	regardless of which storage container we use to process the circuit board. The total number 
	of states will be a product of the size of the circuit board (2d array), the location of the starting 
	and ending components, and the open and closed spaces around them.  However, the maximum number of states in memory 
	is dependent upon which container we choose. The stack will have fewer states in storage because it is
	(in general) processing each route from start to finish, and not remembering all of the possible paths 
	simultaneously like a queue is keeping track of. The stack isn't quite as cut and dry for a large 
	array, but it still will have much fewer possible states in storage.
	
	The runtime of the search algorithm is O(n) because it will scale with the total number of unique pathways.
	Ultimately, this is determined by the number of open positions on the circuit as well as how many 
	steps it will take, on average, to terminate each traceState (by impossibility or by being adjacent to
	the second component). 
	
	The stack storage container will find a solution in fewer steps for all cases that aren't very small 
	(2x3 arrays, 2x2 arrays,etc.), and even then the list can only tie it in terms of steps. 
	The stack will finish out a branch of possible solutions while the queue will continue to work on all 
	of the possible solutions in more of a "layer-by-layer" approach. The list storage structure will 
	guarantee that the first solution found is the shortest solution, but the stack does not.
