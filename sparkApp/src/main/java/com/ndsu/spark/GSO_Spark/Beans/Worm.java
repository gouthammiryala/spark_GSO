package com.ndsu.spark.GSO_Spark.Beans;

import java.io.Serializable;

//package gsomapreduce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Worm implements Serializable {
	@Override
	public String toString() {
		return "Worm [id=" + id + ", position=" + Arrays.toString(position) + ", luc=" + luc + ", rd=" + rd + ", Jx="
				+ Jx + ", neighbourWormsSize=" + neighbourWormsSize + ", neightbourWorm=" + neightbourWorm
				+ ", wormNeighbors=" + wormNeighbors + "]";
	}

	int id;
	double position[]; // / dimensions
	double luc;
	double rd;
	double Jx;
	int neighbourWormsSize;	
	Worm neightbourWorm;
	List<Worm> wormNeighbors; // /
	
	public Worm()
	{
		
	}

	public Worm(double position[], double luc, double rd, double Jx, int neighbourWormsSize){
		this.position = position;
		this.luc = luc;
		this.rd = rd;
		this.Jx = Jx;
		this.neighbourWormsSize = neighbourWormsSize;
		
	}
	public Worm getNeightbourWorm() {
		return neightbourWorm;
	}

	public void setNeightbourWorm(Worm neightbourWorm) {
		this.neightbourWorm = neightbourWorm;
	}

	public void setID(int j) {
		id = j;
	}

	public int getID() {
		return id;
	}

	public void setPosition(double x[]) {
		position = new double[x.length];
		for (int i = 0; i < x.length; i++) { // System.out.println(""+x[i]);
			position[i] = x[i];
		}
		// System.out.println(""+position[0]);
	}

	public double[] getposition() {
		return position;
	}

	public void setwormNeighbors(List<Worm> wormNeighbors) {
		this.wormNeighbors = wormNeighbors;
	}

	public List<Worm> getwormNeighbors() {
		return wormNeighbors;
	}

	public void setluc(double l) {
		luc = l;
	}

	public double getluc() {
		return luc;
	}

	public void setRd(double r) {
		rd = r;
	}

	public double getRd() {
		return rd;
	}

	public void setJx(double j) {
		Jx = j;
	}

	public double getJx() {
		return Jx;
	}
	public int getNeighbourWormSize() {
		return neighbourWormsSize;
	}

	public void setNeighbourWormSize(int neighbourWormSize) {
		this.neighbourWormsSize = neighbourWormSize;
	}


	public void printSwarm(Worm swarm[]) {
		//		for (int i = 0; i < swarm.length; i++) {
		//			double position[] = new double[Settings.DIMENSION];
		//			position = swarm[i].getposition();
		//			System.out.print("W" + i + "\t");
		//			for (int a = 0; a < Settings.DIMENSION; a++) {
		//				System.out.print("P" + a + ": " + position[a] + "\t");
		//			}
		//			System.out.print("Jx:" + swarm[i].getJx() + "\t");
		//			System.out.print("Luc:" + swarm[i].getluc() + "\t");
		//			System.out.println("Rd:" + swarm[i].getRd() + "\t");
		//			// System.out.println("");
		//		}
	}

	public void printWorm() {
		//		System.out.print("W" + id + "\t");
		//		for (int a = 0; a < Settings.DIMENSION; a++) {
		//			System.out.print("P" + a + ": " + position[a] + "\t");
		//		}
		//		System.out.print("Jx:" + Jx + "\t");
		//		System.out.print("Luc:" + luc + "\t");
		//		System.out.println("Rd:" + rd + "\t");
		//		// System.out.println("");

	}

	public void printnb() {
		//		for (int i = 0; i < wormNeighbors.size(); i++) {
		//			double position[] = new double[Settings.DIMENSION];
		//			position = wormNeighbors.get(i).getposition();
		//			System.out.println("nb:" + wormNeighbors.get(i).getID() + "\t");
		//			for (int a = 0; a < Settings.DIMENSION; a++) {
		//				System.out.print("P" + a + ": " + position[a] + "\t");
		//			}
		//			System.out.print("Jx:" + wormNeighbors.get(i).getJx() + "\t");
		//			System.out.print("Luc:" + wormNeighbors.get(i).getluc() + "\t");
		//			System.out.println("Rd:" + wormNeighbors.get(i).getRd() + "\t");

	}
}



