//package gsomapreduce;
/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import benchmark.benchmark;
import scala.Function1;

public class GSOMapper extends Mapper<Text, Text, IntWritable, Text> implements Function1<String, String> {
	public int reduceTaskId = 0;
	int swarmSize;
	int wormDim;
	double p_const;
	double gama;
	double B;
	double nt;
	double step;
	double l0;
	double r0;
	double rs;
	String benchName;
	public static worm swarmPr[];

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
		// System.out.println("ssssssssssssssss");
		Configuration conf = context.getConfiguration();
		wormDim = Integer.parseInt(conf.get("worm.dim")); // dimensions
		swarmSize = Integer.parseInt(conf.get("swarm.size")); // swarm size
		p_const = Double.parseDouble(conf.get("p"));
		gama = Double.parseDouble(conf.get("gama"));
		B = Double.parseDouble(conf.get("B"));
		nt = Double.parseDouble(conf.get("nt"));
		step = Double.parseDouble(conf.get("step"));
		l0 = Double.parseDouble(conf.get("l0"));
		r0 = Double.parseDouble(conf.get("r0"));
		rs = Double.parseDouble(conf.get("rs"));
		benchName = conf.get("benchName");
		Path swarmFile = new Path(conf.get("swarm.file"));
		swarmPr = new worm[swarmSize];
		System.out.println("swarmFile.toString(): " + swarmFile.toString());
		swarmPr = readSwarm(swarmFile.toString(), swarmSize, wormDim, conf);
		// printSwarm(swarmPr);
		// System.exit(-1);

	}

	@Override
	protected void map(Text key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] line = value.toString().split(";");
		worm worm1 = new worm();
		worm1.setID(Integer.parseInt("" + key));
		String[] linepos = line[0].split(",");
		double[] pos = new double[wormDim];
		for (int k = 0; k < wormDim; k++) {
			pos[k] = Double.parseDouble(linepos[k]);
		}
		worm1.setPosition(pos);
		worm1.setJx(Double.parseDouble(line[1]));
		worm1.setluc(Double.parseDouble(line[2]));
		worm1.setRd(Double.parseDouble(line[3]));
		// worm1.printWorm();

		String st = "";
		for (int w = 0; w < wormDim - 1; w++) {
			st = st + (worm1.getposition()[w] + ",");
		}
		st = st + (worm1.getposition()[wormDim - 1] + ";");
		st = st + (worm1.getJx() + ";");
		st = st + (worm1.getluc() + ";");
		st = st + (worm1.getRd() + ";");

		Text newValue = new Text();
		IntWritable newkey = new IntWritable(Integer.parseInt("" + key));
		newValue.set(st);

		List<worm> wormNeighbors = new ArrayList<worm>();

		double PositionJ[] = worm1.getposition();

		for (int x = 0; x < swarmSize; x++) {
			if (swarmPr[x].getID() != worm1.getID()) {
				double distance, sum = 0;
				double PositionI[] = swarmPr[x].getposition();
				for (int a = 0; a < wormDim; a++) {
					sum = sum + Math.pow(PositionI[a] - PositionJ[a], 2);
				}
				distance = Math.sqrt(sum); // Euclidean Distance

				if (distance < worm1.getRd()
						&& worm1.getluc() < swarmPr[x].getluc()) {
					wormNeighbors.add(swarmPr[x]);

				}

			}
		}

		if (wormNeighbors.size() != 0) {
			double[] p = new double[wormNeighbors.size()];
			double acsum = 0;

			for (int c = 0; c < wormNeighbors.size(); c++) {
				acsum = acsum
						+ Math.abs(wormNeighbors.get(c).getluc()
								- worm1.getluc());
			}

			for (int c = 0; c < wormNeighbors.size(); c++) {
				p[c] = Math.abs(wormNeighbors.get(c).getluc() - worm1.getluc())
						/ acsum;
			}

			// ////////////////////////////
			int index = 0;
			Random aRandom = new Random();
			double randn = aRandom.nextDouble();
			double high = p[0];
			double summ = 0.0;
			int ii = 0;
			while (summ <= randn) {
				summ = summ + p[ii];
				++ii;
			}
			index = ii - 1;
			worm J = wormNeighbors.get(index);
			String st1 = wormNeighbors.size() + ";";
			for (int w = 0; w < wormDim - 1; w++) {
				st1 = st1 + J.getposition()[w] + ",";
			}
			st1 = st1 + J.getposition()[wormDim - 1] + ";";
			st1 = st1 + J.getJx() + ";";
			st1 = st1 + J.getluc() + ";";
			st1 = st1 + J.getRd() + ";";
			context.write(newkey, new Text(st1));
		}

		else {
			// System.out.print("oK" + wormNeighbors.size() + "");
			String st1 = wormNeighbors.size() + ";";
			for (int w = 0; w < wormDim - 1; w++) {
				st1 = st1 + "-1" + ",";
			}
			st1 = st1 + "-1" + ";";
			st1 = st1 + "-1;";
			st1 = st1 + "-1;";
			st1 = st1 + ("-1;");
			context.write(newkey, new Text(st1));
		}

		context.write(newkey, newValue);
	}

	private static worm[] readSwarm(String fileNameIn, int ssize, int dim,
			Configuration conf) throws IOException {
		worm swarm[] = new worm[ssize];

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
						double[] pos = new double[dim];
						for (int k = 0; k < dim; k++) {
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

	private static void printSwarm(worm swarm[]) {
		for (int i = 0; i < swarm.length; i++) {
			double position[] = new double[swarm[0].getposition().length];
			position = swarm[i].getposition();
			System.out.print("W" + swarm[i].getID() + "\t");
			for (int a = 0; a < swarm[0].getposition().length; a++) {
				System.out.print("P" + a + ": " + position[a] + "\t");
			}
			System.out.print("Jx:" + swarm[i].getJx() + "\t");
			System.out.print("Luc:" + swarm[i].getluc() + "\t");
			System.out.println("Rd:" + swarm[i].getRd() + "\t");
			// System.out.println("");
		}
	}

	@Override
	public <A> Function1<String, A> andThen(Function1<String, A> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public R apply(T1 arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double apply$mcDD$sp(double arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double apply$mcDF$sp(float arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double apply$mcDI$sp(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double apply$mcDJ$sp(long arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float apply$mcFD$sp(double arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float apply$mcFF$sp(float arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float apply$mcFI$sp(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float apply$mcFJ$sp(long arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int apply$mcID$sp(double arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int apply$mcIF$sp(float arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int apply$mcII$sp(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int apply$mcIJ$sp(long arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long apply$mcJD$sp(double arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long apply$mcJF$sp(float arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long apply$mcJI$sp(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long apply$mcJJ$sp(long arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void apply$mcVD$sp(double arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void apply$mcVF$sp(float arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void apply$mcVI$sp(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void apply$mcVJ$sp(long arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean apply$mcZD$sp(double arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean apply$mcZF$sp(float arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean apply$mcZI$sp(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean apply$mcZJ$sp(long arg0) {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public <A> Function1<A, R> compose(Function1<A, T1> arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
*/