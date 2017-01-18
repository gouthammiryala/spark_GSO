
package com.ndsu.spark.GSO_Spark;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
/**
 * 
 * @author Goutham
 *
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.omg.CORBA.INITIALIZE;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.ndsu.spark.GSO_Spark.Beans.GSOConfig;
import com.ndsu.spark.GSO_Spark.Beans.Results;
import com.ndsu.spark.GSO_Spark.Beans.Worm;
import com.ndsu.spark.GSO_Spark.benchmark.GSOBenchmark;

public class GSO_SparkMain {

	final static Logger logger = Logger.getLogger(GSO_SparkMain.class);
	private static GSOConfig gsoConfig;


	public static void main(String args[]){
		int iteration = 0;
		try {

			System.setProperty("hadoop.home.dir", "D:\\NDSU\\DrSimones_Lab");
			SparkConf sparkConf = new SparkConf().setAppName("testApp").setMaster("local");
			String peaksFile="D:\\NDSU\\GSO_MapReduce\\config\\peaksfilej6";
			// conf.s
			JavaSparkContext sc = new JavaSparkContext(sparkConf);


			sc.hadoopConfiguration().set("dfs.nameservices","hadooptest");

			String log4jConfigFile = System.getProperty("user.dir")
					+ File.separator + "\\src\\resources\\log4j.properties";
			PropertyConfigurator.configure(log4jConfigFile);
			logger.info("***********GSO Algorithm on Spark Started*****************");
			gsoConfig = initialize();
			List<Worm> swarm = createInitialSwarm(gsoConfig.getSwarmSize(), gsoConfig.getDimension());
			
			double [][] peaks=new double[gsoConfig.getPeaksNo()][gsoConfig.getDimension()];
			peaks = readsPeaks(peaksFile,peaks);
			
			String outputDirectories = "src/main/output" + "/" + gsoConfig.getBenchName() +"dim"+gsoConfig.getDimension() +"rs"
					+ gsoConfig.getRs() + "it" + gsoConfig.getMaxIteration() + "ssize"
					+ gsoConfig.getSwarmSize() +"noNodes"+gsoConfig.getNoOfNOdes() +"_" ;
			
			boolean success = (new File(outputDirectories)).mkdirs();
			if (success) {
				System.out
				.println("Directories: " + outputDirectories + " created");
			}

			double start = System.currentTimeMillis();
			double startsubtime = System.currentTimeMillis();
			double sumdiff = 0;
			//No idea whats happening here?
			GSOBenchmark bench;
			for (Worm worm : swarm) {
				bench = new GSOBenchmark(gsoConfig.getBenchName(), worm.getposition());
				//	double Jx = bench.getr();
				worm.setJx(bench.getr());
				//				double l = (1 - Settings.p_const) * swarm[i].getluc()
				//						+ Settings.Gama * swarm[i].getJx();
				worm.setluc((1 - gsoConfig.getP_const()) * worm.getluc()
						+ gsoConfig.getGamma() * worm.getJx());
			}

			Configuration conf = new Configuration();

			conf.set("fs.default.name", "hdfs://127.0.0.1:9000"); //local file system

			String swarmInFolder = gsoConfig.getPathy() + "files/t_" + (iteration);
			FileSystem fs = FileSystem.get(conf);
			fs.delete(new Path(swarmInFolder), true);

			fs.mkdirs(new Path(swarmInFolder));
			String swarmInFile = swarmInFolder + "/swarm0";
			conf.set("swarm.file", "" + swarmInFile);
			writeSwarmwithoutnb(swarm, swarmInFile, conf);
			
			Results[] result = new Results[gsoConfig.getDimension()];
			double [][] resultPerIteration=new
					double[gsoConfig.getSwarmSize()][gsoConfig.getDimension()];

			for (int i = 0; i < gsoConfig.getDimension(); i++) {
				result[i] = new Results(gsoConfig.getSwarmSize(), gsoConfig.getMaxIteration());
			}
			
			
			FileWriter fstream1 = new
					FileWriter(outputDirectories+"/peakcapture.txt");
			BufferedWriter outpeakcapture = new BufferedWriter(fstream1);

			FileWriter fstream2 = new
					FileWriter(outputDirectories+"/avgdist.txt");
			BufferedWriter outavgdist = new BufferedWriter(fstream2);


			FileWriter fstream3 = new
					FileWriter(outputDirectories+"/timeinfo.txt");
			BufferedWriter outtime = new BufferedWriter(fstream3);
			//List<Worm>
			
			while (iteration < gsoConfig.getMaxIteration())
			{
				Broadcast<List<Worm>> brCenters = sc.broadcast(swarm); 


				logger.info("******************Iteration: "+ iteration+"***************************");
				//Creating a parallelising statement
				JavaRDD<Worm> initialRDD = sc.parallelize(swarm);
				//initialRDD.ma
				//JavaRDD<Worm> firstMap = initialRDD.map(new GSOMapper1(gsoConfig, brCenters));
				//initialRDD = firstMap.map(new GSOMapper2(gsoConfig));
				
				swarm = initialRDD.map(new GSOMapper1(gsoConfig, brCenters)).map(new GSOMapper2(gsoConfig)).collect();
				//initialRDD = initialRDD.map(new GSOMapper2(gsoConfig));			
				//swarm =initialRDD.collect();
				
				//JavaRDD<String> saveRDD = initialRDD.map(worm -> worm.toString());
				//saveRDD.saveAsTextFile("c:\\tmp\\hadoop\\"+System.currentTimeMillis());
				
				
				double endsubtime = System.currentTimeMillis();
				double diff = (endsubtime - startsubtime) / 1000;
				sumdiff = sumdiff + diff;
				
				 fill(swarm, iteration, result);
				 fillresultPerIteration(swarm,resultPerIteration);
				double avdist=calcuateavgdist(resultPerIteration,peaks);
				outavgdist.write(iteration+"\t"+avdist+"\n");
				System.out.println("avdist at "+iteration+" iteration="+avdist+"\n");
				double crate=calcuatecrate(resultPerIteration,peaks);
				System.out.println("crate at "+iteration+" iteration="+crate+"\n");
				outpeakcapture.write(iteration+"\t"+crate+"\n");
				outtime.write(iteration+"\t"+diff+"\n"); 
				outpeakcapture.flush();
				outavgdist.flush();
				outtime.flush();
				iteration++;
			}
			if (iteration == (gsoConfig.getMaxIteration())) {
				 writeResults(outputDirectories, result);
				 outtime.append("===============================================================\n");
				 outtime.append("Total Time for "+gsoConfig.getMaxIteration()+" Iteartions:"+sumdiff+" (s)\n");
				 outtime.append("Average Time per iteration :"+(sumdiff/gsoConfig.getMaxIteration())+" (s)\n");
				 outtime.append("===============================================================\n");
				 outtime.append("#nodes:"+gsoConfig.getNoOfNOdes()+"\n");
				
				 
				 outpeakcapture.close();
				 outavgdist.close();
				 outtime.close();
			}

			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.getMessage();
		} 

	}

	//TODO
	//Deletion of this method is required later
	private static GSOConfig initialize() throws JsonSyntaxException, JsonIOException, IOException{
		String sLine = "";
		try (BufferedReader br = new BufferedReader(new FileReader("D:\\NDSU\\DrSimones_Lab\\spark_GSO\\config.json"))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				sLine= sLine+sCurrentLine;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return initialize(sLine);
	}


	/**
	 * Initialize the main class with all the required values here.
	 * @throws JsonIOException 
	 * @throws JsonSyntaxException 
	 * @throws IOException 
	 */
	private static GSOConfig initialize(String json) throws JsonSyntaxException, JsonIOException, IOException{
		Gson gson = new Gson();
		GSOConfig config = gson.fromJson(json, GSOConfig.class);		
		return (config);


	}

	/**
	 * Creates swarm with some random values
	 * @param swarm
	 * @return
	 */
	public static List<Worm> createInitialSwarm(int swarmSize, int dimension) {
		//Worm swarm[] = new Worm[swarmSize];
		List<Worm> swarm = new LinkedList<Worm>();
		Random aRandom = new Random();
		GSOBenchmark bench;
		bench = new GSOBenchmark(gsoConfig.getBenchName());
		double aStart = bench.getMin();
		double aEnd = bench.getMax();
		double range = (double) aEnd - (double) aStart;

		for (int i = 0; i < swarmSize; i++) {
			Worm temp = new Worm();
			// random position
			double position[] = new double[dimension];
			for (int a = 0; a < dimension; a++) {
				double fraction = (double) (range * aRandom.nextDouble());
				double randomNumber = (double) (fraction + aStart);
				position[a] = randomNumber;
				// System.out.print(" position----->  "+ position[a]+"\t");
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
	private static void writeSwarmwithoutnb(List<Worm> swarm, String fileNameOut,
			Configuration conf) throws IOException {
		FileSystem fs = FileSystem.get(conf);
		Path outPath = new Path(fileNameOut);
		fs.delete(outPath, true);
		FSDataOutputStream outy = fs.create(outPath);
		for (Worm worm : swarm) {
			outy.writeBytes(worm.getID() + "\t");
			String st = "";
			for (int w = 0; w < gsoConfig.getDimension() - 1; w++) {
				st = st + (worm.getposition()[w] + ",");
			}
			st = st + (worm.getposition()[gsoConfig.getDimension() - 1] + ";");
			st = st + (worm.getJx() + ";");
			st = st + (worm.getluc() + ";");
			st = st + (worm.getRd() + ";");

			// System.out.println("W"+i+":"+st+"\n");
			try{
				outy.writeBytes(st);
				outy.writeBytes("\n");
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}

		}
		outy.close();
	}



	private static void fill(List<Worm> swarm, int it, Results[] result) {
		for (int i = 0; i < gsoConfig.getDimension(); i++) {
			int j=0;
			for (Worm worm : swarm) {
				result[i].setval(j, it, worm.getposition()[i]);
				j++;
			}
		}
	}

		
	private static void fillresultPerIteration(List<Worm> swarm,
			double[][] resultPerIteration) {
		int i=0;
		for (Worm worm : swarm) {
			for (int j = 0; j < gsoConfig.getDimension(); j++) {
				resultPerIteration[i][j] = worm.getposition()[j];

			}
			i++;
		}
	}
	
	private static double calcuatecrate(double[][] resultPerIteration,
			double[][] peaks) {
		double crate = 0;
		int nwormsclosed = 3;
		double epsilon = 0.05;
		double[] gwormsInsidePeaks = new double[gsoConfig.getPeaksNo()];
		for (int i = 0; i < gsoConfig.getSwarmSize(); i++) {
			for (int k = 0; k < gsoConfig.getPeaksNo(); k++) {
				double dist = 0;
				for (int j = 0; j < gsoConfig.getDimension(); j++) {
					dist = dist
							+ Math.pow(
									(resultPerIteration[i][j] - peaks[k][j]), 2);
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

//	private static double calcuateavgdist(double[][] resultPerIteration,
//			double[][] peaks) {
//		double sum = 0;
//		for (int i = 0; i < gsoConfig.getSwarmSize(); i++) {
//			double minseg = 0;
//			for (int j = 0; j < gsoConfig.getDimension(); j++) {
//				minseg = minseg
//						+ Math.pow((resultPerIteration[i][j] - peaks[0][j]), 2);
//			}
//			minseg = Math.sqrt(minseg);
//
//			for (int k = 1; k < gsoConfig.getPeaksNO(); k++) {
//				double seg = 0;
//				for (int j = 0; j < gsoConfig.getDimension(); j++) {
//					seg = seg
//							+ Math.pow(
//									(resultPerIteration[i][j] - peaks[k][j]), 2);
//				}
//				seg = Math.sqrt(seg);
//				if (seg <= minseg) {
//					minseg = seg;
//				}
//			}
//			sum = sum + minseg;
//		}
//
//		double disAvg = sum / (gsoConfig.getStep() * 1.0);
//
//		return disAvg;
//	}
	
	private static double calcuateavgdist(double[][] resultPerIteration,
			double[][] peaks) {
		double sum = 0;
		for (int i = 0; i < gsoConfig.getSwarmSize(); i++) {
			double minseg = 0;
			for (int j = 0; j < gsoConfig.getDimension(); j++) {
				minseg = minseg
						+ Math.pow((resultPerIteration[i][j] - peaks[0][j]), 2);
			}
			minseg = Math.sqrt(minseg);

			for (int k = 1; k < gsoConfig.getPeaksNo(); k++) {
				double seg = 0;
				for (int j = 0; j < gsoConfig.getDimension(); j++) {
					seg = seg
							+ Math.pow(
									(resultPerIteration[i][j] - peaks[k][j]), 2);
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


	private static void writeResults(String outputDirectories, Results[] result)
			throws IOException {

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
	
	public static double[][] readsPeaks(String fname, double[][] peaks)
			throws NumberFormatException, IOException {

		BufferedReader in = new BufferedReader(new FileReader(fname));
		// Read File Line By Line
		int sc = 0;
		String strLine;
		while((strLine=in.readLine())!=null) {
			   
			    strLine = strLine.trim();
			  //  System.out.println(">>>> "+strLine);
			    String[] cont = strLine.split("\t");
			
				// Print the content on the console
			   
				 
				for (int i = 0; i < gsoConfig.getDimension(); i++) {
				//	System.out.println("before fill peaks ");
					peaks[sc][i] = Double.parseDouble(cont[i]);
				//	System.out.println(">>>> "+peaks[sc][i] );
				}
				sc=sc+1;
			}
		in.close();
		 System.out.println("peaks reading done: "+sc);
		 return peaks;
		 
	}
}
