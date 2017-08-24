
/**
 * 
 * @author Goutham
 *
 */

package com.ndsu.spark.GSO_Spark;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

//import org.apache.spark.util.AccumulatorV2;
//import org.apache.spark.util.DoubleAccumulator ;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkStatusTracker;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
//import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.MapFunction;
//import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
//import org.apache.spark.sql.SparkSession;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ndsu.spark.GSO_Spark.Beans.GSOConfig;
import com.ndsu.spark.GSO_Spark.Beans.Results;
import com.ndsu.spark.GSO_Spark.Beans.Worm;
import com.ndsu.spark.GSO_Spark.benchmark.CF4;
import com.ndsu.spark.GSO_Spark.benchmark.GSOBenchmark;
import com.ndsu.spark.GSO_Spark.utils.GSOBenchmarkHelper;
//import com.sun.tools.javac.code.Attribute.Array;
//import com.ndsu.spark.sparkApp.Mappers;
//import com.ndsu.spark.sparkApp.NeighborMapper;

import scala.Function1;

public class GSO_SparkMain {

	final static Logger logger = Logger.getLogger(GSO_SparkMain.class);
	private static HashMap<String, GSOConfig> gsoConfigMap;
	private static GSOConfig gsoConfig;
	// static WormAccumulator wormaccum = new WormAccumulator();
	static GSOBenchmark benchmark;
	// private static GSOUpdateLuciferen gsoUpdateLuc;
	// private static int

	public static void main(String args[]) {

		try {

			logger.info("***********GSO Algorithm on Spark Started*****************");
			 String log4jConfigFile = "resources/log4j.properties";
			 PropertyConfigurator.configure(log4jConfigFile);

			GSO_SparkMain gsospark = new GSO_SparkMain();
			gsospark.execute(args[0]);
			// brCenters.destroy();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.getMessage();
		}

	}

