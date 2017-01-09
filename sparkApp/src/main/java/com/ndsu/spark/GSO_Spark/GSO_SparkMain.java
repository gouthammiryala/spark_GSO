
package com.ndsu.spark.GSO_Spark;
import java.io.BufferedReader;
import java.io.File;
/**
 * 
 * @author Goutham
 *
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.omg.CORBA.INITIALIZE;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.ndsu.spark.GSO_Spark.Beans.GSOConfig;
import com.ndsu.spark.GSO_Spark.Beans.Worm;
import com.ndsu.spark.GSO_Spark.benchmark.GSOBenchmark;

public class GSO_SparkMain {
	
	final static Logger logger = Logger.getLogger(GSO_SparkMain.class);
	private static GSOConfig gsoConfig;
	

	public static void main(String args[]){
		int iteration = 0;
		try {
			String log4jConfigFile = System.getProperty("user.dir")
	                + File.separator + "\\src\\resources\\log4j.properties";
			PropertyConfigurator.configure(log4jConfigFile);
			logger.info("***********GSO Algorithm on Spark Started*****************");
			gsoConfig = initialize();
			Worm swarm[] = createInitialSwarm(gsoConfig.getSwarmSize(), gsoConfig.getDimension());

			Configuration conf = new Configuration();

			 conf.set("fs.default.name", "hdfs://127.0.0.1:9000"); //local file system
			 
			 String swarmInFolder = gsoConfig.getPathy() + "files/t_" + (iteration);
				FileSystem fs = FileSystem.get(conf);
				fs.delete(new Path(swarmInFolder), true);

				fs.mkdirs(new Path(swarmInFolder));
				String swarmInFile = swarmInFolder + "/swarm0";
				conf.set("swarm.file", "" + swarmInFile);
				writeSwarmwithoutnb(swarm, swarmInFile, conf);

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
		try (BufferedReader br = new BufferedReader(new FileReader("D:\\NDSU\\DrSimones Lab\\spark_GSO\\config.json"))) {

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
	public static Worm[] createInitialSwarm(int swarmSize, int dimension) {
		Worm swarm[] = new Worm[swarmSize];
		Random aRandom = new Random();
		GSOBenchmark bench;
		bench = new GSOBenchmark(gsoConfig.getBenchName());
		double aStart = bench.getMin();
		double aEnd = bench.getMax();
		double range = (double) aEnd - (double) aStart;

		for (int i = 0; i < swarmSize; i++) {
			swarm[i] = new Worm();
			// random position
			double position[] = new double[dimension];
			for (int a = 0; a < dimension; a++) {
				double fraction = (double) (range * aRandom.nextDouble());
				double randomNumber = (double) (fraction + aStart);
				position[a] = randomNumber;
				// System.out.print(" position----->  "+ position[a]+"\t");
			}
			// System.out.println();
			swarm[i].setID(i);
			swarm[i].setPosition(position);
			swarm[i].setluc(gsoConfig.getL0());
			swarm[i].setRd(gsoConfig.getR0());

		}
		return swarm;
	}
	/**
	 * 
	 * @param a
	 */
	private static void writeSwarmwithoutnb(Worm swarm[], String fileNameOut,
			Configuration conf) throws IOException {
		FileSystem fs = FileSystem.get(conf);
		Path outPath = new Path(fileNameOut);
		fs.delete(outPath, true);
		FSDataOutputStream outy = fs.create(outPath);
		for (int i = 0; i < gsoConfig.getSwarmSize(); i++) {
			outy.writeBytes(i + "\t");
			String st = "";
			for (int w = 0; w < gsoConfig.getDimension() - 1; w++) {
				st = st + (swarm[i].getposition()[w] + ",");
			}
			st = st + (swarm[i].getposition()[gsoConfig.getDimension() - 1] + ";");
			st = st + (swarm[i].getJx() + ";");
			st = st + (swarm[i].getluc() + ";");
			st = st + (swarm[i].getRd() + ";");

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
}
