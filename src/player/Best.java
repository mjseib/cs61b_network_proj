package player;

/**
 * TODO Put here a description of what this class does.
 *
 * @author James. Created Mar 21, 2013.
 *      
 */
public class Best {

	/**
	 * Fields: move type (add or step), score, to_index, from_index (only for step move)
	 */
	public int move_kind;
	public double score;
	public int to_index;
	public int from_index;
	public final static int UNUSED = -999;
	
	public Best(int move_kind, double score, int to_index, int from_index) {
		this.move_kind = move_kind;
		this.score = score;
		this.to_index = to_index;
		this.from_index = from_index;
	}
	
	//necessary? 
	public Best(int move_kind, double score, int to_index) {
		this(move_kind, score, to_index, UNUSED);
	}
	
	public Best(double score) {
		this(UNUSED, score, UNUSED, UNUSED); 
	}
	
	public Best() {
		this(UNUSED, UNUSED, UNUSED, UNUSED);
	}
	
	public void setMoveKind(int move_kind) {
		this.move_kind = move_kind;
	}
	
	public void setScore(int scor) {
		this.score = score;
	}
	
	public void setToIndex(int to_index) {
		this.to_index = to_index;
	}
	
	public void setFromIndex(int from_index) {
		this.from_index = from_index;
	}
	
	public int getMoveKind() {
		return move_kind;
	}
	

	public double getScore() {
		return score;
	}
	

	public int getToIndex() {
		return to_index;
	}
	

	public int getFromIndex() {
		return from_index;
	}
	
	public String toString() {
		return String.format("My move_kind is %s, my score is %s, my to_index is %s, and my from_index is %s", 
				move_kind, score, to_index, from_index);
	}
}