	void execute(String config) throws Exception {
		int iteration = 0;
		// GSOBroadcaster gsoBroadcaster = new GSOBroadcaster();
		gsoConfigMap = initialize();
		gsoConfig = gsoConfigMap.get(config);

		logger.info("Using the following configuration for this execution: " + gsoConfig);

		// System.setProperty("hadoop.home.dir", "D:\\NDSU\\DrSimones_Lab");

		SparkConf sparkConf = new SparkConf().setAppName("GSO_Spark")
				.setMaster("local[4]");
//		sparkConf.set("spark.default.parallelism", sparkConf.get("spark.executor.instances", "4"));
		
		
		JavaSparkContext sc = new JavaSparkContext(sparkConf);

		 
//		 spark
		sc.setCheckpointDir("checkpoint");
		logger.info("******************is Spark Master Set: " + sparkConf.get("spark.master"));
		logger.info("**********************************Number of Executors: "
				+ sparkConf.getInt("spark.executor.instances", 1) + "**********************************");
		 System.out.println("**********************************Number of Executors: "
					+ sparkConf.getInt("spark.executor.instances", 1) + "**********************************");

		// sc.hadoopConfiguration().set("dfs.nameservices","hadooptest");

		String peaksFile = "resources/"+gsoConfig.getBenchName()+"/peaksfile" + gsoConfig.getDimension()+".txt";

		

		double[][] peaks = new double[gsoConfig.getPeaksNo()][gsoConfig.getDimension()];
		peaks = readsPeaks(peaksFile, peaks);

		String outputDirectories = "output" + "/" + gsoConfig.getBenchName() + "dim" + gsoConfig.getDimension() + "rs"
				+ gsoConfig.getRs() + "it" + gsoConfig.getMaxIteration() + "ssize" + gsoConfig.getSwarmSize()
				+ "noNodes" + sc.getConf().getInt("spark.executor.instances", 1) + "_"
				+ new SimpleDateFormat("MM_dd_yyyy_h_m_s").format(new Date());
		String swarmInFolder = "input" + "/" + gsoConfig.getBenchName() + "dim" + gsoConfig.getDimension() + "rs"
				+ gsoConfig.getRs() + "it" + gsoConfig.getMaxIteration() + "ssize" + gsoConfig.getSwarmSize()
				+ "noNodes" + sc.getConf().getInt("spark.executor.instances", 1) + "_"
				+ new SimpleDateFormat("MM_dd_yyyy_h_m_s").format(new Date());
		
		List<Worm> swarm = createInitialSwarm(gsoConfig);
//		writeSwarmwithoutnb(swarm, );

		boolean success = (new File(outputDirectories)).mkdirs();
		//boolean success1 = (new File(swarmInFolder)).mkdirs();
		if (success) {
			System.out.println("Directories: " + outputDirectories + " created");
		}
//		if (success1){
//			System.out.println("Directories: " + outputDirectories + " created");
//		}

		// double start = System.currentTimeMillis();
		double startsubtime = System.currentTimeMillis();
		double sumdiff = 0;
		// No idea whats happening here?
		GSOBenchmark benchmark = new GSOBenchmarkHelper(gsoConfig.getBenchName()).getBenchMark();
	//	benchmark = new GSOBenchmarkHelper(gsoConfig.getBenchName());
//		benchmark.setO_(
//				loadOptima("resources/data/CF4_M_D" + gsoConfig.getDimension() + "_opt.dat", gsoConfig.getDimension()));
//		benchmark.setM_(loadRotationMatrix("resources/data/CF4_M_D" + gsoConfig.getDimension() + ".dat",
//				gsoConfig.getDimension()));
		for (Worm worm : swarm) {
			// bench = new GSOBenchmark(gsoConfig.getBenchName(),
			// worm.getposition());
			// double Jx = bench.getr();
			worm.setJx(benchmark.evaluate(worm.getposition()));
			double l = (1 - gsoConfig.getP_const()) * worm.getluc() + gsoConfig.getGamma() * worm.getJx();
			worm.setluc((1 - gsoConfig.getP_const()) * worm.getluc() + gsoConfig.getGamma() * worm.getJx());
		}

//		benchmark.setM_(loadRotationMatrix("resources/data/CF4_M_D" + gsoConfig.getDimension() + ".dat",
//				gsoConfig.getDimension()));
//		benchmark.setO_(
//				loadOptima("resources/data/CF4_M_D" + gsoConfig.getDimension() + "_opt.dat", gsoConfig.getDimension()));

		Results[] result = new Results[gsoConfig.getDimension()];
		double[][] resultPerIteration = new double[gsoConfig.getSwarmSize()][gsoConfig.getDimension()];

		for (int i = 0; i < gsoConfig.getDimension(); i++) {
			result[i] = new Results(gsoConfig.getSwarmSize(), gsoConfig.getMaxIteration());
		}

		FileWriter fstream1 = new FileWriter(outputDirectories + "/peakcapture.txt");
		BufferedWriter outpeakcapture = new BufferedWriter(fstream1);

		FileWriter fstream2 = new FileWriter(outputDirectories + "/avgdist.txt");
		BufferedWriter outavgdist = new BufferedWriter(fstream2);

		FileWriter fstream3 = new FileWriter(outputDirectories + "/timeinfo.txt");
		BufferedWriter outtime = new BufferedWriter(fstream3);
	
		

		while (iteration < gsoConfig.getMaxIteration()) {

			startsubtime = System.currentTimeMillis();
			logger.info("******************Iteration: " + iteration + " Started***************************");
			logger.info("Mapping and Reducing started");
//			if (iteration!=0)
//				swarm = readSwarm(swarmInFolder+"\\swarm"+iteration);
			JavaRDD<Worm> initialRDD = sc.parallelize(swarm);

			Broadcast<List<Worm>> brCenters = sc.broadcast(swarm);
			
//			Dataset<Row> wormDataSet = spark.createDataFrame(swarm, Worm.class);
					
	
			initialRDD = initialRDD.map(new GSOMapper1(gsoConfig, brCenters))
					.map(new GSOMapper2(gsoConfig, benchmark));
			swarm = initialRDD.collect();
//			initialRDD.saveAsObjectFile(swarmInFolder+(iteration+1));
		//	initialRDD.checkpoint();
			initialRDD.unpersist();
//			brCenters.unpersist();
			brCenters.destroy();
			

			System.out.println("************Number of worms: " + swarm.size() + "************************");
			logger.info("Mapping and Reducing Ended");
			double endsubtime = System.currentTimeMillis();
			double diff = (endsubtime - startsubtime) / 1000;
			sumdiff = sumdiff + diff;
			System.out.println("Time taken for execution of iteration " + iteration + " =" + diff + "\n");

			fill(swarm, iteration, result);
			fillresultPerIteration(swarm, resultPerIteration);
			double avdist = calcuateavgdist(resultPerIteration, peaks);
			outavgdist.write(iteration + "\t" + avdist + "\n");
			System.out.println("avdist at " + iteration + " iteration=" + avdist + "\n");
			double crate = calcuatecrate(resultPerIteration, peaks);
			System.out.println("crate at " + iteration + " iteration=" + crate + "\n");
			outpeakcapture.write(iteration + "\t" + crate + "\n");
			outtime.write(iteration + "\t" + diff + "\n");
			outpeakcapture.flush();
			outavgdist.flush();
			outtime.flush();
			iteration++;
			System.gc();
			Thread.sleep(2000);
//			sc.che
//			sc.c
			logger.info("******************Iteration: " + iteration + " Ended***************************");

		}
		if (iteration == (gsoConfig.getMaxIteration())) {
//			writeResults(outputDirectories, result);
			outtime.append("===============================================================\n");
			outtime.append("Total Time for " + gsoConfig.getMaxIteration() + " Iteartions:" + sumdiff + " (s)\n");
			outtime.append("Average Time per iteration :" + (sumdiff / gsoConfig.getMaxIteration()) + " (s)\n");
			outtime.append("===============================================================\n");
			outtime.append("#nodes:" + gsoConfig.getNoOfNOdes() + "\n");

			outpeakcapture.close();
			outavgdist.close();
			outtime.close();
		}
		sc.close();
	}

