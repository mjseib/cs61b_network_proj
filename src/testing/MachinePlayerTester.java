package testing;
import player.*;

/**
 * TODO Put here a description of what this class does.
 *
 * @author James. Created Mar 20, 2013.
 *      
 */
public class MachinePlayerTester extends MachinePlayer {
	
	public MachinePlayerTester(int color) {
		super(color);
	}
	
	private void printChipsLeft() {
		System.out.println("white_chips_left: " + white_chips_left);
		System.out.println("black_chips_left: " + black_chips_left);	}
	
	private static void printStepMoves(Move[] moves) {
		System.out.println();
        for (Move move : moves) {
        	System.out.print("(" + move.x2 + ", " + move.y2 + ") -> " + "(" + move.x1 + ", " + move.y1 + ")  ");
        }
	}
	
	private static void printAddMoves(Move[] moves) {
		System.out.println();
        for (Move move : moves) {
        	System.out.print("(" + move.x1 + ", " + move.y1 + ")  ");
        }
        System.out.println();
	}
	
	private void printMove(Move mv) {
		System.out.println();
		System.out.println("Picked (" + mv.x1 + ", " + mv.y1 + ")");
		System.out.println(getBoardGraphic());
		printChipsLeft();
	}
	
	public Move chooseMove() {
		printAddMoves(validMoves(board, color));
		return super.chooseMove();
	}

	protected Move chooseOppMove() { //James
		printAddMoves(validMoves(board, otherColor(color))); //testing 
    	Best b = alphaBetaSearch(otherColor(color), -101, 101, depth);
    	Move mv = null;
    	int[] to_coords = indexToCoords(b.getToIndex());
    	if (b.getMoveKind() == Move.ADD) {
    		mv = new Move(to_coords[0], to_coords[1]);
        } else if (b.getMoveKind() == Move.STEP) {
        	int[] from_coords = indexToCoords(b.getFromIndex());
        	mv = new Move(to_coords[0], to_coords[1], from_coords[0], from_coords[1]);
        } else { //debugging
        	System.out.println("OppMove is null because Best obj did not get updated properly");
        }
    	updateBoard(mv, otherColor(color));
        return mv;
    } 
	
	private void testMinNeighborChain() {
		System.out.println("Testing minNeighborChain.");
		System.out.println("At (1, 5): " + minNeighborChain(0, new int[] {1, 5}, BLACK));
		System.out.println(getBoardGraphic());
		printChipsLeft();
        System.out.println("At (4, 1): " + minNeighborChain(0, new int[] {4, 1}, BLACK));
        System.out.println(getBoardGraphic());
        printChipsLeft();
        System.out.println("At (4, 2): " + minNeighborChain(0, new int[] {4, 2}, BLACK));
        System.out.println(getBoardGraphic());
        printChipsLeft();
        System.out.println("At (6, 3): " + minNeighborChain(0, new int[] {6, 3}, BLACK));
        System.out.println(getBoardGraphic());
        printChipsLeft();
        System.out.println("At (3, 4): " + minNeighborChain(0, new int[] {3, 4}, BLACK)); 
        System.out.println(getBoardGraphic());
        printChipsLeft();
        System.out.println("At (3, 4) for WHITE: " + minNeighborChain(0, new int[] {3, 4}, WHITE)); 
        System.out.println(getBoardGraphic());
        printChipsLeft();
        System.out.println("At (2, 6) for BLACK: " + minNeighborChain(0, new int[] {2, 6}, WHITE)); 
        System.out.println(getBoardGraphic());
        printChipsLeft();
	}
	
	private void testAlphaBetaSearch() {
		System.out.println("Testing alphaBetaSearch");
		Best b = alphaBetaSearch(otherColor(color), -101, 101, depth);
		System.out.println(getBoardGraphic());
		System.out.println(b);
		printChipsLeft();
		System.out.println("------------------------------------------------");
	}
	
	private void testIsValidMove() {
		System.out.println("Testing isValidMove.");
		Move move1 = new Move(1, 5);
		Move move2 = new Move(4, 1);
		Move move3 = new Move(4, 2);
		Move move4 = new Move(6, 3);
		Move move5 = new Move(3, 4);
		System.out.println("Is valid " + move1 + "? " + isValid(move1, BLACK));
		System.out.println("Is valid " + move2 + "? " + isValid(move2, BLACK));
		System.out.println("Is valid " + move3 + "? " + isValid(move3, BLACK));
		System.out.println("Is valid " + move4 + "? " + isValid(move4, BLACK));
		System.out.println("Is valid " + move5 + "? " + isValid(move5, BLACK));
	}
	
