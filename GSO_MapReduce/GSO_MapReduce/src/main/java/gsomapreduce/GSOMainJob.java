package gsomapreduce;

import java.io.*;

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
import org.apache.hadoop.mapreduce.Job;
///ort org.apache.hadoop.mapreduce.lib.input.FileInputFormat; split remove it use b\new one
//import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import benchmark.benchmark;

import java.text.DecimalFormat;

public class GSOMainJob {
	private static final Log LOG = LogFactory.getLog(GSOMainJob.class);
	private static DecimalFormat twoDForm = new DecimalFormat("#.##");
	static worm globalMAXworm = new worm();
	static worm globalMINworm = new worm();

	public static worm[] initial(worm swarm[]) {
		Random aRandom = new Random();
		benchmark bench;
		bench = new benchmark(Settings.benchName);
		double aStart = bench.getMin();
		double aEnd = bench.getMax();
		double range = (double) aEnd - (double) aStart;

		for (int i = 0; i < Settings.SWARM_SIZE; i++) {
			swarm[i] = new worm();
			// random position
			double position[] = new double[Settings.DIMENSION];
			for (int a = 0; a < Settings.DIMENSION; a++) {
				double fraction = (double) (range * aRandom.nextDouble());
				double randomNumber = (double) (fraction + aStart);
				position[a] = randomNumber;
				// System.out.print(" position----->  "+ position[a]+"\t");
			}
			// System.out.println();
			swarm[i].setID(i);
			swarm[i].setPosition(position);
			swarm[i].setluc(Settings.l0);
			swarm[i].setRd(Settings.r0);

		}
		return swarm;
	}