	// TODO
	// Deletion of this method is required later
	public static HashMap<String, GSOConfig> initialize() throws JsonSyntaxException, JsonIOException, IOException {
		String sLine = "";
		try (BufferedReader br = new BufferedReader(new FileReader("resources/config.json"))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				sLine = sLine + sCurrentLine;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return initialize(sLine);
	}

	/**
	 * Initialize the main class with all the required values here.
	 * 
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 * @throws IOException
	 */
	private static HashMap<String, GSOConfig> initialize(String json)
			throws JsonSyntaxException, JsonIOException, IOException {
		Type type = new TypeToken<HashMap<String, GSOConfig>>() {
		}.getType();

		Gson gson = new Gson();
		HashMap<String, GSOConfig> configMap = gson.fromJson(json, type);
		return (configMap);
	}

	/**
	 * Creates swarm with some random values
	 * 
	 * @param swarm
	 * @return
	 * @throws Exception
	 */
	public static List<Worm> createInitialSwarm(GSOConfig gsoConfig) throws Exception {
		// Worm swarm[] = new Worm[swarmSize];
		List<Worm> swarm = new LinkedList<Worm>();
		Random aRandom = new Random();
		GSOBenchmark bench;
		bench = new GSOBenchmarkHelper(gsoConfig.getBenchName()).getBenchMark();
		double aStart = bench.getMin();
		double aEnd = bench.getMax();
		double range = (double) aEnd - (double) aStart;

		for (int i = 0; i < gsoConfig.getSwarmSize(); i++) {
			Worm temp = new Worm();
			// random position
			double position[] = new double[gsoConfig.getDimension()];
			for (int a = 0; a < gsoConfig.getDimension(); a++) {
				double fraction = (double) (range * aRandom.nextDouble());
				double randomNumber = (double) (fraction + aStart);
				position[a] = randomNumber;
				// System.out.print(" position-----> "+ position[a]+"\t");
			}
			// System.out.println();
			temp.setID(i);
			temp.setPosition(position);
			temp.setluc(gsoConfig.getL0());
			temp.setRd(gsoConfig.getR0());
			swarm.add(temp);

		}
		return swarm;
	}

	/**
	 * 
	 * @param a
	 */
	private static void writeSwarmwithoutnb(List<Worm> swarm, String fileNameOut)
			throws IOException {
		FileSystem fs = FileSystem.get(new Configuration());
		Path outPath = new Path(fileNameOut);
		fs.delete(outPath, true);
		FSDataOutputStream outy = fs.create(outPath);
		for (Worm worm : swarm) {
			outy.writeBytes(worm.toString());
//			String st = "";
//			for (int w = 0; w < gsoConfig.getDimension() - 1; w++) {
//				st = st + (worm.getposition()[w] + ",");
//			}
//			st = st + (worm.getposition()[gsoConfig.getDimension() - 1] + ";");
//			st = st + (worm.getJx() + ";");
//			st = st + (worm.getluc() + ";");
//			st = st + (worm.getRd() + ";");
//
//			// System.out.println("W"+i+":"+st+"\n");
//			try {
//				outy.writeBytes(st);
//				outy.writeBytes("\n");
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}

		}
		outy.close();
	}
	
//	private static worm[] readSwarm(String fileNameIn, Configuration conf)
//			throws IOException {
//		worm swarm[] = new worm[settings.getSwarmSize()];
//
//		FileSystem fs = FileSystem.get(conf);
//		Path result = new Path(fileNameIn);
//		FileStatus[] stati = fs.listStatus(result);
//		for (FileStatus status : stati) {
//			if (!status.isDir()) {
//				String string = status.getPath().toString();
//				int indexx = string.indexOf("_SUCCESS");
//				if (indexx == -1) {
//					Path path = status.getPath();
//					FSDataInputStream in = fs.open(path);
//
//					String x = in.readLine();
//					int count = 0;
//
//					while (x != null) {
//
//						String[] keyAndValue = x.split(",");
//						//int wind = Integer.parseInt(keyAndValue[0]);
//						swarm[wind] = new worm();
//						swarm[wind].setID(Integer.parseInt(keyAndValue[0]));
//						String[] line = keyAndValue[1].toString().split(";");
//						String[] linepos = line[0].toString().split(",");
//						double[] pos = new double[settings.getDimension()];
//						for (int k = 0; k < settings.getDimension(); k++) {
//							pos[k] = Double.parseDouble(linepos[k]);
//						}
//						swarm[wind].setPosition(pos);
//						swarm[wind].setJx(Double.parseDouble(line[1]));
//						swarm[wind].setluc(Double.parseDouble(line[2]));
//						swarm[wind].setRd(Double.parseDouble(line[3]));
//						x = in.readLine();
//					}
//					in.close();
//
//				}
//
//			}
//		}
//		return swarm;
//
//	}
	
