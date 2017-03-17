
/**
 * 
 * @author Goutham
 *
 */

package com.ndsu.spark.GSO_Spark;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkStatusTracker;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.broadcast.Broadcast;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ndsu.spark.GSO_Spark.Beans.GSOBroadcaster;
import com.ndsu.spark.GSO_Spark.Beans.GSOConfig;
import com.ndsu.spark.GSO_Spark.Beans.Results;
import com.ndsu.spark.GSO_Spark.Beans.Worm;
import com.ndsu.spark.GSO_Spark.benchmark.CF4;
import com.ndsu.spark.GSO_Spark.benchmark.GSOBenchmark;
import com.ndsu.spark.GSO_Spark.utils.GSOBenchmarkHelper;

public class GSO_SparkMain {

	final static Logger logger = Logger.getLogger(GSO_SparkMain.class);
	private HashMap<String, GSOConfig> gsoConfigMap;
	private GSOConfig gsoConfig;
	
	//private static int 



	public static void main(String args[]){
		
		try {
			
			logger.info("***********GSO Algorithm on Spark Started*****************");
		//	String log4jConfigFile =  "resources/log4j.properties";
		//	PropertyConfigurator.configure(log4jConfigFile);

		GSO_SparkMain gsospark = new GSO_SparkMain();
		gsospark.execute();
	//		brCenters.destroy();
	

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.getMessage();
		} 

	}
	
