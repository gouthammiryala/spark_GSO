/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gsomapreduce;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 * 
 */
public class Settings {
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
	private double reducersThreshold;
	
	
	@Override
	public String toString() {
		return "Settings [testRun=" + testRun + ", swarmSize=" + swarmSize + ", dimension=" + dimension
				+ ", maxIteration=" + maxIteration + ", peaksNO=" + peaksNo + ", benchName=" + benchName + ", p_const="
				+ p_const + ", gamma=" + gamma + ", B=" + B + ", nt=" + nt + ", step=" + step + ", l0=" + l0 + ", r0="
				+ r0 + ", rs=" + rs + ", pathy=" + pathy + ", noOfNOdes=" + noOfNOdes + ", reducersThreshold="
				+ reducersThreshold + "]";
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
	public int getPeaksNO() {
		return peaksNo;
	}
	public void setPeaksNO(int peaksNO) {
		this.peaksNo = peaksNO;
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
	public double getReducersThreshold() {
		return reducersThreshold;
	}
	public void setReducersThreshold(double reducersThreshold) {
		this.reducersThreshold = reducersThreshold;
	}

	
	
	
		

//	public Settings(String filename) {
//		File configFile = new File(filename);
//		// Check that file exists
//		if (!configFile.exists()) {
//			System.out.println("Config file " + filename + " does not exist");
//		}
//
//		// Read each line from the config file, setting the relevant variables
//		try {
//			BufferedReader in = new BufferedReader(new FileReader(configFile));
//			this.testrun = Integer.parseInt(readNextLine(in));
//			System.out.println(" fffffffffff" + testrun);
//			this.SWARM_SIZE = Integer.parseInt(readNextLine(in));
//			this.DIMENSION = Integer.parseInt(readNextLine(in));
//			this.MAX_ITERATION = Integer.parseInt(readNextLine(in));
//			this.peaksNO = Integer.parseInt(readNextLine(in));
//			this.benchName = readNextLine(in);
//			this.p_const = Double.parseDouble(readNextLine(in));
//			this.Gama = Double.parseDouble(readNextLine(in));
//			this.B = Double.parseDouble(readNextLine(in));
//			this.nt = Double.parseDouble(readNextLine(in));
//			this.step = Double.parseDouble(readNextLine(in));
//			this.l0 = Double.parseDouble(readNextLine(in));
//			this.r0 = Double.parseDouble(readNextLine(in));
//			this.rs = Double.parseDouble(readNextLine(in));
//
//			this.pathy = readNextLine(in);
//			this.noOfNOdes = Integer.parseInt(readNextLine(in));
//			this.maxReducers = Integer.parseInt(readNextLine(in));
//			this.maxMappers = Integer.parseInt(readNextLine(in));
//			this.reducersThreshold = Double.parseDouble(readNextLine(in));
//			// this.resultsFile = readNextLine(in);
//
//		} catch (Exception e) {
//			System.out.println("Error opening config file " + filename);
//			System.out.println(e.toString());
//		}
//
//	}
//
//	/**
//	 * Reads a single line from the configuration file, ignoring empty lines and
//	 * removing commenting characters Prof. Simone
//	 */
//	private String readNextLine(BufferedReader in) throws Exception {
//		String str = null;
//		boolean proceed;
//
//		// Read in lines from the input stream ignoring lines that are empty or
//		// start with the "#" character
//		do {
//			proceed = false;
//			str = in.readLine();
//			str = str.trim();
//			if (str.startsWith("#") || str.matches(""))
//				proceed = true;
//		} while (proceed);
//		return str;
//	}
}