	/*private static ArrayList<Worm> readSwarm(String fileNameIn)
			throws IOException {
		ArrayList<Worm> swarm = new ArrayList<Worm>();//Worm[gsoConfig.getSwarmSize()];
//		System.out.println("Started reading swarm files");
		FileSystem fs = FileSystem.get(new Configuration());
		Path result = new Path(fileNameIn);
		FileStatus[] stati = fs.listStatus(result);
		for (FileStatus status : stati) {
			if (!status.isDir()) {
				String string = status.getPath().toString();
				int indexx = string.indexOf("_SUCCESS");
				if (indexx == -1) {
					Path path = status.getPath();
					FSDataInputStream in = fs.open(path);

					String line = in.readLine();
					int count = 0;

					while (line != null) {
				    	Gson gson = new Gson();
				    	swarm.add(gson.fromJson(line, Worm.class));
				        line = in.readLine();
				    }
					
					in.close();
				} 
				}
			}
		fs.close();
		
//		System.out.println("Ended reading swarm files");
		return swarm;

	}
*/
	private static void fill(List<Worm> swarm, int it, Results[] result) {
		for (int i = 0; i < gsoConfig.getDimension(); i++) {
			int j = 0;
			for (Worm worm : swarm) {
				result[i].setval(j, it, worm.getposition()[i]);
				j++;
			}
		}
	}

