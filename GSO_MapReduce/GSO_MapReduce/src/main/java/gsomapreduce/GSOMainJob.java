package gsomapreduce;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.apache.hadoop.mapreduce.Job;
///ort org.apache.hadoop.mapreduce.lib.input.FileInputFormat; split remove it use b\new one
//import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import benchmark.benchmark;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class GSOMainJob {
	private static final Log LOG = LogFactory.getLog(GSOMainJob.class);
	private static DecimalFormat twoDForm = new DecimalFormat("#.##");
	static worm globalMAXworm = new worm();
	static worm globalMINworm = new worm();
	
	private static Settings settings;

	public static worm[] initial(worm swarm[]) {
		Random aRandom = new Random();
		benchmark bench;
		bench = new benchmark(settings.getBenchName());
		double aStart = bench.getMin();
		double aEnd = bench.getMax();
		double range = (double) aEnd - (double) aStart;

		for (int i = 0; i < settings.getSwarmSize(); i++) {
			swarm[i] = new worm();
			// random position
			double position[] = new double[settings.getDimension()];
			for (int a = 0; a < settings.getDimension(); a++) {
				double fraction = (double) (range * aRandom.nextDouble());
				double randomNumber = (double) (fraction + aStart);
				position[a] = randomNumber;
				// System.out.print(" position----->  "+ position[a]+"\t");
			}
			// System.out.println();
			swarm[i].setID(i);
			swarm[i].setPosition(position);
			swarm[i].setluc(settings.getL0());
			swarm[i].setRd(settings.getR0());

		}
		return swarm;
	}

	public static List<worm> findNeighbors(worm swarm[], worm w) {
		List<worm> wormNeighbors = new ArrayList<worm>();

		double PositionJ[] = w.getposition();
		// System.out.println(w.getID());
		for (int x = 0; x < settings.getSwarmSize(); x++) {
			double distance, sum = 0;
			if (swarm[x].getID() != w.getID()) {
				// System.out.println(swarm[x].getID()+" "+w.getID());
				double PositionI[] = swarm[x].getposition();
				for (int a = 0; a < settings.getDimension(); a++) {
					sum = sum + Math.pow(PositionI[a] - PositionJ[a], 2);
				}

				distance = Math.sqrt(sum); // Euclidean Distance

				if (distance < w.getRd() && w.getluc() < swarm[x].getluc()) {
					wormNeighbors.add(swarm[x]);
				}
			}
		}
		return wormNeighbors;
	}

	private static void printnb(List<worm> wormNeighbors) {
		for (int i = 0; i < wormNeighbors.size(); i++) {
			double position[] = new double[settings.getDimension()];
			position = wormNeighbors.get(i).getposition();
//SL			System.out.println("n" + wormNeighbors.get(i).getID() + "\t");
			for (int a = 0; a < settings.getDimension(); a++) {
//SL				System.out.print("P" + a + ": " + position[a] + "\t");
			}
//SL			System.out.print("Luc:" + wormNeighbors.get(i).getluc() + "\t");
//SL			System.out.println("Rd:" + wormNeighbors.get(i).getRd() + "\t");
			// System.out.println("");
		}
	}

	private static void printSwarm(worm swarm[]) {
		for (int i = 0; i < settings.getSwarmSize(); i++) {
			double position[] = new double[settings.getDimension()];
			position = swarm[i].getposition();
//SL			System.out.print("W" + i + "\t");
			for (int a = 0; a < settings.getDimension(); a++) {
//SL				System.out.print("P" + a + ": " + position[a] + "\t");
			}
//SL			System.out.print("Jx:" + swarm[i].getJx() + "\t");
//SL			System.out.print("Luc:" + swarm[i].getluc() + "\t");
//SL			System.out.println("Rd:" + swarm[i].getRd() + "\t");
			// System.out.println("");
		}
	}

	private static void writeSwarm(worm swarm[], String fileNameOut,
			Configuration conf) throws IOException {
		FileSystem fs = FileSystem.get(conf);
		Path outPath = new Path(fileNameOut);
		fs.delete(outPath, true);
		FSDataOutputStream outy = fs.create(outPath);
		for (int i = 0; i < settings.getSwarmSize(); i++) {
			outy.writeBytes(i + "\t");
			String st = "";
			for (int w = 0; w < settings.getDimension() - 1; w++) {
				st = st + (swarm[i].getposition()[w] + ",");
			}
			st = st + (swarm[i].getposition()[settings.getDimension() - 1] + ";");
			st = st + (swarm[i].getJx() + ";");
			st = st + (swarm[i].getluc() + ";");
			st = st + (swarm[i].getRd() + ";");

			st = st + (swarm[i].getwormNeighbors().size() + ";");
			for (int w = 0; w < swarm[i].getwormNeighbors().size(); w++) {
				st = st + (swarm[i].getwormNeighbors().get(w).getID() + ";");
				for (int j = 0; j < settings.getDimension() - 1; j++) {
					st = st
							+ (swarm[i].getwormNeighbors().get(w).getposition()[j] + ",");
				}
				st = st
						+ (swarm[i].getwormNeighbors().get(w).getposition()[settings.getDimension() - 1] + ";");
				st = st + (swarm[i].getwormNeighbors().get(w).getJx() + ";");
				st = st + (swarm[i].getwormNeighbors().get(w).getluc() + ";");
				st = st + (swarm[i].getwormNeighbors().get(w).getRd() + ";");

			}
			// System.out.println("W"+i+":"+st+"\n");
			outy.writeBytes(st);
			outy.writeBytes("\n");
		}
		outy.close();
	}

	private static void writeSwarmwithoutnb(worm swarm[], String fileNameOut,
			Configuration conf) throws IOException {
		FileSystem fs = FileSystem.get(conf);
		Path outPath = new Path(fileNameOut);
		fs.delete(outPath, true);
		FSDataOutputStream outy = fs.create(outPath);
		for (int i = 0; i < settings.getSwarmSize(); i++) {
			outy.writeBytes(i + "\t");
			String st = "";
			for (int w = 0; w < settings.getDimension() - 1; w++) {
				st = st + (swarm[i].getposition()[w] + ",");
			}
			st = st + (swarm[i].getposition()[settings.getDimension() - 1] + ";");
			st = st + (swarm[i].getJx() + ";");
			st = st + (swarm[i].getluc() + ";");
			st = st + (swarm[i].getRd() + ";");

			// System.out.println("W"+i+":"+st+"\n");
			outy.writeBytes(st);
			outy.writeBytes("\n");
		}
		outy.close();
	}

	private static worm[] readSwarm(String fileNameIn, Configuration conf)
			throws IOException {
		worm swarm[] = new worm[settings.getSwarmSize()];

		FileSystem fs = FileSystem.get(conf);
		Path result = new Path(fileNameIn);
		FileStatus[] stati = fs.listStatus(result);
		for (FileStatus status : stati) {
			if (!status.isDir()) {
				String string = status.getPath().toString();
				int indexx = string.indexOf("_SUCCESS");
				if (indexx == -1) {
					Path path = status.getPath();
					FSDataInputStream in = fs.open(path);

					String x = in.readLine();
					int count = 0;

					while (x != null) {

						String[] keyAndValue = x.split("\t");
						int wind = Integer.parseInt(keyAndValue[0]);
						swarm[wind] = new worm();
						swarm[wind].setID(Integer.parseInt(keyAndValue[0]));
						String[] line = keyAndValue[1].toString().split(";");
						String[] linepos = line[0].toString().split(",");
						double[] pos = new double[settings.getDimension()];
						for (int k = 0; k < settings.getDimension(); k++) {
							pos[k] = Double.parseDouble(linepos[k]);
						}
						swarm[wind].setPosition(pos);
						swarm[wind].setJx(Double.parseDouble(line[1]));
						swarm[wind].setluc(Double.parseDouble(line[2]));
						swarm[wind].setRd(Double.parseDouble(line[3]));
						x = in.readLine();
					}
					in.close();

				}

			}
		}
		return swarm;

	}

	private static void fill(worm swarm[], int it, Results[] result) {
		for (int i = 0; i < settings.getDimension(); i++) {
			for (int j = 0; j < settings.getSwarmSize(); j++) {
				result[i].setval(j, it, swarm[j].getposition()[i]);
			}
		}
	}

	private static void fillresultPerIteration(worm swarm[],
			double[][] resultPerIteration) {
		for (int i = 0; i < settings.getSwarmSize(); i++) {
			for (int j = 0; j < settings.getDimension(); j++) {
				resultPerIteration[i][j] = swarm[i].getposition()[j];

			}
		}
	}

	private static void writeResults(String outputDirectories, Results[] result)
			throws IOException {

		for (int k = 0; k < settings.getDimension(); k++) {

			FileWriter fstreamX = new FileWriter(outputDirectories + "/d" + k);
			BufferedWriter outX = new BufferedWriter(fstreamX);
			for (int i = 0; i < settings.getSwarmSize(); i++) {
				String rowX = "";
				for (int a = 0; a < settings.getMaxIteration() - 1; a++) {
					rowX = rowX + result[k].getval(i, a) + ",";
				}
				rowX = rowX + result[k].getval(i, settings.getMaxIteration() - 1);
				outX.write(rowX + "\n");

			}
			outX.close();
		}

	}

	public static void MinJx(worm[] swarm, worm globalworm, int it) {
		int index = 0;
		for (int i = 0; i < swarm.length; i++) {

			if (swarm[i].getJx() < globalworm.getJx()) {

				globalworm.setPosition(swarm[i].getposition());
				globalworm.setJx(swarm[i].getJx());
				globalworm.setID(it);

			}
		}
		System.out.println("MIN Jx= " + globalworm.getJx()
				+ " Exist at iteration:" + globalworm.getID());
		System.out.print("Position: ");
		for (int z = 0; z < settings.getDimension(); z++) {
			System.out
					.print("D" + z + ": " + globalworm.getposition()[z] + " ");
		}
		System.out.println();

	}

	public static void MaxJx(worm[] swarm, worm globalworm, int it) {
		int index = 0;
		for (int i = 0; i < swarm.length; i++) {
			if (swarm[i].getJx() > globalworm.getJx()) {
				// System.out.println(">>>> "+
				// globalworm.getJx()+" vs "+swarm[i].getJx());
				globalworm.setPosition(swarm[i].getposition());
				globalworm.setJx(swarm[i].getJx());
				globalworm.setID(it);

			}
		}
		System.out.println("MAX Jx= " + globalworm.getJx()
				+ " Exist at iteration:" + globalworm.getID());
		System.out.print("Position: ");
		for (int z = 0; z < settings.getDimension(); z++) {
			System.out
					.print("D" + z + ": " + globalworm.getposition()[z] + " ");
		}
		System.out.println();
	}

	public static void readsPeaks(String fname, double[][] peaks)
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
			   
				 
				for (int i = 0; i < settings.getDimension(); i++) {
				//	System.out.println("before fill peaks ");
					peaks[sc][i] = Double.parseDouble(cont[i]);
				//	System.out.println(">>>> "+peaks[sc][i] );
				}
				sc=sc+1;
			}
		in.close();
		 System.out.println("peaks reading done: "+sc);
	}

	public static void main(String[] args) throws IOException,
			InterruptedException, ClassNotFoundException {

		String configFile = "resources/config.json";//args[0];// "config.txt";
	   
	    if (args.length != 2)
	    {
	    	throw new IllegalArgumentException("\nNumber of arguments should be 2: "
	    			+ "\n1. Config \n2. Numberofnodes");
	    }
	    
	    initialize(configFile, args[0]);
	    settings.setNoOfNOdes(Integer.parseInt(args[1]));
		System.out.println(settings);

	    
		//Settings settings = new Settings(configFile);
	    String peaksFile="resources/peaksfileCF4D"+ settings.getDimension() + ".txt";//"peaksFile.txt";
		settings.getClass();

		String outputDirectories = "mrout" + "/" + settings.getBenchName() +"dim"+settings.getDimension() +"rs"
				+ settings.getRs() + "it" + settings.getMaxIteration() + "ssize"
				+ settings.getSwarmSize() +"noNodes"+settings.getNoOfNOdes() +"_" 
				+ new SimpleDateFormat("MM_dd_yyyy_h_m_s").format(new Date());

		boolean success = (new File(outputDirectories)).mkdirs();
		if (success) {
			System.out
					.println("Directories: " + outputDirectories + " created");
		}

		worm swarm1[] = new worm[settings.getSwarmSize()];
		worm swarm[] = new worm[settings.getSwarmSize()];
		double start = System.currentTimeMillis();
		double startsubtime = System.currentTimeMillis();
		double sumdiff = 0;

		swarm = initial(swarm1);
		printSwarm(swarm);
		 double [][] peaks=new double[settings.getPeaksNO()][settings.getDimension()];
		 readsPeaks(peaksFile,peaks);
		benchmark bench;
		for (int i = 0; i < settings.getSwarmSize(); i++) {
			bench = new benchmark(settings.getBenchName(), swarm[i].getposition());
			double Jx = bench.getr();
			swarm[i].setJx(Jx);
			double l = (1 - settings.getP_const()) * swarm[i].getluc()
					+ settings.getGamma() * swarm[i].getJx();
			swarm[i].setluc(l);
		}

		int iteration = 0;
		Configuration conf = new Configuration();

//		 conf.set("fs.default.name", "hdfs://127.0.0.1:9000"); //local file system
//		 conf.set("mapred.job.tracker","yarn"); //local tracker
		// conf.set("io.sort.factor","20");
		// conf.set("io.sort.mb","200");
		// conf.set("io.sort.record.percent","0.15");
		// conf.set("mapred.job.reuse.jvm.num.tasks","-1");
		// conf.set("io.sort.spill.percent","0.80");
		// conf.set("mapred.inmem.merge.threshold","1000");
		// conf.set("mapred.compress.map.output", "true");
		// conf.set("mapred.output.compression.type", "BLOCK");
		// conf.set("mapred.map.output.compression.codec",
		// "org.apache.hadoop.io.compress.DefaultCodec");
		// conf.set("mapred.child.java.opts","-Xmx3048M");

		conf.set("worm.dim", "" + settings.getDimension()); // dimensions
		conf.set("swarm.size", "" + settings.getSwarmSize()); // swarm size
		conf.set("p", "" + settings.getP_const());
		conf.set("gama", "" + settings.getGamma());
		conf.set("B", "" + settings.getB());
		conf.set("nt", "" + settings.getNt());
		conf.set("step", "" + settings.getStep());
		conf.set("l0", "" + settings.getL0());
		conf.set("r0", "" + settings.getR0());
		conf.set("rs", "" + settings.getRs());
		conf.set("benchName", "" + settings.getBenchName());

		String swarmInFolder = settings.getPathy() + "files/t_" + (iteration);
		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path(swarmInFolder), true);

		fs.mkdirs(new Path(swarmInFolder));
		String swarmInFile = swarmInFolder + "/swarm0";
		conf.set("swarm.file", "" + swarmInFile);

		System.out.println("done1");
		writeSwarmwithoutnb(swarm, swarmInFile, conf);
		System.out.println("done2");

		Path InFile = new Path(settings.getPathy() + "files/t_" + (iteration) + "/");
		Path outFile = new Path(settings.getPathy() + "files/t_" + (iteration + 1)
				+ "/");
		fs.delete(outFile, true);

		Job job = new Job(conf);
		job.setJobName("GSO" + iteration);
		long lenth = fs.getContentSummary(InFile).getLength();
		long splitSize = lenth / (settings.getNoOfNOdes());
		FileInputFormat.setMaxInputSplitSize(job, (splitSize));

		job.setMapperClass(GSOMapper.class);
		job.setReducerClass(GSOReducer.class);

		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setInputFormatClass(KeyTextInputFormat.class);
		FileInputFormat.addInputPath(job, InFile);
		FileOutputFormat.setOutputPath(job, outFile);
		job.setJarByClass(GSOMainJob.class);

		// the job using just the MAPPER function.
		job.setNumReduceTasks(1);
		// job.setNumReduceTasks(0);
		System.out.println("*IBRAHIM: ****JOB1 START****");

		job.waitForCompletion(true);
		double endsubtime = System.currentTimeMillis();
		double diff = (endsubtime - startsubtime) / 1000;
		sumdiff = sumdiff + diff;

		Results[] result = new Results[settings.getDimension()];
	    double [][] resultPerIteration=new
		double[settings.getSwarmSize()][settings.getDimension()];

		for (int i = 0; i < settings.getDimension(); i++) {
			result[i] = new Results(settings.getSwarmSize(), settings.getMaxIteration());
		}

		worm swarmPr[] = new worm[settings.getSwarmSize()];
		swarmPr = readSwarm(outFile.toString(), conf);
		// printSwarm(swarmPr);
		
		fill(swarmPr, iteration, result);
		
		fillresultPerIteration(swarmPr,resultPerIteration);
		
		 FileWriter fstream1 = new
		 FileWriter(outputDirectories+"/peakcapture.txt");
		 BufferedWriter outpeakcapture = new BufferedWriter(fstream1);

		 FileWriter fstream2 = new
		 FileWriter(outputDirectories+"/avgdist.txt");
		 BufferedWriter outavgdist = new BufferedWriter(fstream2);

		
		 FileWriter fstream3 = new
		 FileWriter(outputDirectories+"/timeinfo.txt");
		 BufferedWriter outtime = new BufferedWriter(fstream3);

		 
	
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
		
		globalMAXworm.setPosition(swarmPr[0].getposition());
		globalMAXworm.setJx(swarmPr[0].getJx());
		globalMAXworm.setID(iteration);
		System.out.println("=========================================");
		System.out.println("ITERATION: " + iteration);
		System.out.println("=========================================");
		MaxJx(swarmPr, globalMAXworm, iteration);
		System.out.println("Iteration TIME: " + diff + " Seconds");
		System.out.println("Whole TIME: " + sumdiff + " Seconds");
		System.out.println("=========================================");
		System.out.println("*IBRAHIM: FileInputFormat.getMaxSplitSize="
				+ FileInputFormat.getMaxSplitSize(job) + " cal:" + splitSize
				+ " swarm lingth:" + lenth);
		System.out.println("*IBRAHIM: ****JOB1 END****");
		
		iteration++;
		while (iteration < settings.getMaxIteration()) {

			startsubtime = System.currentTimeMillis();
			
			FileSystem fs2 = FileSystem.get(conf); // nnn conf1
			Path InFilen2 = new Path(settings.getPathy() + "files/t_" + (iteration)
					+ "/");
			Path outFile2 = new Path(settings.getPathy() + "files/t_"
					+ (iteration + 1) + "/");
			fs2.delete(outFile2, true);
			conf.set("swarm.file", "" + InFilen2.toString());
			
			job = new Job(conf);
			lenth = fs.getContentSummary(InFilen2).getLength();
			splitSize = lenth / settings.getNoOfNOdes() ;
			// System.out.println(FileInputFormat.getMaxSplitSize()+"  "+splitSize);
			FileInputFormat.setMaxInputSplitSize(job, splitSize);

			// System.exit(-1);
			job.setJobName("GSO" + iteration);
			job.setMapperClass(GSOMapper.class);
			job.setReducerClass(GSOReducer.class);

			job.setMapOutputKeyClass(IntWritable.class);
			job.setMapOutputValueClass(Text.class);
			job.setInputFormatClass(KeyTextInputFormat.class);
			FileInputFormat.addInputPath(job, InFilen2);
			FileOutputFormat.setOutputPath(job, outFile2);
			job.setJarByClass(GSOMainJob.class);

			// the job using just the MAPPER function.
			job.setNumReduceTasks(1);
			// job.setNumReduceTasks(0);
			System.out.println("*IBRAHIM: ****JOB1 START****");
			job.waitForCompletion(true);
			System.out.println("*IBRAHIM: ****JOB1 END****");

			endsubtime = System.currentTimeMillis();
			diff = (endsubtime - startsubtime) / 1000;
			sumdiff = sumdiff + diff;

			swarmPr = readSwarm(outFile2.toString(), conf);

			// printSwarm(swarmPr);

			 fill(swarmPr, iteration, result);
			 fillresultPerIteration(swarmPr,resultPerIteration);
			 avdist=calcuateavgdist(resultPerIteration,peaks);
			 outavgdist.append(iteration+"\t"+avdist+"\n");
			 crate=calcuatecrate(resultPerIteration,peaks);
			 outpeakcapture.append(iteration+"\t"+crate+"\n");
			 outtime.append(iteration+"\t"+diff+"\n");
			 outpeakcapture.flush();
			 outavgdist.flush();
			 outtime.flush();
			 
			 
			System.out.println("=========================================");
			System.out.println("ITERATION: " + iteration);
			System.out.println("=========================================");
			MaxJx(swarmPr, globalMAXworm, iteration);
			System.out.println("Iteration TIME: " + diff + " Seconds");
			System.out.println("Whole TIME: " + sumdiff + " Seconds");
			System.out.println("=========================================");
			System.out.println("*IBRAHIM: FileInputFormat.getMaxSplitSize="
					+ FileInputFormat.getMaxSplitSize(job) + " cal:"
					+ splitSize + " swarm lingth:" + lenth);
			
			++iteration;
		}
		if (iteration == (settings.getMaxIteration())) {
//			 writeResults(outputDirectories, result);
			 outtime.append("===============================================================\n");
			 outtime.append("Total Time for "+settings.getMaxIteration()+" Iteartions:"+sumdiff+" (s)\n");
			 outtime.append("Average Time per iteration :"+(sumdiff/settings.getMaxIteration())+" (s)\n");
			 outtime.append("===============================================================\n");
			 outtime.append("#nodes:"+settings.getNoOfNOdes()+"\n");
			
			 
			 outpeakcapture.close();
			 outavgdist.close();
			 outtime.close();
		}

	}

	private static double calcuatecrate(double[][] resultPerIteration,
			double[][] peaks) {
		double crate = 0;
		int nwormsclosed = 3;
		double epsilon = 0.05;  //0.05
		double[] gwormsInsidePeaks = new double[settings.getPeaksNO()];
		for (int i = 0; i < settings.getSwarmSize(); i++) {
			for (int k = 0; k < settings.getPeaksNO(); k++) {
				double dist = 0;
				for (int j = 0; j < settings.getDimension(); j++) {
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
		for (int k = 0; k < settings.getPeaksNO(); k++) {
			if (gwormsInsidePeaks[k] >= nwormsclosed) {
				count = count + 1;
			}
		}

		crate = count / (settings.getPeaksNO() * 1.0);

		return crate;
	}

	private static double calcuateavgdist(double[][] resultPerIteration,
			double[][] peaks) {
		double sum = 0;
		for (int i = 0; i < settings.getSwarmSize(); i++) {
			double minseg = 0;
			for (int j = 0; j < settings.getDimension(); j++) {
				minseg = minseg
						+ Math.pow((resultPerIteration[i][j] - peaks[0][j]), 2);
			}
			minseg = Math.sqrt(minseg);

			for (int k = 1; k < settings.getPeaksNO(); k++) {
				double seg = 0;
				for (int j = 0; j < settings.getDimension(); j++) {
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

		double disAvg = sum / (settings.getSwarmSize() * 1.0);

		return disAvg;
	}
	
	
	private static void initialize(String filePath, String config) throws JsonSyntaxException, JsonIOException, IOException{
		String sLine = "";
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				sLine= sLine+sCurrentLine;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	 settings = createConfigMap(sLine).get(config);
	}
	/**
	 * Initialize the main class with all the required values here.
	 * @throws JsonIOException 
	 * @throws JsonSyntaxException 
	 * @throws IOException 
	 */
	private static HashMap<String, Settings> createConfigMap(String json) throws JsonSyntaxException, JsonIOException, IOException{
		Type type = new TypeToken<HashMap<String, Settings>>(){}.getType();

		Gson gson = new Gson();
		HashMap<String, Settings>  configMap= gson.fromJson(json, type);		
		return (configMap);


	}
}