	void execute() throws Exception
	{
		int iteration = 0;
//		GSOBroadcaster gsoBroadcaster = new GSOBroadcaster();
		gsoConfigMap = initialize();
		gsoConfig = gsoConfigMap.get("config1");
		
		
		logger.info("Using the following configuration for this execution: "+gsoConfig);
		
		

		//System.setProperty("hadoop.home.dir", "D:\\NDSU\\DrSimones_Lab");
		
		SparkConf sparkConf = new SparkConf().setAppName("GSO_Spark").setMaster(gsoConfig.getSparkMaster());
		JavaSparkContext sc = new JavaSparkContext(sparkConf);
		System.out.println("******************is Spark Master Set: "+sparkConf.get("spark.master"));
//		System.out.println();

	//	sc.hadoopConfiguration().set("dfs.nameservices","hadooptest");

		
		
		
		
		
		String peaksFile="resources/peaksfileCF4D"+gsoConfig.getDimension()+".txt";

		List<Worm> swarm = createInitialSwarm(gsoConfig.getSwarmSize(), gsoConfig.getDimension());
		
		double [][] peaks=new double[gsoConfig.getPeaksNo()][gsoConfig.getDimension()];
		peaks = readsPeaks(peaksFile,peaks);
		
		String outputDirectories = "output" + "/" + gsoConfig.getBenchName() +"dim"+gsoConfig.getDimension() +"rs"
				+ gsoConfig.getRs() + "it" + gsoConfig.getMaxIteration() + "ssize"
				+ gsoConfig.getSwarmSize() +"noNodes"+gsoConfig.getNoOfNOdes() +"_"+new SimpleDateFormat("MM_dd_yyyy_h_m_s").format(new Date()) ;
		
		boolean success = (new File(outputDirectories)).mkdirs();
		if (success) {
			System.out
			.println("Directories: " + outputDirectories + " created");
		}

		//double start = System.currentTimeMillis();
		double startsubtime = System.currentTimeMillis();
		double sumdiff = 0;
		//No idea whats happening here?
		GSOBenchmark bench = GSOBenchmarkHelper.getBenchMark(gsoConfig.getBenchName());
		CF4 cf4benchmark = (CF4)bench;
		cf4benchmark.setO_(loadOptima("resources/data/CF4_M_D" + gsoConfig.getDimension() + "_opt.dat", gsoConfig.getDimension()));
		cf4benchmark.setM_(loadRotationMatrix("resources/data/CF4_M_D" + gsoConfig.getDimension() + ".dat", gsoConfig.getDimension()));
		for (Worm worm : swarm) {
//			bench = new GSOBenchmark(gsoConfig.getBenchName(), worm.getposition());
//				double Jx = bench.getr();
			worm.setJx(cf4benchmark.evaluate(worm.getposition()));
							double l = (1 - gsoConfig.getP_const()) * worm.getluc()
									+ gsoConfig.getGamma() * worm.getJx();
			worm.setluc((1 - gsoConfig.getP_const()) * worm.getluc()
					+ gsoConfig.getGamma() * worm.getJx());
		}

		cf4benchmark.setM_(loadRotationMatrix("resources/data/CF4_M_D" + gsoConfig.getDimension() + ".dat", gsoConfig.getDimension()));
		cf4benchmark.setO_(loadOptima("resources/data/CF4_M_D" + gsoConfig.getDimension() + "_opt.dat", gsoConfig.getDimension()));
		
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
		Broadcast<List<Worm>> brCenters = null;
		JavaRDD<Worm> initialRDD;
		
		/**
		 * Accumulators
		 */
		WormAccumulator wormaccum = new WormAccumulator();
		sc.sc().register(wormaccum);
		
		List<Worm> swarm1 = new ArrayList<Worm>();

		while (iteration < gsoConfig.getMaxIteration())
		{
	
			startsubtime = System.currentTimeMillis();
			logger.info("******************Iteration: "+ iteration+" Started***************************");
			logger.info("Mapping and Reducing started");

			
			brCenters = sc.broadcast(swarm); 


			
			//Creating a parallelising statement
			//initialRDD = sc.parallelize(swarm, gsoConfig.getNoOfNOdes());
//			swarm = sc.parallelize(swarm, gsoConfig.getNoOfNOdes()).map(new GSOMapper1(gsoConfig, brCenters)).map(new GSOMapper2(gsoConfig, cf4benchmark)).collect();
//		sc.parallelize(swarm, gsoConfig.getNoOfNOdes()).map(new GSOMapper1(gsoConfig, brCenters)).map(new GSOMapper2(gsoConfig, cf4benchmark)).saveAsObjectFile("output/test/test"+iteration);;
			initialRDD = sc.parallelize(swarm, gsoConfig.getNoOfNOdes()).map(new GSOMapper1(gsoConfig, brCenters)).map(new GSOMapper2(gsoConfig, cf4benchmark));//.reduce(last());
//			initialRDD.collect();
			//swarm.clear();
			initialRDD.reduce(new GSOReducer());
			
//			reduce(new Function2<Worm, Worm, Worm>() {
//			    /**
//				 * 
//				 */
//				//private static final long serialVersionUID = 1L;
//
//				public Worm call(Worm i1, Worm i2) {
////					wormaccum.add(i1);
//			        return i2;
//			      }
//			    }
//			  );//map((x) -> { wormaccum.add(x); return f(x); });
//			swarm = wormaccum
			System.out.println("************Number of worms: "+swarm1.size()+"************************");
			logger.info("Mapping and Reducing Ended");
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
			System.gc();
			logger.info("******************Iteration: "+ iteration+" Ended***************************");

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
		sc.close();
	}

	//TODO
	//Deletion of this method is required later
	private HashMap<String, GSOConfig> initialize() throws JsonSyntaxException, JsonIOException, IOException{
		String sLine = "";
		try (BufferedReader br = new BufferedReader(new FileReader("resources/config.json"))) {

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
	private HashMap<String, GSOConfig> initialize(String json) throws JsonSyntaxException, JsonIOException, IOException{
		Type type = new TypeToken<HashMap<String, GSOConfig>>(){}.getType();

		Gson gson = new Gson();
		HashMap<String, GSOConfig>  configMap= gson.fromJson(json, type);		
		return (configMap);


	}

	/**
	 * Creates swarm with some random values
	 * @param swarm
	 * @return
	 * @throws Exception 
	 */
	public List<Worm> createInitialSwarm(int swarmSize, int dimension) throws Exception {
		//Worm swarm[] = new Worm[swarmSize];
		List<Worm> swarm = new LinkedList<Worm>();
		Random aRandom = new Random();
		GSOBenchmark bench;
		bench = GSOBenchmarkHelper.getBenchMark(gsoConfig.getBenchName());
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
	private void writeSwarmwithoutnb(List<Worm> swarm, String fileNameOut,
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



	private void fill(List<Worm> swarm, int it, Results[] result) {
		for (int i = 0; i < gsoConfig.getDimension(); i++) {
			int j=0;
			for (Worm worm : swarm) {
				result[i].setval(j, it, worm.getposition()[i]);
				j++;
			}
		}
	}

		
	private void fillresultPerIteration(List<Worm> swarm,
			double[][] resultPerIteration) {
		int i=0;
		for (Worm worm : swarm) {
			for (int j = 0; j < gsoConfig.getDimension(); j++) {
				resultPerIteration[i][j] = worm.getposition()[j];

			}
			i++;
		}
	}
	
	private double calcuatecrate(double[][] resultPerIteration,
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
	
	private double calcuateavgdist(double[][] resultPerIteration,
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


	private void writeResults(String outputDirectories, Results[] result)
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
	
	public double[][] readsPeaks(String fname, double[][] peaks)
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
	
	
	
	
	
	double [][] loadOptima(final String filename,int dim) 
	{
		int nofunc_=8;
		double [][] O_;
		O_  	= new double[nofunc_][dim];
		File file = new File(filename); 
		try {
			LineNumberReader reader = new LineNumberReader( new FileReader( file ) );
			try {
				String buffer;	
				try {
					for (int i=0; i<nofunc_; ++i) {
						buffer = reader.readLine();
						
						double tmp = 0;
						String[] numbers = buffer.split("\t");
						for( int j = 0; j < dim; ++j ) {
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
			System.err.println("Error: loadOptima, Can not find file: " + filename +e.toString());
			e.printStackTrace();
		} 
		return O_;
	}
	double [][][] loadRotationMatrix(final String filename, int dim)
	{	
		int nofunc_=8;
		double [][][] M_ = 	new double[nofunc_][dim][dim];
		File file = new File(filename); 
		try {
			LineNumberReader reader = new LineNumberReader( new FileReader( file ) );
			try {
				String buffer;	
				try {
				    double tmp = -1;
				    for (int i=0; i<nofunc_; ++i) {
				        for (int j=0; j<dim; ++j) {
							buffer = reader.readLine();
							String[] numbers = buffer.split("\t");
				            for (int k=0; k<dim; ++k) {
				            	tmp = Double.parseDouble(numbers[k].trim());
				                M_[i][j][k] = tmp;
				             //   System.out.print(","+M_[i][j][k]);
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
			System.err.println("Error: loadRotationMatrix, Can not find file: " + filename +e.toString());
			e.printStackTrace();
		} 
		return M_;
	}
	
	public static <T> Function2<Worm,Worm,Worm> last() {
		  return new Function2<Worm,Worm,Worm>() {
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
}