	private static void fillresultPerIteration(List<Worm> swarm, double[][] resultPerIteration) {
		int i = 0;
		for (Worm worm : swarm) {
			for (int j = 0; j < gsoConfig.getDimension(); j++) {
				resultPerIteration[i][j] = worm.getposition()[j];

			}
			i++;
		}
	}

	private static double calcuatecrate(double[][] resultPerIteration, double[][] peaks) {
		double crate = 0;
		int nwormsclosed = 3;
		double epsilon = 0.05;
		double[] gwormsInsidePeaks = new double[gsoConfig.getPeaksNo()];
		for (int i = 0; i < gsoConfig.getSwarmSize(); i++) {
			for (int k = 0; k < gsoConfig.getPeaksNo(); k++) {
				double dist = 0;
				for (int j = 0; j < gsoConfig.getDimension(); j++) {
					dist = dist + Math.pow((resultPerIteration[i][j] - peaks[k][j]), 2);
				}
				dist = Math.sqrt(dist);
				if (dist <= epsilon) {
					gwormsInsidePeaks[k] = gwormsInsidePeaks[k] + 1;
				}
			}
		}
		double count = 0;
		for (int k = 0; k < gsoConfig.getPeaksNo(); k++) {
			if (gwormsInsidePeaks[k] >= nwormsclosed) {
				count = count + 1;
			}
		}

		crate = count / (gsoConfig.getPeaksNo() * 1.0);

		return crate;
	}

	// private static double calcuateavgdist(double[][] resultPerIteration,
	// double[][] peaks) {
	// double sum = 0;
	// for (int i = 0; i < gsoConfig.getSwarmSize(); i++) {
	// double minseg = 0;
	// for (int j = 0; j < gsoConfig.getDimension(); j++) {
	// minseg = minseg
	// + Math.pow((resultPerIteration[i][j] - peaks[0][j]), 2);
	// }
	// minseg = Math.sqrt(minseg);
	//
	// for (int k = 1; k < gsoConfig.getPeaksNO(); k++) {
	// double seg = 0;
	// for (int j = 0; j < gsoConfig.getDimension(); j++) {
	// seg = seg
	// + Math.pow(
	// (resultPerIteration[i][j] - peaks[k][j]), 2);
	// }
	// seg = Math.sqrt(seg);
	// if (seg <= minseg) {
	// minseg = seg;
	// }
	// }
	// sum = sum + minseg;
	// }
	//
	// double disAvg = sum / (gsoConfig.getStep() * 1.0);
	//
	// return disAvg;
	// }

	private static double calcuateavgdist(double[][] resultPerIteration, double[][] peaks) {
		double sum = 0;
		for (int i = 0; i < gsoConfig.getSwarmSize(); i++) {
			double minseg = 0;
			for (int j = 0; j < gsoConfig.getDimension(); j++) {
				minseg = minseg + Math.pow((resultPerIteration[i][j] - peaks[0][j]), 2);
			}
			minseg = Math.sqrt(minseg);

			for (int k = 1; k < gsoConfig.getPeaksNo(); k++) {
				double seg = 0;
				for (int j = 0; j < gsoConfig.getDimension(); j++) {
					seg = seg + Math.pow((resultPerIteration[i][j] - peaks[k][j]), 2);
				}
				seg = Math.sqrt(seg);
				if (seg <= minseg) {
					minseg = seg;
				}
			}
			sum = sum + minseg;
		}

		double disAvg = sum / (gsoConfig.getSwarmSize() * 1.0);

		return disAvg;
	}

