/* MachinePlayer.java */

package player;

import java.io.*;
import hashtable.*;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {
    
    protected static final byte BLACK = 0;
    protected static final byte WHITE = 1;
    protected static final byte EMPTY = 2;
    protected static byte otherColor(byte color){
        if (color==BLACK){return WHITE;}else if(color==WHITE){return BLACK;}
        else{return EMPTY;}}
    protected static final byte[] DIRECTION_OFFSET = {-1, 7, 8, 9, 1, -7, -8, -9};
    protected static final int[][] RC_OFFSET = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, 
                                        {1, -1}, {0, -1}, {-1, -1}};
    protected byte[] board = new byte[64]; 
    protected int white_chips_left;
    protected int black_chips_left;
    
    protected byte color;
    protected int depth;
    public static final double MAX = 100;
    public static final double MIN = -100;
    
    // 
    // index = column*8 + row
    
//    X/C 0    1    2    3    4    5    6    7
// Y/R -----------------------------------------
//  0  |    |  8 | 16 | 24 | 32 | 40 | 48 |    |
//     -----------------------------------------
//  1  |  1 |  9 | 17 | 25 | 33 | 41 | 49 | 57 |
//     -----------------------------------------
//  2  |  2 | 10 | 18 | 26 | 34 | 42 | 50 | 58 |
//     -----------------------------------------    7  0  1
//  3  |  3 | 11 | 19 | 27 | 35 | 43 | 51 | 59 |    6     2
//     -----------------------------------------    5  4  3
//  4  |  4 | 12 | 20 | 28 | 36 | 44 | 52 | 60 |
//     -----------------------------------------
//  5  |  5 | 13 | 21 | 29 | 37 | 45 | 53 | 61 |
//     -----------------------------------------
//  6  |  6 | 14 | 22 | 30 | 38 | 46 | 54 | 62 |
//     -----------------------------------------
//  7  |    | 15 | 23 | 31 | 39 | 47 | 55 |    |
//     -----------------------------------------
    
    // Creates a machine player with the given color.  Color is either 0 (black)
    // or 1 (white).  (White has the first move.)
    public MachinePlayer(int color) { //Anting
        this(color, 5);
    }

    // Creates a machine player with the given color and search depth.  Color is
    // either 0 (black) or 1 (white).  (White has the first move.)
    public MachinePlayer(int color, int searchDepth) { //Anting
        this.color = (byte)color;
        for (int i=0; i<64; i++){
            board[i]=EMPTY;
        }
        depth = searchDepth;
        white_chips_left = 10; 
        black_chips_left = 10;
    }

    // Returns a new move by "this" player.  Internally records the move (updates
    // the internal game board) as a move by "this" player.
    public Move chooseMove() { //James
    	Best b = alphaBetaSearch(color, -101, 101, depth);
    	Move mv = null;
    	int[] to_coords = indexToCoords(b.getToIndex());
    	if (b.getMoveKind() == Move.ADD) {
    		mv = new Move(to_coords[0], to_coords[1]);
        } else if (b.getMoveKind() == Move.STEP) {
        	int[] from_coords = indexToCoords(b.getFromIndex());
        	mv = new Move(to_coords[0], to_coords[1], from_coords[0], from_coords[1]);
        } else { // debugging
        	System.out.println("Move is null because Best obj did not update properly");
        }
    	updateBoard(mv, color);
        return mv;
    } 

    //updates the board with the Move m
    /**Need a "side" parameter, to know which side to mark down?*/
    protected void updateBoard(Move m, byte side) { //Ed
    	int to_index = coordsToIndex(m.x1, m.y1);
    	if (m.moveKind == Move.ADD) {
    		board[to_index] = side;
    		//update # of chips left on board
    		if (side == BLACK) { black_chips_left--; }
    		else if (side == WHITE) { white_chips_left--; }
        } else if (m.moveKind == Move.STEP) {
        	int from_index = coordsToIndex(m.x2, m.y2);
        	//byte from_color = board[from_index];
        	board[from_index] = EMPTY;
        	board[to_index] = side;  
        }
    }
    
    void reverseMove(Move m, byte side) { //James
    	int to_index = coordsToIndex(m.x1, m.y1);
    	if (m.moveKind == Move.ADD) {
    		board[to_index] = EMPTY;
    		if (side == BLACK) { black_chips_left++; }
    		else if (side == WHITE) { white_chips_left++; }
        } else if (m.moveKind == Move.STEP) {
        	int from_index = coordsToIndex(m.x2, m.y2);
        	board[from_index] = side;
        	board[to_index] = EMPTY;  
        }
    }
    
    // If the Move m is legal, records the move as a move by the opponent
    // (updates the internal game board) and returns true.  If the move is
    // illegal, returns false without modifying the internal state of "this"
    // player.  This method allows your opponents to inform you of their moves.
    public boolean opponentMove(Move m) { //Anting
        /*if (isValid(m, otherColor(color))){
            
            return true;
        }
    	return false;*/
    	updateBoard(m, otherColor(color));
    	return true;
    }

    // If the Move m is legal, records the move as a move by "this" player
    // (updates the internal game board) and returns true.  If the move is
    // illegal, returns false without modifying the internal state of "this"
    // player.  This method is used to help set up "Network problems" for your
    // player to solve.
    public boolean forceMove(Move m) { //Anting
        return false;
    }

    //uses this.board to determine if m is valid
    protected boolean isValid(Move m, byte side) { //Ed
    	/**
    	 * look at target square (applies to both ADD & STEP moves). Get a list of neighbors. 
    	 * for each neighbor, recursively call minNeighborChain(board, minChain + 1), while temp removing target square to avoid 
    	 * cycle. If minChain > 2, return false for isValid. 
    	 * Helper function: minNeighborChain(Board board, int minChain = 0) 
    	 * Base case: if no list of neighbors, return 1
    	 */
    	int[] coords = {m.x1, m.x2};
    	if (minNeighborChain(0, coords, side) < 2) {
    		return true;
    	}
        return false;
    }
    
    /**
     * Used to check whether there is AT LEAST a neighbor chain of 2 cells, for the given coords
     *
     * NOTE: calls to this method should use min_chain = 0 by default
     * @return int
     */
    protected int minNeighborChain(int min_chain, int[] coords, byte side) {
    	if (min_chain >= 2) 
    		return min_chain;
    	
    	int[] potential_neighbors = new int[8]; //assume max size 8
    	for (int i = 0; i < 8; i++) { //assume no neighbors initially (set to -1)
    		potential_neighbors[i] = -1;
    	}
    	int neighbor_count = 0;
    	int k = 0;
    	for (int x = coords[0] - 1; x <= coords[0] + 1; x++) {
			for (int y = coords[1] - 1; y <= coords[1] + 1; y++) {
				int index = coordsToIndex(x, y);
				if (!(x == coords[0] && y == coords[1]) && inBounds(x, y, index)) {
					if (board[index] == side) {
						potential_neighbors[k] = index;
						neighbor_count++;
						k++;
					} 
				} 
			}
		}
    	if (neighbor_count == 0)
    		return min_chain;
  
    	//make neighbors_arr with just the right # of elements
    	int[] neighbors = new int[neighbor_count]; 
    	int l = 0;
    	for (int i = 0; i < 8; i++) {
    		if (potential_neighbors[i] != -1) {
    			neighbors[l] = potential_neighbors[i];
    			l++;
    		}
    	}
    	int chainLen = 0;
    	for (int i = 0; i < neighbor_count; i++) {
    		int[] new_coords = indexToCoords(neighbors[i]);
    		int old_coords_index = coordsToIndex(coords);
    		if (min_chain == 0) { //first cell in chain is already empty, so don't do remove/add 
    			chainLen = minNeighborChain(min_chain + 1, new_coords, side);
    		} else {
    			board[old_coords_index] = EMPTY; //temp remove curr coord to avoid double-counting
        		chainLen = minNeighborChain(min_chain + 1, new_coords, side);
        		board[old_coords_index] = side;
    		}
    		if (chainLen >= 2) {
    			return chainLen;
    		}
    	}
    	return chainLen;
    }
    
   
    /**
     * @return: list of valid moves (for given side)
     * @param: side; must be BLACK or WHITE;
     */
    protected Move[] validMoves(byte[] board, byte side) { //Ed
    	/**
    	 * 1) Get an array of all the chips for this side (corners & goal coords of other side are NOT valid)
    	 * 2) Get array of all empty cells
    	 * 3) For each empty cell, check how many neighbors (of given side) it has. Make cell invalid if there's too many. 
    	 * 4) Return valid moves (there are no white or black chips left, valid moves -> step move)
    	 */
    	int REMOVED = -1;
    	int[] empty_locations = new int[64]; // assume all cells are empty initially
    	HashTableChained invalidCells = getInvalidCells(side);
    	int num_removed = 0;
    
    	for (int i = 0; i < empty_locations.length; i++) {
    		//mark down & skip indices that correspond to illegal squares or existing player chips, while making sure 
    		//not to double-count those that are both in illegal square & have a player chip
    		if (invalidCells.hasKey(i) || ((board[i] == BLACK || board[i] == WHITE) && empty_locations[i] != REMOVED)) { 
    			empty_locations[i] = REMOVED;
    			num_removed++;
    			continue;
    		}
    		int[] coords = indexToCoords(i);
    		int neighbors = 0;
    		for (int x = coords[0] - 1; x <= coords[0] + 1; x++) {
    			for (int y = coords[1] - 1; y <= coords[1] + 1; y++) {
    				if (!(x == coords[0] && y == coords[1]) && inBounds(x, y, i)) {
    					if (board[coordsToIndex(x, y)] == side) {
    						neighbors += 1;
    					}
    				}
    			}
    		}
    		//if cell has > 1 neighbor, it's invalid immediately. Else, check if it's potentially part of a chain
    		if (neighbors > 1) {
    			empty_locations[i] = REMOVED;
    			num_removed++;
    		} else if (minNeighborChain(0, new int[] {coords[0], coords[1]}, side) >= 2) {
    			empty_locations[i] = REMOVED;
    			//System.out.println("minNeighborChain removed " + i);
    			num_removed++;
    		}
    	}
    	
    	/* Copy values of empty_locations -> finalized validMoves array */
    	Move[] moves;
    	int[] FINAL_empty_locs = new int[empty_locations.length - num_removed];
    	int q = 0;
    	for (int i = 0; i < empty_locations.length; i++) {
    		if (empty_locations[i] != REMOVED) {
    			FINAL_empty_locs[q] = i;
    			q++;
    		}
    	}
    	
    	if ((side == WHITE && white_chips_left > 0) || (side == BLACK && black_chips_left > 0)) { //add moves
    		moves = new Move[FINAL_empty_locs.length];
        	for (int i = 0; i < FINAL_empty_locs.length; i++) {
        		int[] coords = indexToCoords(FINAL_empty_locs[i]);
        		moves[i] = new Move(coords[0], coords[1]);	
        	}
    	} else { //do step moves
    		moves = new Move[10 * (FINAL_empty_locs.length)];
    		//System.out.println(getBoardGraphic());
    		int[] my_chip_locs = getChipLocations(side);
    		int k = 0;
    		for (int i = 0; i < my_chip_locs.length; i++) {
    			int[] from_coords = indexToCoords(my_chip_locs[i]);
    			for (int j = 0; j < FINAL_empty_locs.length; j++) {
            		int[] to_coords = indexToCoords(FINAL_empty_locs[j]);
            		moves[k] = new Move(to_coords[0], to_coords[1], from_coords[0], from_coords[1]);
            		k++;
            	}
    		}
    	}
    	//System.out.println(getBoardGraphic());
    	return moves;
    }
    protected int[] getChipLocations(byte side) {
    	int[] my_chip_locations;
    	if (side == WHITE) { my_chip_locations = new int[10 - white_chips_left]; }
    	else { my_chip_locations = new int[10 - black_chips_left]; }
    	int k = 0;
    	//System.out.println(getBoardGraphic());
    	for (int i = 0; i < board.length; i++) {
    		if (board[i] == side) {
    			my_chip_locations[k] = i;
    			k++;
    		}
    	}
    	return my_chip_locations;
    }
    
    /** 
     * Helper for validMoves
     * @return HashTable w/invalid cells (for given side) as items
     */
    protected HashTableChained getInvalidCells(byte side) {
    	int DEFAULT_VAL = -999;
    	HashTableChained invalidCells = new HashTableChained(4 + (2 * 6));
    	invalidCells.insert(0, DEFAULT_VAL); 
    	invalidCells.insert(7, DEFAULT_VAL);
    	invalidCells.insert(56, DEFAULT_VAL);
    	invalidCells.insert(63, DEFAULT_VAL);
    	if (side == WHITE) { 
    		int n1 = 8;
    		int n2 = 15;
    		for (int i = 0; i <= 5; i++) {
    			invalidCells.insert(n1 + (8 * i), DEFAULT_VAL);
    			invalidCells.insert(n2 + (8 * i), DEFAULT_VAL);
    		}
    	} else if (side == BLACK) {
    		int n1 = 1;
    		int n2 = 57;
    		for (int i = 0; i <= 5; i++) {
    			invalidCells.insert(n1 + i, DEFAULT_VAL);
    			invalidCells.insert(n2 + i, DEFAULT_VAL);
    		}
    	}
    	return invalidCells;
    }
    
    //returns whether the coordinate is in bounds
    protected boolean inBounds(int r, int c, int i){
        return r>=0 && r<8 && c>=0 && c<8 && i>0 && i!=7 && i!=56 && i<63;
    }
    
    //returns an array of chip coordinates that are connected to the chip at coor
    //the array has 8 elements, each element is a direction starting from up,
    //going clockwise. 0 represents empty/null.
    int[] connectedChips(byte[] board, int coor, byte color) { //Anting
        //initializes variables
        byte oppColor = otherColor(color);
        int row = coor%8, column = coor/8, r, c, i;
        int[] connected = new int[8], directions = {0,1,2,3,4,5,6,7};
        
        //set directions to look in, avoiding goal to goal connections
        int[] row0 = {3,4,5}, row7 = {7,0,1}, col0 = {1,2,3}, col7 = {5,6,7};
        if (row == 0){directions = row0;}
        else if(row == 7){directions = row7;}
        else if(column == 0){directions = col0;}
        else if(column == 7){directions = col7;}
        
        //loops through each direction
        for (int offset:directions){
            r = row; c = column; i = coor;
            //loops through each cell in that direction
            while (true){
                r += RC_OFFSET[offset][0];
                c += RC_OFFSET[offset][1];
                i += DIRECTION_OFFSET[offset];
                if (!inBounds(r,c,i)){break;}
                if (board[i]==color){
                    connected[offset] = i;
                    break;
                } else if(board[i]==oppColor){break;}
            }  
        }
        return connected;
    }
    

    //returns whether a coor is in the north or western goals
    boolean inNWGoal(int coor){
        return coor < 7 || coor % 8 ==0;}
    
    //checks for networks. returns True/False.
    protected boolean hasNetwork(byte[] board, byte color) { //Anting
    	System.out.println(getBoardGraphic()); //debugging
    	System.out.println("white_chips_left: " + white_chips_left);
		System.out.println("black_chips_left: " + black_chips_left);
        boolean result = false;
        
        //find chips in starting area and put them in array
        int[] startingChips = new int[6];
        if (color==WHITE){
            for (int i=0; i<=5; i++){
                if (board[i+57] == WHITE){
                    startingChips[i]=i+57;}}
        } else {
            for (int i=0; i<=5; i++){
                if (board[i*8+7] == BLACK){
                    startingChips[i]=i*8+7;}}}
        
        //DFS on startingChips
        for (int chip:startingChips){
            if (chip != 0){
                board[chip]=MachinePlayer.otherColor(color);
                result |= hasNetworkDFS(board, color, chip, 5, -1);
                board[chip]=color;
            }
        }
        System.out.println(getBoardGraphic()); //debugging
        System.out.println("white_chips_left: " + white_chips_left);
		System.out.println("black_chips_left: " + black_chips_left);
        return result;
    }
    
    //steps is the number of additional chips that must connect to this chip
    //exclude is the direction to exclude when looking
    boolean hasNetworkDFS(byte[] board, byte color, int coor, 
                            int steps, int exclude){
        //base case
        if (steps <= 0 && inNWGoal(coor)){return true;}
        
        byte otherColor = MachinePlayer.otherColor(color);
        int[] connected = connectedChips(board, coor, color);
        
        //loop through each direction
        for (int i = 0; i<8; i++){
            int chip = connected[i];
            
            //if direction is different and there is a connection
            if (chip!=0 && i!=exclude){
                
                //remove current chip to prevent circular networks
                board[chip]= otherColor;
                
                //recurse
                if (hasNetworkDFS(board, color, chip, steps-1, i)){
                    return true;}
                
                //reset board
                board[chip]=color;
            }
        }
        return false;
    }
    
    protected Best alphaBetaSearch(byte side, double alpha, double beta, int depth) { // <-- color/move type/Best obj/alpha/beta/depth
    	if (hasNetwork(board, side)) {
    		if (side == color) {
    			return new Best(MAX);
    		} else {
    			return new Best(MIN);
    		}
    	}
    	
		Best myBest = new Best();
		if (side == color) {
			myBest.score = alpha;
		} else {
			myBest.score = beta;
		}
		
		Move[] moves = validMoves(board, side);
		//System.out.println(getBoardGraphic());
		
		if (depth == 1) { //call eval function on each move
			for (Move mv: moves) {
				updateBoard(mv, side);
				double board_score = evalFunction(board);
				reverseMove(mv, side);
				if ((side == color) && (board_score > myBest.score)){
					myBest = new Best(mv.moveKind, board_score, coordsToIndex(mv.x1, mv.y1));
					if (mv.moveKind == Move.STEP) {
						myBest.setFromIndex(coordsToIndex(mv.x2, mv.y2)); 
					}
					alpha = board_score;
				} else if ((side == otherColor(color)) && (board_score < myBest.score)) {
					myBest = new Best(mv.moveKind, board_score, coordsToIndex(mv.x1, mv.y1));
					if (mv.moveKind == Move.STEP) {
						myBest.setFromIndex(coordsToIndex(mv.x2, mv.y2)); 
					}
					beta = board_score;
				} 
			}
		} else {
			for (Move mv : moves) {
				updateBoard(mv, side);
				Best reply = alphaBetaSearch(otherColor(side), alpha, beta, depth - 1); //switches between 1 and 2
				reverseMove(mv, side);
				
				if ((side == color) && (reply.score > myBest.score)) {
					myBest = new Best(mv.moveKind, reply.score, coordsToIndex(mv.x1, mv.y1));
					if (mv.moveKind == Move.STEP) {
						myBest.setFromIndex(coordsToIndex(mv.x2, mv.y2)); 
					}
					alpha = reply.score;
				} else if ((side == otherColor(color)) && (reply.score < myBest.score)) {
					myBest = new Best(mv.moveKind, reply.score, coordsToIndex(mv.x1, mv.y1));
					if (mv.moveKind == Move.STEP) {
						myBest.setFromIndex(coordsToIndex(mv.x2, mv.y2)); 
					}
					beta = reply.score;
				} 
				if (alpha >= beta) { 
					return myBest; 
				}
			}
		}
		return myBest; 
    }
    
    //returns int between ??
    double evalFunction(byte[] board) { //James
        return 1;
    }
    
    protected static int[] indexToCoords(int index) {
		//coords = {x, y} = {col, row}
		int[] coords = {index / 8, index % 8};
		return coords;
	}
	
    protected static int coordsToIndex(int x, int y) {
		int index = (x * 8) + y;
		return index;
	}
    
   	protected static int coordsToIndex(int[] coords) {
   		int x = coords[0]; int y = coords[1];
		return coordsToIndex(x, y);
   	}
    
    protected String getBoardGraphic() {
    	String s = "";
    	s += "   ";
    	/** Initializes top heading numbers */
    	for (int l = 0; l < 8; l++) {
    		s += "  " + l + "  ";
    	}
    	
    	s += "\n  ------------------------------------------";
    	for (int i = 0; i < 8; i++) {
    		s += "\n" + i + " |";
    		for (int j = 0; j < 8; j++) {
    			int board_index = (i + (j * 8));
    			byte value_at_index = board[(i + (j * 8))];
    			String repr;
    			if (value_at_index == BLACK) {
    				repr = "B";
    			} else if (value_at_index == WHITE) {
    				repr = "W";
    			} else {
    				repr = "-";
    			}
    			s += "  " + repr + "  ";
    			/*
    			if (board_index == 0 || board_index == 7 || board_index == 56 || board_index == 63) {
    				s += "     ";
    			} else {
    				s += "  " + repr + "  ";
    			}*/
    			
    		}
    		s += "|\n";
    	}
    	s += "  ------------------------------------------";
    	return s;
    }
    
    public static void main(String[] args){
    	
        MachinePlayer m = new MachinePlayer(WHITE);
        m.board[16]=BLACK;
        m.board[48]=BLACK;
        m.board[11]=BLACK;
        m.board[27]=BLACK;
        m.board[21]=BLACK;
        m.board[29]=BLACK;
        m.board[45]=BLACK;
        m.board[53]=BLACK;
        m.board[23]=BLACK;
        m.board[47]=BLACK;
        //m.board[25]=BLACK; //this breaks board for some reason (if running hasNetwork())? 
        m.board[52]=WHITE;
        m.board[38]=WHITE;
        m.board[20]=WHITE;
        m.white_chips_left = 7;
        m.black_chips_left = 0;
        long start_time = System.currentTimeMillis();
        System.out.println(m.hasNetwork(m.board, BLACK));
        System.out.println("Time taken to evaluate hasNetwork() " + (System.currentTimeMillis() - start_time) + " ms");
        Move[] moves = m.validMoves(m.board, BLACK);
        System.out.println();
        for (Move move : moves) {
        	System.out.print(coordsToIndex(move.x1, move.y1) + " ");
        }
    }
}
//    X/C 0    1    2    3    4    5    6    7
// Y/R -----------------------------------------
//  0  |    |  8 | 16B| 24 | 32 | 40 | 48B|    |
//     -----------------------------------------
//  1  |  1 |  9 | 17 | 25 | 33 | 41 | 49 | 57 |
//     -----------------------------------------
//  2  |  2 | 10 | 18 | 26 | 34 | 42 | 50 | 58 |
//     -----------------------------------------    7  0  1
//  3  |  3 | 11B| 19 | 27B| 35 | 43 | 51 | 59 |    6     2
//     -----------------------------------------    5  4  3
//  4  |  4 | 12 | 20W| 28 | 36 | 44 | 52W| 60 |
//     -----------------------------------------
//  5  |  5 | 13 | 21B| 29B| 37 | 45B| 53B| 61 |
//     -----------------------------------------
//  6  |  6 | 14 | 22 | 30 | 38W| 46 | 54 | 62 |
//     -----------------------------------------
//  7  |    | 15 | 23B| 31 | 39 | 47B| 55 |    |
//     -----------------------------------------