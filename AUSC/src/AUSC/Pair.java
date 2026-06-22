package AUSC;

public class Pair implements Comparable<Pair> {
	private int node1;
	private int node2;
	private double score;

	public Pair(int node1, int node2) {
		this.node1 = node1;
		this.node2 = node2;
	}

	public Pair(int node1, int node2, double error) {
		this.node1 = node1;
		this.node2 = node2;
		this.score = error;
	}

	public boolean overLap(Pair p) {
		if (this.node1 == p.node1)
			return true;
		if (this.node1 == p.node2)
			return true;
		if (this.node2 == p.node1)
			return true;
		if (this.node2 == p.node2)
			return true;
		if (this.node1 == p.node1 && this.node2 == p.node2)
			return true;
		if (this.node1 == p.node1 && this.node2 == p.node2)
			return true;

		if (this.node2 == p.node1 && this.node1 == p.node2)
			return true;

		else
			return false;

	}

	public void setNode1(int i) {
		this.node1 = i;
	}

	public void setNode2(int i) {
		this.node2 = i;
	}

	public boolean equal(Pair p) {

		if (this.node1 == p.node1)
			if (this.node2 == p.node2)
				return true;

		if (this.node1 == p.node2)
			if (this.node2 == p.node1)
				return true;

		return false;

	}

	public int getNode1() {
		return this.node1;
	}

	public int getNode2() {
		return this.node2;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double s) {
		this.score=s;
	}

	public int compareTo(Pair e) {
		if (score < e.score)
			return 1;
		else if (score > e.score)
			return -1;
		return 0;

	}

}