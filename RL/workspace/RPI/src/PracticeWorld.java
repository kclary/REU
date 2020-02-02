import java.util.Arrays;
import java.util.Random;
import org.math.array.LinearAlgebra;
import Jama.EigenvalueDecomposition;

public class PracticeWorld {
	boolean[][] world;
	int size;
	Random gen;
	
	public PracticeWorld() { 
		world = new boolean[3][3];
		size = 9;
		gen = new Random();
		
		for(int i = 0; i < world.length; i++) { 
			Arrays.fill(world[i], true);
		}
		world[0][2] = false;
		world[2][2] = false;
			
	}
	
	public int[][] weights() { 
		int[][] W = new int[9][9];
		int j;
		int start = 0;
		int next;
		for(int i = 0; i < 50; i++) { 
			j = 0;
			start = randomStart();
			while(j < 50) { 
				next = move(getXFrom(start), getYFrom(start));
				if(start != next) W[start][next] = 1;
				start = next;
				j++;
			}
		}
		return W;
	}
	
	public int[][] valence(int[][] W) { 
		int[][] D = new int[9][9];
		int sum;
		for(int i = 0; i < 9; i++) { 
			sum = 0; 
			for(int j = 0; j < 9; j++) { 
				if(W[i][j] != 0) { sum++; }
			}
			D[i][i] = sum;
		}
		return D;
	}
	
	public int[][] laplacian() { 
		int[][] weights = weights();
		int[][] valence = valence(weights);
		int[][] L = new int[9][9];
		for(int i = 0; i < 9; i++) { 
			for(int j = 0; j < 9; j++) { 
				L[i][j] = valence[i][j] - weights[i][j];
			}
		}
		return L;
	}
	
	public double[][] normalizedLaplacian() { 
		int[][] weights = weights();
		int[][] valence = valence(weights);
		int[][] L = laplacian();
		
		double[][] normalized = new double[L.length][L.length];
		for(int i = 0; i < L.length; i++) { 
			for(int j = 0; j < L.length; j++) { 
				if(i == j && L[i][j] != 0) { normalized[i][j] = 1; }
				else if(L[i][j] == -1) { normalized[i][j] = -1 * Math.pow(L[i][i] * L[j][j], -0.5); }
				else { normalized[i][j] = 0; }
			}
		}
		return normalized;
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
			case 0: if(y + 1 < 3 && world[x][y + 1]) { return getStateFrom(x, y + 1); } else { return getStateFrom(x, y); }
			case 1: if(x + 1 < 3 && world[x + 1][y]) { return getStateFrom(x + 1, y); } else { return getStateFrom(x, y); }
			case 2: if(y - 1 >= 0 && world[x][y - 1]) { return getStateFrom(x, y - 1); } else { return getStateFrom(x, y); }
			default: if(x - 1 >= 0 && world[x - 1][y]) { return getStateFrom(x - 1, y); } else { return getStateFrom(x, y); }
		}
	}
	
	private int randomStart() { 
		int s = gen.nextInt(9);
		while(!world[getXFrom(s)][getYFrom(s)]) { 
			s = gen.nextInt(9);
		}
		return s;
	}
	
	private int getStateFrom(int x, int y) { 
		if(x < 0) return getStateFrom(0, y);
		if(3 <= x) return getStateFrom(size - 1, y);
		if(y < 0) return getStateFrom(x, 0);
		if(3 <= y) return getStateFrom(x, size - 1);
		return 3*x + y;
	}
	
	private int getXFrom(int s) { 
		return s/3;
	}
	
	private int getYFrom(int s) { 
		return s%3;
	}
	
	public static void main(String[] args) { 
		PracticeWorld w = new PracticeWorld();
		//double[][] model = w.model(w.randomWalk());
		double[][] laplacian = w.normalizedLaplacian();
		EigenvalueDecomposition eigen = LinearAlgebra.eigen(laplacian);
		double[][] eigenvectors = eigen.getV().getArray();
		for(int i = 0; i < eigenvectors.length; i++) { 
			//System.out.print(i + ": ");
			for(int j = 0; j < eigenvectors[0].length; j++) { 
				System.out.print(eigenvectors[i][j] + " ");
				//if( model[i][j] != 0.0) { 
				//	System.out.print(j + "," + model[i][j] + " ");
				//}
			}
			System.out.println();
		}
	}

}
