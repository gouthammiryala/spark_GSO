package com.ndsu.spark.GSO_Spark.Beans;
/**
 * 
 * @author Goutham
 * Bean to hold all the basic configuration. 
 * Expected to receive the values from a json either from an external source or local file
 *
 */
public class GSOConfig {
	public int testRun;
	public int swarmSize;
	public int dimension;
	public int maxIteration;
	public int peaksNO;

	public String benchName;
	public double p_const;
	public double gamma;
	public double B;
	public double nt;
	public double step;
	public double l0;
	public double r0;
	public double rs;
	public String pathy;;
	public int noOfNOdes;
	public int maxReducersPerNode;
	public int maxMappersPerNode;
	public double reducersThreshold;
	
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
	public int getPeaksNO() {
		return peaksNO;
	}
	public void setPeaksNO(int peaksNO) {
		this.peaksNO = peaksNO;
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
		return "Config [testRun=" + testRun + ", swarmSize=" + swarmSize + ", dimension=" + dimension
				+ ", maxIteration=" + maxIteration + ", peaksNO=" + peaksNO + ", benchName=" + benchName + ", p_const="
				+ p_const + ", gamma=" + gamma + ", B=" + B + ", nt=" + nt + ", step=" + step + ", l0=" + l0 + ", r0="
				+ r0 + ", rs=" + rs + ", pathy=" + pathy + ", noOfNOdes=" + noOfNOdes + ", maxReducersPerNode="
				+ maxReducersPerNode + ", maxMappersPerNode=" + maxMappersPerNode + ", reducersThreshold="
				+ reducersThreshold + "]";
	}
}
