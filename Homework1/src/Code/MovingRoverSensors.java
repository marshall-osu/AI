package Code;


import java.io.*;
import java.util.*;

/**
 * MovingRoverSensors: A class for reading visual and sample perceptions from a
 * file
 *
 * @author Eric Fosler-Lussier
 * @version 1.1
 *
 *          allowed stdin version of constructor
 */

public class MovingRoverSensors {
	
	private SamplePercept[][] samps;
	private VisionPercept[][] vis;

	/**
	 * Creates Sensors object from file
	 * 
	 * @param filename The file that data is read from
	 */
	public MovingRoverSensors(String filename) {
		BufferedReader myFile = null;

		try {
			myFile = new BufferedReader(new FileReader(filename));
		} catch (Exception e) {
			System.err.println("Ooops!  I can't seem to load the file \"" + filename
					+ "\", do you have the file in the correct place?");
			System.exit(1);
		}
		initialize(myFile);
	}

	/**
	 * Creates Sensors object from standard input
	 */
	public MovingRoverSensors() {
		BufferedReader myFile = null;

		try {
			myFile = new BufferedReader(new InputStreamReader(System.in));
		} catch (Exception e) {
			System.err.println("Ooops!  I can't seem to read the file from the standard input!");
			System.exit(1);
		}
		initialize(myFile);
	}

	private void initialize(BufferedReader myFile) {

		int counter = 0;
		LinkedList<SamplePercept> sp1 = new LinkedList<>();
		LinkedList<SamplePercept> sp2 = new LinkedList<>();
		LinkedList<VisionPercept> vp1 = new LinkedList<>();
		LinkedList<VisionPercept> vp2 = new LinkedList<>();
		String line;

		try {
			while ((line = myFile.readLine()) != null) {
				counter++;
				StringTokenizer st = new StringTokenizer(line, ",");
				int row = Integer.parseInt(st.nextToken());

				if (row != counter) {
					throw new Exception("Malformatted file");
				}
				while (st.hasMoreTokens()) {
					if (counter == 1) {
						// next token: vision
						vp1.add(new VisionPercept(st.nextToken()));
						// next token: sample
						sp1.add(new SamplePercept(st.nextToken()));
					} else if (counter == 2) {
						// next token: vision
						vp2.add(new VisionPercept(st.nextToken()));
						// next token: sample
						sp2.add(new SamplePercept(st.nextToken()));
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Ooops!  I had some problems reading in the file.");
			System.exit(1);
		}

		try {
			// now allocate array space for fast lookup
			// this isn't really necessary, but it makes the lookup code
			// very clean

			Object[] x1 = sp1.toArray();
			Object[] x2 = sp2.toArray();
			Object[] x3 = vp1.toArray();
			Object[] x4 = vp2.toArray();
			samps = new SamplePercept[x1.length][2];
			vis = new VisionPercept[x1.length][2];

			int i;
			for (i = 0; i < x1.length; i++) {
				samps[i][0] = (SamplePercept) x1[i];
				samps[i][1] = (SamplePercept) x2[i];
				vis[i][0] = (VisionPercept) x3[i];
				vis[i][1] = (VisionPercept) x4[i];
			}

		} catch (Exception e) {
			System.err.println("Ooops!  I had some problems reading in the file.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Gets a sample perception at <x,y>
	 * 
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return SamplePercept A SamplePercept object containing the percept
	 */
	public SamplePercept getSamplePercept(int x, int y) {
		try {
			return samps[x - 1][y - 1];

		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Gets a vision perception at <x,y>
	 * 
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return VisionPercept A VisionPercept object containing the percept
	 */
	public VisionPercept getVisionPercept(int x, int y) {
		try {
			return vis[x - 1][y - 1];

		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Run a test of the reading routines, prints out all percepts of the file
	 *
	 * Usage: java MovingRoverSensors -file <filename>
	 */
	public static void main(String args[]) {
		if (args.length != 0 && (args.length != 2 || (!args[0].equals("-file")))) {
			System.err.println("Usage: MovingRoverSensors -file <filename>");
			System.exit(1);
		}

		MovingRoverSensors mrs = null;
		int i, j;

		if (args.length == 0) {
			mrs = new MovingRoverSensors();
		} else {
			mrs = new MovingRoverSensors(args[1]);
		}
		for (i = 1; i < 6; i++) {
			for (j = 1; j < 3; j++) {
				System.out.println("Position: <" + i + "," + j + ">: Sample=" + mrs.getSamplePercept(i, j).value()
						+ " Clear:" + mrs.getVisionPercept(i, j).isClear());
			}
		}
	}

}
