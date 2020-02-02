import java.util.Arrays;
import java.util.Random;


public class GridWorld {
	boolean[][] world;
	int size;
	Random gen;
	
	public GridWorld() { 
		world = new boolean[10][10];
		size = 10;
		gen = new Random();
		
		for(int i = 0; i < 10; i++) { 
			Arrays.fill(world[i], true);
		}
		for(int i = 0; i < 10; i++) { 
			world[0][i] = false;
			world[4][i] = false;
			world[9][i] = false;
			world[i][0] = false;
			world[i][9] = false;
		}
		world[4][5] = true;
	}
	
	public int[][] randomWalk() { 
		int[][] visits = new int[100][100];
		int j;
		int start = 11;
		int next;
		for(int i = 0; i < 100; i++) {
			j = 0;
			start = randomStart();
			while(start != 88 && j < 100) { 
				next = move(getXFrom(start), getYFrom(start));
				if(start != next) { visits[start][next]++; }
				start = next;
				j++;
			}
		}
		return visits;
	}
	
	public double[][] model(int[][] visits) { 
		double[][] diffusionModel = new double[100][100];
		int sum;
		for(int i = 0; i < visits[0].length; i++) { 
			sum = 0;
			for(int j = 0; j < visits[0].length; j++) { 
				sum += visits[i][j];
			}
			if(sum != 0) { 
				for(int j = 0; j < diffusionModel[0].length; j++) { 
					diffusionModel[i][j] = visits[i][j]*1.0 / sum;
				}
			}
		}
		return diffusionModel;
	}
	
	private int move(int x, int y) { 
		int dir = gen.nextInt(4);
		switch(dir) { 
			case 0: if(y + 1 < 10 && world[x][y + 1]) { return getStateFrom(x, y + 1); } else { return getStateFrom(x, y); }
			case 1: if(x + 1 < 10 && world[x + 1][y]) { return getStateFrom(x + 1, y); } else { return getStateFrom(x, y); }
			case 2: if(y - 1 >= 0 && world[x][y - 1]) { return getStateFrom(x, y - 1); } else { return getStateFrom(x, y); }
			default: if(x - 1 >= 0 && world[x - 1][y]) { return getStateFrom(x - 1, y); } else { return getStateFrom(x, y); }
		}
	}
	
	private int randomStart() { 
		int s = gen.nextInt(100);
		while(!world[getXFrom(s)][getYFrom(s)]) { 
			s = gen.nextInt(100);
		}
		return s;
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
	
	public static void main(String[] args) { 
		GridWorld w = new GridWorld();
		double[][] model = w.model(w.randomWalk());
		for(int i = 0; i < 100; i++) { 
			System.out.print(i + ": ");
			for(int j = 0; j < 100; j++) { 
				if( model[i][j] != 0.0) { 
					System.out.print(j + "," + model[i][j] + " ");
				}
			}
			System.out.println();
		}
	}

}
