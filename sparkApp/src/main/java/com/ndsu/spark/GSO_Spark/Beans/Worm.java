package com.ndsu.spark.GSO_Spark.Beans;

import java.io.Serializable;

//package gsomapreduce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;


public class Worm implements Serializable {
	@Override
	public String toString() {
		  return new Gson().toJson(this);
	}

	int id;
	double position[]; // / dimensions
	double luc;
	double rd;
	double Jx;
	int neighbourWormsSize;	
	Worm neightbourWorm;
//	List<Worm> wormNeighbors; // /
	
	public Worm()
	{
		
	}

	public Worm(double position[], double luc, double rd, double Jx, int neighbourWormsSize){
		this.id = 0;
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
		this.position = x;
//		position = new double[x.length];
//		for (int i = 0; i < x.length; i++) { // System.out.println(""+x[i]);
//			position[i] = x[i];
//		}
		// System.out.println(""+position[0]);
	}

	public double[] getposition() {
		return position;
	}

//	public void setwormNeighbors(List<Worm> wormNeighbors) {
//		this.wormNeighbors = wormNeighbors;
//	}
//
//	public List<Worm> getwormNeighbors() {
//		return wormNeighbors;
//	}

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
	
	@Override
	public int hashCode() {
	    return this.id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
		{
			return false;
		}
		 return this.hashCode() == o.hashCode();
	}

}



