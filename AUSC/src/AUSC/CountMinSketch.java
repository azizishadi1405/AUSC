package AUSC;

import java.util.Random;

public class CountMinSketch {
	int depth;
	int width;
	long[][] table;
	long[][] hashAB;

	CountMinSketch(int width, int depth) {

		this.depth = depth;
		this.width = width;
		this.table = new long[this.width][this.depth];
		this.hashAB = new long[depth][2];
		Random r = new Random();

		for (int i = 0; i < depth; ++i) {
			int a = r.nextInt(Integer.MAX_VALUE - 1);
			int b = r.nextInt(Integer.MAX_VALUE - 1);
			hashAB[i][0] = a;
			hashAB[i][1] = b;
		}
	}

	int hash(int item, int i) {
		long hash = (hashAB[i][0] * item + hashAB[i][1]) % Integer.MAX_VALUE;
		return ((int) hash) % width;
	}
}
