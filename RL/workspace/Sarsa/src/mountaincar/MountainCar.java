package mountaincar;

public class MountainCar {
	int num_actions;
	
	public MountainCar() { 
		num_actions = 3;
	}
	
	public int numActions() { return num_actions; } 
	
	public double[] move(int action, double pos, double v) {
		return projectMove(action, pos, v);
	}
	
	public double[] projectMove(int action, double pos, double v) { 
		double[] ret = new double[2];
		ret[1] = projectV(pos, v, action);
		ret[0] = projectPos(pos, v, action);
		
		if(ret[0] == -1.2) { ret[1] = 0; }
		return ret;
	}
	
	public int evaluateReward(double pos) { 
		if(pos >= 0.5) { return 0; }
		return -1;
	}
	
	private double projectV(double pos, double v, int a) {
		return boundV(v + 0.001 * (a - 1) + -0.0025 * Math.cos(3 * pos));
	}
	
	private double projectPos(double pos, double v, int a) { 
		return boundPos(pos + projectV(pos, v, a));
	}
	
	private double boundPos(double pos) { 
		if(pos < -1.2) {
			return -1.2; 
		}
		else if(pos > 0.5) { return 0.5; }
		else { return pos; }
	}
	
	private double boundV(double v) { 
		if(v < -0.07) { return -0.07; }
		else if(v > 0.07) { return 0.07; } 
		else { return v; }
	}
}
