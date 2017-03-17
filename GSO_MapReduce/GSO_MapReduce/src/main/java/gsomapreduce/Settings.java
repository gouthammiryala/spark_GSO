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
	public static int testrun;
	public static int SWARM_SIZE;
	public static int DIMENSION;
	public static int MAX_ITERATION;
	public static int peaksNO;

	public static String benchName;
	public static double p_const;
	public static double Gama;
	public static double B;
	public static double nt;
	public static double step;
	public static double l0;
	public static double r0;
	public static double rs;
	public static String pathy;;
	public static int noOfNOdes;
	public static int maxReducers;
	public static int maxMappers;
	public static double reducersThreshold;

	public Settings(String filename) {
		File configFile = new File(filename);
		// Check that file exists
		if (!configFile.exists()) {
			System.out.println("Config file " + filename + " does not exist");
		}

		// Read each line from the config file, setting the relevant variables
		try {
			BufferedReader in = new BufferedReader(new FileReader(configFile));
			this.testrun = Integer.parseInt(readNextLine(in));
			System.out.println(" fffffffffff" + testrun);
			this.SWARM_SIZE = Integer.parseInt(readNextLine(in));
			this.DIMENSION = Integer.parseInt(readNextLine(in));
			this.MAX_ITERATION = Integer.parseInt(readNextLine(in));
			this.peaksNO = Integer.parseInt(readNextLine(in));
			this.benchName = readNextLine(in);
			this.p_const = Double.parseDouble(readNextLine(in));
			this.Gama = Double.parseDouble(readNextLine(in));
			this.B = Double.parseDouble(readNextLine(in));
			this.nt = Double.parseDouble(readNextLine(in));
			this.step = Double.parseDouble(readNextLine(in));
			this.l0 = Double.parseDouble(readNextLine(in));
			this.r0 = Double.parseDouble(readNextLine(in));
			this.rs = Double.parseDouble(readNextLine(in));

			this.pathy = readNextLine(in);
			this.noOfNOdes = Integer.parseInt(readNextLine(in));
			this.maxReducers = Integer.parseInt(readNextLine(in));
			this.maxMappers = Integer.parseInt(readNextLine(in));
			this.reducersThreshold = Double.parseDouble(readNextLine(in));
			// this.resultsFile = readNextLine(in);

		} catch (Exception e) {
			System.out.println("Error opening config file " + filename);
			System.out.println(e.toString());
		}

	}

	/**
	 * Reads a single line from the configuration file, ignoring empty lines and
	 * removing commenting characters Prof. Simone
	 */
	private String readNextLine(BufferedReader in) throws Exception {
		String str = null;
		boolean proceed;

		// Read in lines from the input stream ignoring lines that are empty or
		// start with the "#" character
		do {
			proceed = false;
			str = in.readLine();
			str = str.trim();
			if (str.startsWith("#") || str.matches(""))
				proceed = true;
		} while (proceed);
		return str;
	}
}
