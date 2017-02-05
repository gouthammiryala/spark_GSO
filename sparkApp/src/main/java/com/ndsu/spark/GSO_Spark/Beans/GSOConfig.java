package com.ndsu.spark.GSO_Spark.Beans;

import java.io.Serializable;

/**
 * 
 * @author Goutham
 * Bean to hold all the basic configuration. 
 * Expected to receive the values from a json either from an external source or local file
 *
 */
public class GSOConfig implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sparkMaster;
	
	private int testRun;
	private int swarmSize;
	private int dimension;
	private int maxIteration;
	private int peaksNo;

	private String benchName;
	private double p_const;
	private double gamma;
	private double B;
	private double nt;
	private double step;
	private double l0;
	private double r0;
	private double rs;
	private String pathy;;
	private int noOfNOdes;
	private int maxReducersPerNode;
	private int maxMappersPerNode;
	private double reducersThreshold;
	
	public String getSparkMaster() {
		return sparkMaster;
	}
	public void setSparkMaster(String sparkMaster) {
		this.sparkMaster = sparkMaster;
	}


	
	public int getTestRun() {
		return testRun;
	}
	public void setTestRun(int testRun) {
		this.testRun = testRun;
	}
	public int getSwarmSize() {
		return swarmSize;
	}
	public void setSwarmSize(int swarmSize) {
		this.swarmSize = swarmSize;
	}
	public int getDimension() {
		return dimension;
	}
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}
	public int getMaxIteration() {
		return maxIteration;
	}
	public void setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;
	}
	public int getPeaksNo() {
		return peaksNo;
	}
	public void setPeaksNo(int peaksNo) {
		this.peaksNo = peaksNo;
	}
	public String getBenchName() {
		return benchName;
	}
	public void setBenchName(String benchName) {
		this.benchName = benchName;
	}
	public double getP_const() {
		return p_const;
	}
	public void setP_const(double p_const) {
		this.p_const = p_const;
	}
	public double getGamma() {
		return gamma;
	}
	public void setGamma(double gamma) {
		this.gamma = gamma;
	}
	public double getB() {
		return B;
	}
	public void setB(double b) {
		B = b;
	}
	public double getNt() {
		return nt;
	}
	public void setNt(double nt) {
		this.nt = nt;
	}
	public double getStep() {
		return step;
	}
	public void setStep(double step) {
		this.step = step;
	}
	public double getL0() {
		return l0;
	}
	public void setL0(double l0) {
		this.l0 = l0;
	}
	public double getR0() {
		return r0;
	}
	public void setR0(double r0) {
		this.r0 = r0;
	}
	public double getRs() {
		return rs;
	}
	public void setRs(double rs) {
		this.rs = rs;
	}
	public String getPathy() {
		return pathy;
	}
	public void setPathy(String pathy) {
		this.pathy = pathy;
	}
	public int getNoOfNOdes() {
		return noOfNOdes;
	}
	public void setNoOfNOdes(int noOfNOdes) {
		this.noOfNOdes = noOfNOdes;
	}
	public int getMaxReducersPerNode() {
		return maxReducersPerNode;
	}
	public void setMaxReducersPerNode(int maxReducersPerNode) {
		this.maxReducersPerNode = maxReducersPerNode;
	}
	public int getMaxMappersPerNode() {
		return maxMappersPerNode;
	}
	public void setMaxMappersPerNode(int maxMappersPerNode) {
		this.maxMappersPerNode = maxMappersPerNode;
	}
	public double getReducersThreshold() {
		return reducersThreshold;
	}
	public void setReducersThreshold(double reducersThreshold) {
		this.reducersThreshold = reducersThreshold;
	}
	
	
	@Override
	public String toString() {
		return "GSOConfig [sparkMaster=" + sparkMaster + ", testRun=" + testRun + ", swarmSize=" + swarmSize
				+ ", dimension=" + dimension + ", maxIteration=" + maxIteration + ", peaksNo=" + peaksNo
				+ ", benchName=" + benchName + ", p_const=" + p_const + ", gamma=" + gamma + ", B=" + B + ", nt=" + nt
				+ ", step=" + step + ", l0=" + l0 + ", r0=" + r0 + ", rs=" + rs + ", pathy=" + pathy + ", noOfNOdes="
				+ noOfNOdes + ", maxReducersPerNode=" + maxReducersPerNode + ", maxMappersPerNode=" + maxMappersPerNode
				+ ", reducersThreshold=" + reducersThreshold + "]";
	}
}
