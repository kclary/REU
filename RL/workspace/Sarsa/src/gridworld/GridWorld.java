package gridworld;

public class GridWorld {
	int[][] grid;
	int size;
	int state;
	int terminal;

	public GridWorld(int length, int t) { 
		size = length;
		grid = new int[size][size];
		terminal = t;
		state = 0;
		
	}
	
	public int numStates() { return size * size; }
	public int numActions(int state) { return 4; }
	public int getState() { return state; } 
	
	public int move(int action, int s) { 
		state = projectMove(action, s);
		return state;
	}
	
	public int projectMove(int action, int s) { 
		int ret;
		switch(action) {
		case 0: ret = getStateFrom(getXFrom(s) - 1, getYFrom(s)); break; //north
		case 1: ret = getStateFrom(getXFrom(s), getYFrom(s) - 1); break; //west
		case 2: ret = getStateFrom(getXFrom(s), getYFrom(s) + 1); break; //east
		default: ret = getStateFrom(getXFrom(s) + 1, getYFrom(s)); break; //south
		}
		return ret;
	}
	
	public int evaluateReward(int s) { 
		if(s == terminal) { 
			return 0;
		}
		return -1;
	}
	
	private int getStateFrom(int x, int y) { 
		if(x < 0) return getStateFrom(0, y);
		if(size <= x) return getStateFrom(size - 1, y);
		if(y < 0) return getStateFrom(x, 0);
		if(size <= y) return getStateFrom(x, size - 1);
		return size*x + y;
	}
	
	private int getXFrom(int s) { 
		return s/size;
	}
	
	private int getYFrom(int s) { 
		return s%size;
	}
	
}
