package mountaincar;

import java.util.Random;


public class Control {
	double[][] theta;
	double[][] traces;
	 
	double[] Q;
	double[][] F;
	double[] terms;
	int num_actions;
	int num_states;
	int n;
	
	int a, a_;
	double position, position_, velocity, velocity_;
	double terminal;

	double epsilon, r, delta;
	double gamma, lambda, alpha;
	
	MountainCar world;
	Random gen;
	
	public Control(int order, double t) { 
		n = order;
		a = a_ = 0;
		position = position_ = 0.0;
		velocity = velocity_ = 0.0;
		
		world = new MountainCar();
		num_actions = world.numActions();
		num_states = (int) Math.pow((n + 1), 2);
		terminal = t;
		
		
		traces = new double[num_actions][num_states];
		theta = new double[num_actions][num_states];
		
		Q = new double[num_actions];
		F = new double[num_actions][num_states];
		
		epsilon = 0.1;
		lambda = 0.9;
		alpha = 0.001;
		gamma = 1;
		
		gen = new Random();

	}
	
	public int iterateEpisodes(double initialPos, double initialV) {
		position = position_ = initialPos;
		velocity = velocity_ = initialV;
		updateActions(position, velocity);
		a = selectAction();
		
		int i = 0;
		traces = new double[num_actions][num_states];
		while(position_ != terminal ) { 
			iterateSteps(i);
			i++;
		}
		return i;
	}
	
	private void iterateSteps(int i) { 
		position = position_;
		velocity = velocity_;

		accumulateTraces();
		r = move(a, position, velocity);
		
		delta = r - Q[a];
		
		updateActions(position_, velocity_);
		a_ = selectAction();
		
		System.out.println(i + " position: " + position_ + " velocity: " + velocity_ + " action: " + (a - 1));
	//	System.out.print("delta: "  + delta);
		//for(int j = 0; j < num_actions; j++) {
		//	for(int k = 0; k < num_states; k++) { 
		//		System.out.print(Q[j] + " ");
		//	}
		//}
	//	System.out.println();
		
		updateTheta();
		a = a_;
	}
	
	private void accumulateTraces() { 
		for(int i = 0; i < num_actions; i++) {
			for(int j = 0; j < num_states; j++) { 
				traces[i][j] = gamma * lambda * traces[i][j];
				if(i == a) { 
					traces[a][j] += F[a][j];
				}
			}
		}
	}
	
	private void updateActions(double position, double velocity) { 
		updateF(position, velocity);
		updateQ();
	}
	
	private void updateF(double position, double velocity) { 
		for(int i = 0; i < num_actions; i++) { 
			updateF(i, position, velocity);
		}
	}
	
	private void updateF(int action, double position, double velocity) { 
		double[] state = world.projectMove(action, position, velocity);
		
		for(int i = 0; i < num_states; i++) { 
			F[action][i] = Math.cos(Math.PI * ((int)(i/(n + 1)) * state[0] + (int)(i%(n + 1)) * state[1]));
		}
	}
	
	private void updateQ() { 
		for(int i = 0; i < num_actions; i++) { 
			updateQ(i);
		}
	}
	
	private void updateQ(int i) { 
		Q[i] = 0;
		for(int j = 0; j < num_states; j++) { 
			Q[i] += theta[i][j] * F[i][j];
		}
	}
	
	private void updateTheta() { 
		delta = delta + gamma * Q[a];
		for(int i = 0; i < num_actions; i++) {
			for(int j = 0; j < num_states; j++) { 
				theta[i][j] = theta[i][j]  + alpha * delta * traces[i][j];
			}
		}
	}
	
	private int selectAction() { 
		double i = gen.nextDouble();
		
		if(i < epsilon) { 
	//		System.out.print(" random action ");
			return gen.nextInt(num_actions);
		} else {
			return maxAction();
		}
		
	}
	
	private int move(int action, double position, double velocity) {
		double[] state = world.move(action, position, velocity);
		position_ = state[0];
		velocity_ = state[1];
		return world.evaluateReward(position_);
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
		double v, p;
		double[] steps = new double[20];
		for(int k = 0; k < 100; k++) {
			Control c = new Control(3, 0.5);
			for(int j = 0; j < 20; j++) { 
				v = c.gen.nextDouble()*0.14 - 0.07;
				p = c.gen.nextDouble()*1.7 - 1.2;
				int num = c.iterateEpisodes(-.35, 0);
				steps[j] += num;
				System.out.println("episode " + j + ", iterations: " + num);
			}
		}
		System.out.println("final:");
		for(int j = 0; j < 20; j++) { 
			System.out.println(steps[j]/100);
		}
	}
	
}