	private static void writeResults(String outputDirectories, Results[] result) throws IOException {

		for (int k = 0; k < gsoConfig.getDimension(); k++) {

			FileWriter fstreamX = new FileWriter(outputDirectories + "/d" + k);
			BufferedWriter outX = new BufferedWriter(fstreamX);
			for (int i = 0; i < gsoConfig.getSwarmSize(); i++) {
				String rowX = "";
				for (int a = 0; a < gsoConfig.getMaxIteration() - 1; a++) {
					rowX = rowX + result[k].getval(i, a) + ",";
				}
				rowX = rowX + result[k].getval(i, gsoConfig.getMaxIteration() - 1);
				outX.write(rowX + "\n");

			}
			outX.close();
		}

	}

	public static double[][] readsPeaks(String fname, double[][] peaks) throws NumberFormatException, IOException {

		BufferedReader in = new BufferedReader(new FileReader(fname));
		// Read File Line By Line
		int sc = 0;
		String strLine;
		while ((strLine = in.readLine()) != null) {

			strLine = strLine.trim();
			// System.out.println(">>>> "+strLine);
			String[] cont = strLine.split("\t");

			// Print the content on the console

			for (int i = 0; i < gsoConfig.getDimension(); i++) {
				// System.out.println("before fill peaks ");
				peaks[sc][i] = Double.parseDouble(cont[i]);
				// System.out.println(">>>> "+peaks[sc][i] );
			}
			sc = sc + 1;
		}
		in.close();
		System.out.println("peaks reading done: " + sc);
		return peaks;

	}

	static double[][] loadOptima(final String filename, int dim) {
		int nofunc_ = 8;
		double[][] O_;
		O_ = new double[nofunc_][dim];
		File file = new File(filename);
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(file));
			try {
				String buffer;
				try {
					for (int i = 0; i < nofunc_; ++i) {
						buffer = reader.readLine();

						double tmp = 0;
						String[] numbers = buffer.split("\t");
						for (int j = 0; j < dim; ++j) {
							tmp = Double.parseDouble(numbers[j].trim());
							O_[i][j] = tmp;
						}
					}
				} catch (NumberFormatException e) {
					System.err.println("Error: loadOptima, NumberFormatException: " + e.toString());
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("Error: loadOptima, IOException: " + e.toString());
					e.printStackTrace();
				}
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					System.err.println("Error: loadOptima, Can not close file: IOException: " + e.toString());
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Error: loadOptima, Can not find file: " + filename + e.toString());
			e.printStackTrace();
		}
		return O_;
	}

	static double[][][] loadRotationMatrix(final String filename, int dim) {
		int nofunc_ = 8;
		double[][][] M_ = new double[nofunc_][dim][dim];
		File file = new File(filename);
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(file));
			try {
				String buffer;
				try {
					double tmp = -1;
					for (int i = 0; i < nofunc_; ++i) {
						for (int j = 0; j < dim; ++j) {
							buffer = reader.readLine();
							String[] numbers = buffer.split("\t");
							for (int k = 0; k < dim; ++k) {
								tmp = Double.parseDouble(numbers[k].trim());
								M_[i][j][k] = tmp;
								// System.out.print(","+M_[i][j][k]);
							}
						}

					}
				} catch (NumberFormatException e) {
					System.err.println("Error: loadRotationMatrix, NumberFormatException: " + e.toString());
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("Error: loadRotationMatrix, IOException: " + e.toString());
					e.printStackTrace();
				}
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					System.err.println("Error: loadRotationMatrix, Can not close file: IOException: " + e.toString());
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Error: loadRotationMatrix, Can not find file: " + filename + e.toString());
			e.printStackTrace();
		}
		return M_;
	}

	public static <T> Function2<Worm, Worm, Worm> last() {
		return new Function2<Worm, Worm, Worm>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Worm call(Worm current, Worm next) {
				return next;
			}
		};
	}

	/**
	 * Mappers
	 */

	public <T> Function<Worm, Worm> neighbors() {
		return new Function<Worm, Worm>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Worm call(Worm v1) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

}
