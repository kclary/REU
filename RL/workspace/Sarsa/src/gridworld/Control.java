package gridworld;

import java.util.Random;


public class Control {
	double[] theta;
	double[] traces;
	 
	double[] Q;
	int[][] F;
	int num_states;
	int num_actions;
	
	int s, s_, a, a_, terminal;

	double epsilon, r, delta;
	double gamma, lambda, alpha;
	
	GridWorld world;
	Random gen;
	
	public Control(int size, int t) { 
		s = s_ = 0;
		a = a_ = 0;
		
		world = new GridWorld(size, t);
		num_states = world.numStates();
		num_actions = world.numActions(s);
		terminal = t;
		
		traces = new double[num_states];
		theta = new double[num_states];
		
		Q = new double[num_actions];
		F = new int[num_actions][num_states];
		
		epsilon = 0;
		lambda = 0.9;
		alpha = 0.1;
		gamma = 1;
		
		gen = new Random();

	}
	
	public int iterateEpisodes(int initialState) {
		s = s_ = initialState;
		updateActions(s);
		a = selectAction();
		
		traces = new double[num_states];
		int i = 0;
		while(s_ != terminal && i < 100) { 
			s = s_;
			iterateSteps();
			i++;
		}
		return i;
	}
	
	private void iterateSteps() { 
		accumulateTraces();
		
		r = move(s, a);
		delta = r - Q[a];
		
		updateActions(s_);
		a_ = selectAction();
		
		updateTheta();
		System.out.println("state: " + s + " next: " + s_);
		a = a_;
	}
	
	private void accumulateTraces() { 
		for(int i = 0; i < traces.length; i++) {
			traces[i] = gamma * lambda * traces[i];
			if(F[a][i] == 1) { 
				traces[i] += 1;
			}
		}
	}
	
	private void updateActions(int state) { 
		updateF(state);
		updateQ();
	}
	
	private void updateF(int state) { 
		for(int i = 0; i < num_actions; i++) { 
			updateF(i, state);
		}
	}
	
	private void updateF(int i, int j) { 
		int state = world.projectMove(i, j);
		F[i] = new int[num_states];
		F[i][state] = 1;
	}
	
	private void updateQ() { 
		for(int i = 0; i < num_actions; i++) { 
			updateQ(i);
		}
	}
	
	private void updateQ(int i) { 
		Q[i] = 0;
		for(int j = 0; j < num_states; j++) { 
			Q[i] += theta[j] * F[i][j];
		}
	}
	
	private void updateTheta() { 
		delta = delta + gamma * Q[a];
		for(int i = 0; i < theta.length; i++) { 
			theta[i] = theta[i]  + alpha * delta * traces[i];
		}
	}
	
	private int selectAction() { 
		double i = gen.nextDouble();
		
		if(i < epsilon) { 
			System.out.print(" random action ");
			return gen.nextInt(num_actions);
		} else {
			return maxAction();
		}
		
	}
	
	private int move(int state, int action) {
		s_ = world.move(action, state);
		return world.evaluateReward(s_);
	}
	
	private int maxAction() { 
		int ret = 0;
		double max = -Double.MAX_VALUE;
		for(int i = 0; i < num_actions; i++) { 
			if(Q[i] > max) { 
				max = Q[i];
				ret = i;
			}
		}
		return ret;
	}
	
	public static void main(String[] args) { 
		Control c = new Control(100, 999);
		for(int j = 0; j < 200; j++) { 
			System.out.println("iterations: " + c.iterateEpisodes(0));
		}
		System.out.println("final:");
		System.out.println(c.iterateEpisodes(0));
	}
	
}