	private void testValidMoves() {
		System.out.println("Testing validMoves");
		Move[] moves = validMoves(board, color);
		printAddMoves(moves);
		System.out.println(getBoardGraphic());
		printChipsLeft();
		System.out.println("------------------------------------------------");
	}

	public static void main(String[] args) {
		MachinePlayerTester m = new MachinePlayerTester(WHITE);
		//ADJUST BLACK_CHIPS_LEFT & WHITE_CHIPS_LEFT
		m.board[16]=BLACK;
        m.board[48]=BLACK;
        m.board[11]=BLACK;
        m.board[27]=BLACK;
        m.board[21]=BLACK;
        m.board[29]=BLACK;
        m.board[45]=BLACK;
        m.board[53]=BLACK;
        m.board[23]=BLACK;
        //m.board[47]=BLACK;
        //m.board[25]=BLACK; //this breaks board for some reason (if running hasNetwork())? 
        m.board[52]=WHITE;
        m.board[38]=WHITE;
        m.board[20]=WHITE;
        m.white_chips_left = 7;
        m.black_chips_left = 1;
        System.out.println(m.getBoardGraphic());
        
        /*
        while ((m.white_chips_left > 0) || (m.black_chips_left > 0)) {
        	m.printMove(m.chooseMove());
        	if ((m.white_chips_left <= 0) && (m.black_chips_left <= 0)) 
        		break;
        	m.printMove(m.chooseOppMove());
        }*/
        
		MachinePlayerTester m2 = new MachinePlayerTester(WHITE);
        //System.out.println(m2.getBoardGraphic());
        //System.out.println("Has network? " + m.hasNetwork(m.board, BLACK));
        
        //Move[] black_moves = m.validMoves(m.board, BLACK);
        //printAddMoves(black_moves);
        //printStepMoves(black_moves);
        //Move[] white_moves = m.validMoves(m.board, WHITE);
        //printAddMoves(white_moves);
        
        //m.testValidMoves();
		m.testMinNeighborChain();
		m.testIsValidMove();
        //m.testAlphaBetaSearch();
        
		//System.out.println(m2.getBoardGraphic());
		//m2.testValidMoves();
        //m2.testAlphaBetaSearch();
		//m2.printMove(m2.chooseMove());
		//m2.printMove(m2.chooseOppMove());
		
        /*
        while ((m2.white_chips_left > 0) || (m2.black_chips_left > 0)) {
        	m2.printMove(m2.chooseMove());
        	if ((m2.white_chips_left <= 0) && (m2.black_chips_left <= 0)) 
        		break;
        	m2.printMove(m2.chooseOppMove());
        }*/
       
        MachinePlayerTester m3 = new MachinePlayerTester(WHITE);
        m3.board[8]=BLACK;
        m3.board[9]=BLACK;
        m3.board[11]=BLACK;
        m3.board[12]=BLACK;
        m3.board[14]=BLACK;
        m3.board[15]=BLACK;
        m3.board[24]=BLACK;
        m3.board[1]=WHITE;
        m3.board[2]=WHITE;
        m3.board[4]=WHITE;
        m3.board[5]=WHITE;
        m3.board[17]=WHITE;
        m3.board[18]=WHITE;
        m3.board[20]=WHITE;
        m3.board[21]=WHITE;
        m3.white_chips_left = 2;
        m3.black_chips_left = 3;
        System.out.println(m3.getBoardGraphic());
        
        
        /*while ((m3.white_chips_left > 0) || (m3.black_chips_left > 0)) {
        	m3.printMove(m3.chooseMove());
        	if ((m3.white_chips_left <= 0) && (m3.black_chips_left <= 0)) 
        		break;
        	m3.printMove(m3.chooseOppMove());
        }*/
        
        MachinePlayerTester m4 = new MachinePlayerTester(WHITE);
        m4.board[8]=BLACK;
        m4.board[9]=BLACK;
        m4.board[11]=BLACK;
        m4.board[12]=BLACK;
        m4.board[14]=BLACK;
        m4.board[15]=BLACK;
        m4.board[24]=BLACK;
        m4.board[25]=BLACK;
        m4.board[27]=BLACK;
        m4.board[1]=WHITE;
        m4.board[2]=WHITE;
        m4.board[4]=WHITE;
        m4.board[5]=WHITE;
        m4.board[17]=WHITE;
        m4.board[18]=WHITE;
        m4.board[20]=WHITE;
        m4.board[21]=WHITE;
        m4.board[35]=WHITE;
        m4.board[58]=WHITE;
        m4.white_chips_left = 0;
        m4.black_chips_left = 1;
        System.out.println("Abracadabra");
        System.out.println(m4.getBoardGraphic());
        System.out.println("Has valid network? " + m4.hasNetwork(m4.board, WHITE));
        System.out.println(m4.getBoardGraphic());
	}

}