	public static List<worm> findNeighbors(worm swarm[], worm w) {
		List<worm> wormNeighbors = new ArrayList<worm>();

		double PositionJ[] = w.getposition();
		// System.out.println(w.getID());
		for (int x = 0; x < Settings.SWARM_SIZE; x++) {
			double distance, sum = 0;
			if (swarm[x].getID() != w.getID()) {
				// System.out.println(swarm[x].getID()+" "+w.getID());
				double PositionI[] = swarm[x].getposition();
				for (int a = 0; a < Settings.DIMENSION; a++) {
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
			double position[] = new double[Settings.DIMENSION];
			position = wormNeighbors.get(i).getposition();
//SL			System.out.println("n" + wormNeighbors.get(i).getID() + "\t");
			for (int a = 0; a < Settings.DIMENSION; a++) {
//SL				System.out.print("P" + a + ": " + position[a] + "\t");
			}
//SL			System.out.print("Luc:" + wormNeighbors.get(i).getluc() + "\t");
//SL			System.out.println("Rd:" + wormNeighbors.get(i).getRd() + "\t");
			// System.out.println("");
		}
	}

	private static void printSwarm(worm swarm[]) {
		for (int i = 0; i < Settings.SWARM_SIZE; i++) {
			double position[] = new double[Settings.DIMENSION];
			position = swarm[i].getposition();
//SL			System.out.print("W" + i + "\t");
			for (int a = 0; a < Settings.DIMENSION; a++) {
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
		for (int i = 0; i < Settings.SWARM_SIZE; i++) {
			outy.writeBytes(i + "\t");
			String st = "";
			for (int w = 0; w < Settings.DIMENSION - 1; w++) {
				st = st + (swarm[i].getposition()[w] + ",");
			}
			st = st + (swarm[i].getposition()[Settings.DIMENSION - 1] + ";");
			st = st + (swarm[i].getJx() + ";");
			st = st + (swarm[i].getluc() + ";");
			st = st + (swarm[i].getRd() + ";");

			st = st + (swarm[i].getwormNeighbors().size() + ";");
			for (int w = 0; w < swarm[i].getwormNeighbors().size(); w++) {
				st = st + (swarm[i].getwormNeighbors().get(w).getID() + ";");
				for (int j = 0; j < Settings.DIMENSION - 1; j++) {
					st = st
							+ (swarm[i].getwormNeighbors().get(w).getposition()[j] + ",");
				}
				st = st
						+ (swarm[i].getwormNeighbors().get(w).getposition()[Settings.DIMENSION - 1] + ";");
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
		for (int i = 0; i < Settings.SWARM_SIZE; i++) {
			outy.writeBytes(i + "\t");
			String st = "";
			for (int w = 0; w < Settings.DIMENSION - 1; w++) {
				st = st + (swarm[i].getposition()[w] + ",");
			}
			st = st + (swarm[i].getposition()[Settings.DIMENSION - 1] + ";");
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
		worm swarm[] = new worm[Settings.SWARM_SIZE];

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
						String[] line = keyAndValue[1].split(";");
						String[] linepos = line[0].toString().split(",");
						double[] pos = new double[Settings.DIMENSION];
						for (int k = 0; k < Settings.DIMENSION; k++) {
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
		for (int i = 0; i < Settings.DIMENSION; i++) {
			for (int j = 0; j < Settings.SWARM_SIZE; j++) {
				result[i].setval(j, it, swarm[j].getposition()[i]);
			}
		}
	}

	private static void fillresultPerIteration(worm swarm[],
			double[][] resultPerIteration) {
		for (int i = 0; i < Settings.SWARM_SIZE; i++) {
			for (int j = 0; j < Settings.DIMENSION; j++) {
				resultPerIteration[i][j] = swarm[i].getposition()[j];

			}
		}
	}

	private static void writeResults(String outputDirectories, Results[] result)
			throws IOException {

		for (int k = 0; k < Settings.DIMENSION; k++) {

			FileWriter fstreamX = new FileWriter(outputDirectories + "/d" + k);
			BufferedWriter outX = new BufferedWriter(fstreamX);
			for (int i = 0; i < Settings.SWARM_SIZE; i++) {
				String rowX = "";
				for (int a = 0; a < Settings.MAX_ITERATION - 1; a++) {
					rowX = rowX + result[k].getval(i, a) + ",";
				}
				rowX = rowX + result[k].getval(i, Settings.MAX_ITERATION - 1);
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
		for (int z = 0; z < Settings.DIMENSION; z++) {
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
		for (int z = 0; z < Settings.DIMENSION; z++) {
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
			   
				 
				for (int i = 0; i < Settings.DIMENSION; i++) {
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

		String configFile = "mrresources/config.txt";//args[0];// "config.txt";
	    String peaksFile="mrresources/peaksfileCF4D2.txt";//"peaksFile.txt";
		Settings settings = new Settings(configFile);
		settings.getClass();

		String outputDirectories = "mrout" + "/" + settings.benchName +"dim"+settings.DIMENSION +"rs"
				+ settings.rs + "it" + settings.MAX_ITERATION + "ssize"
				+ settings.SWARM_SIZE +"noNodes"+settings.noOfNOdes +"_" + (int)(Math.random()*100);

		boolean success = (new File(outputDirectories)).mkdirs();
		if (success) {
			System.out
					.println("Directories: " + outputDirectories + " created");
		}

		worm swarm1[] = new worm[settings.SWARM_SIZE];
		worm swarm[] = new worm[settings.SWARM_SIZE];
		double start = System.currentTimeMillis();
		double startsubtime = System.currentTimeMillis();
		double sumdiff = 0;

		swarm = initial(swarm1);
		printSwarm(swarm);
		 double [][] peaks=new double[settings.peaksNO][settings.DIMENSION];
		 readsPeaks(peaksFile,peaks);
		benchmark bench;
		for (int i = 0; i < Settings.SWARM_SIZE; i++) {
			bench = new benchmark(Settings.benchName, swarm[i].getposition());
			double Jx = bench.getr();
			swarm[i].setJx(Jx);
			double l = (1 - Settings.p_const) * swarm[i].getluc()
					+ Settings.Gama * swarm[i].getJx();
			swarm[i].setluc(l);
		}

		int iteration = 0;
		Configuration conf = new Configuration();

		// conf.set("fs.default.name", "file:///"); //local file system
//		 conf.set("mapred.job.tracker","local"); //local tracker
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

		conf.set("worm.dim", "" + settings.DIMENSION); // dimensions
		conf.set("swarm.size", "" + settings.SWARM_SIZE); // swarm size
		conf.set("p", "" + settings.p_const);
		conf.set("gama", "" + settings.Gama);
		conf.set("B", "" + settings.B);
		conf.set("nt", "" + settings.nt);
		conf.set("step", "" + settings.step);
		conf.set("l0", "" + settings.l0);
		conf.set("r0", "" + settings.r0);
		conf.set("rs", "" + settings.rs);
		conf.set("benchName", "" + settings.benchName);

		String swarmInFolder = settings.pathy + "files/t_" + (iteration);
		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path(swarmInFolder), true);

		fs.mkdirs(new Path(swarmInFolder));
		String swarmInFile = swarmInFolder + "/swarm0";
		conf.set("swarm.file", "" + swarmInFile);

		System.out.println("done1");
		writeSwarmwithoutnb(swarm, swarmInFile, conf);
		System.out.println("done2");

		Path InFile = new Path(settings.pathy + "files/t_" + (iteration) + "/");
		Path outFile = new Path(settings.pathy + "files/t_" + (iteration + 1)
				+ "/");
		fs.delete(outFile, true);

		Job job = new Job(conf);
		job.setJobName("GSO" + iteration);
		long lenth = fs.getContentSummary(InFile).getLength();
		long splitSize = lenth / (settings.noOfNOdes * settings.maxMappers);
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
		job.setNumReduceTasks(settings.maxReducers);
		// job.setNumReduceTasks(0);
		System.out.println("*IBRAHIM: ****JOB1 START****");

		job.waitForCompletion(true);
//		double endsubtime = System.currentTimeMillis();
//		double diff = (endsubtime - startsubtime) / 1000;
//		sumdiff = sumdiff + diff;
//
//		Results[] result = new Results[settings.DIMENSION];
//	    double [][] resultPerIteration=new
//		double[settings.SWARM_SIZE][settings.DIMENSION];
//
//		for (int i = 0; i < settings.DIMENSION; i++) {
//			result[i] = new Results(settings.SWARM_SIZE, settings.MAX_ITERATION);
//		}
//
//		worm swarmPr[] = new worm[settings.SWARM_SIZE];
//		swarmPr = readSwarm(outFile.toString(), conf);
//		// printSwarm(swarmPr);
//		
//		fill(swarmPr, iteration, result);
//		
//		fillresultPerIteration(swarmPr,resultPerIteration);
//		
//		 FileWriter fstream1 = new
//		 FileWriter(outputDirectories+"/peakcapture.txt");
//		 BufferedWriter outpeakcapture = new BufferedWriter(fstream1);
//
//		 FileWriter fstream2 = new
//		 FileWriter(outputDirectories+"/avgdist.txt");
//		 BufferedWriter outavgdist = new BufferedWriter(fstream2);
//
//		
//		 FileWriter fstream3 = new
//		 FileWriter(outputDirectories+"/timeinfo.txt");
//		 BufferedWriter outtime = new BufferedWriter(fstream3);
//
//		 
//	
//		 double avdist=calcuateavgdist(resultPerIteration,peaks);
//		outavgdist.write(iteration+"\t"+avdist+"\n");
//		 System.out.println("avdist at "+iteration+" iteration="+avdist+"\n");
//		 double crate=calcuatecrate(resultPerIteration,peaks);
//		 System.out.println("crate at "+iteration+" iteration="+crate+"\n");
//		 outpeakcapture.write(iteration+"\t"+crate+"\n");
//		 outtime.write(iteration+"\t"+diff+"\n"); 
//		 outpeakcapture.flush();
//		 outavgdist.flush();
//		 outtime.flush();
//		
//		globalMAXworm.setPosition(swarmPr[0].getposition());
//		globalMAXworm.setJx(swarmPr[0].getJx());
//		globalMAXworm.setID(iteration);
//		System.out.println("=========================================");
//		System.out.println("ITERATION: " + iteration);
//		System.out.println("=========================================");
//		MaxJx(swarmPr, globalMAXworm, iteration);
//		System.out.println("Iteration TIME: " + diff + " Seconds");
//		System.out.println("Whole TIME: " + sumdiff + " Seconds");
//		System.out.println("=========================================");
//		System.out.println("*IBRAHIM: FileInputFormat.getMaxSplitSize="
//				+ FileInputFormat.getMaxSplitSize(job) + " cal:" + splitSize
//				+ " swarm lingth:" + lenth);
//		System.out.println("*IBRAHIM: ****JOB1 END****");
//		
//		iteration++;
//		while (iteration < settings.MAX_ITERATION) {
//
//			startsubtime = System.currentTimeMillis();
//			
//			FileSystem fs2 = FileSystem.get(conf); // nnn conf1
//			Path InFilen2 = new Path(settings.pathy + "files/t_" + (iteration)
//					+ "/");
//			Path outFile2 = new Path(settings.pathy + "files/t_"
//					+ (iteration + 1) + "/");
//			fs2.delete(outFile2, true);
//			conf.set("swarm.file", "" + InFilen2.toString());
//			
//			job = new Job(conf);
//			lenth = fs.getContentSummary(InFilen2).getLength();
//			splitSize = lenth / (settings.noOfNOdes * settings.maxMappers);
//			// System.out.println(FileInputFormat.getMaxSplitSize()+"  "+splitSize);
//			FileInputFormat.setMaxInputSplitSize(job, splitSize);
//
//			// System.exit(-1);
//			job.setJobName("GSO" + iteration);
//			job.setMapperClass(GSOMapper.class);
//			job.setReducerClass(GSOReducer.class);
//
//			job.setMapOutputKeyClass(IntWritable.class);
//			job.setMapOutputValueClass(Text.class);
//			job.setInputFormatClass(KeyTextInputFormat.class);
//			FileInputFormat.addInputPath(job, InFilen2);
//			FileOutputFormat.setOutputPath(job, outFile2);
//			job.setJarByClass(GSOMainJob.class);
//
//			// the job using just the MAPPER function.
//			job.setNumReduceTasks(settings.maxReducers);
//			// job.setNumReduceTasks(0);
//			System.out.println("*IBRAHIM: ****JOB1 START****");
//			job.waitForCompletion(true);
//			System.out.println("*IBRAHIM: ****JOB1 END****");
//
//			endsubtime = System.currentTimeMillis();
//			diff = (endsubtime - startsubtime) / 1000;
//			sumdiff = sumdiff + diff;
//
//			swarmPr = readSwarm(outFile2.toString(), conf);
//
//			// printSwarm(swarmPr);
//
//			 fill(swarmPr, iteration, result);
//			 fillresultPerIteration(swarmPr,resultPerIteration);
//			 avdist=calcuateavgdist(resultPerIteration,peaks);
//			 outavgdist.append(iteration+"\t"+avdist+"\n");
//			 crate=calcuatecrate(resultPerIteration,peaks);
//			 outpeakcapture.append(iteration+"\t"+crate+"\n");
//			 outtime.append(iteration+"\t"+diff+"\n");
//			 outpeakcapture.flush();
//			 outavgdist.flush();
//			 outtime.flush();
//			 
//			 
//			System.out.println("=========================================");
//			System.out.println("ITERATION: " + iteration);
//			System.out.println("=========================================");
//			MaxJx(swarmPr, globalMAXworm, iteration);
//			System.out.println("Iteration TIME: " + diff + " Seconds");
//			System.out.println("Whole TIME: " + sumdiff + " Seconds");
//			System.out.println("=========================================");
//			System.out.println("*IBRAHIM: FileInputFormat.getMaxSplitSize="
//					+ FileInputFormat.getMaxSplitSize(job) + " cal:"
//					+ splitSize + " swarm lingth:" + lenth);
//			
//			++iteration;
//		}
//		if (iteration == (settings.MAX_ITERATION)) {
////			 writeResults(outputDirectories, result);
//			 outtime.append("===============================================================\n");
//			 outtime.append("Total Time for "+settings.MAX_ITERATION+" Iteartions:"+sumdiff+" (s)\n");
//			 outtime.append("Average Time per iteration :"+(sumdiff/settings.MAX_ITERATION)+" (s)\n");
//			 outtime.append("===============================================================\n");
//			 outtime.append("#nodes:"+settings.noOfNOdes+"\n");
//			
//			 
//			 outpeakcapture.close();
//			 outavgdist.close();
//			 outtime.close();
//		}

	}

	private static double calcuatecrate(double[][] resultPerIteration,
			double[][] peaks) {
		double crate = 0;
		int nwormsclosed = 3;
		double epsilon = 0.05;  //0.05
		double[] gwormsInsidePeaks = new double[Settings.peaksNO];
		for (int i = 0; i < Settings.SWARM_SIZE; i++) {
			for (int k = 0; k < Settings.peaksNO; k++) {
				double dist = 0;
				for (int j = 0; j < Settings.DIMENSION; j++) {
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
		for (int k = 0; k < Settings.peaksNO; k++) {
			if (gwormsInsidePeaks[k] >= nwormsclosed) {
				count = count + 1;
			}
		}

		crate = count / (Settings.peaksNO * 1.0);

		return crate;
	}

	private static double calcuateavgdist(double[][] resultPerIteration,
			double[][] peaks) {
		double sum = 0;
		for (int i = 0; i < Settings.SWARM_SIZE; i++) {
			double minseg = 0;
			for (int j = 0; j < Settings.DIMENSION; j++) {
				minseg = minseg
						+ Math.pow((resultPerIteration[i][j] - peaks[0][j]), 2);
			}
			minseg = Math.sqrt(minseg);

			for (int k = 1; k < Settings.peaksNO; k++) {
				double seg = 0;
				for (int j = 0; j < Settings.DIMENSION; j++) {
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

		double disAvg = sum / (Settings.SWARM_SIZE * 1.0);

		return disAvg;
	}
}