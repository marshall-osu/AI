package Code;
import java.io.*;

/**
 * RovingSampleSensors: A class for reading sample perceptions from a file and
 * presenting them one at a time
 *
 * @author Eric Fosler-Lussier
 * @version 1.1
 *
 *          allowed stdin version of contstructor
 */

public class RoverSampleSensor {
	// File
	private BufferedReader myFile;

	/**
	 * Creates Sensors object from file
	 * 
	 * @param filename The file that data is read from
	 */
	public RoverSampleSensor(String filename) {
		try {
			myFile = new BufferedReader(new FileReader(filename));
		} catch (Exception e) {
			System.err.println("Ooops!  I can't seem to load the file \"" + filename
					+ "\", do you have the file in the correct place?");
			System.exit(1);
		}
	}

	/**
	 * Creates Sensors object from standard input
	 */
	public RoverSampleSensor() {
		try {
			myFile = new BufferedReader(new InputStreamReader(System.in));
		} catch (Exception e) {
			System.err.println("Ooops!  I can't seem to read from the standard input!");
			System.exit(1);
		}
	}

	/**
	 * Gets the next sample perception
	 * 
	 * @return SamplePercept A SamplePercept object containing the percept
	 */

	public SamplePercept getPercept() {
		String line;

		try {
			line = myFile.readLine();
			if (myFile == null) {
				return null;
			} else if (line == null) {
				try {
					myFile.close();
				} catch (Exception e) {
				}

				myFile = null;
				return null;
			} else {
				return new SamplePercept(line);
			}
		} catch (Exception e) {
			System.err.println("Ooops!  I seem to have gotten an i/o error reading the file.");
			System.exit(1);
		}
		return null;
	}

	/**
	 * Run a test of the reading routines, prints out all percepts of the file
	 *
	 * Usage: java RoverSampleSensor -file <filename>
	 */

	public static void main(String args[]) {
		if (args.length != 0 && (args.length != 2 || (!args[0].equals("-file")))) {
			System.err.println("Usage: RoverSampleSensor -file <filename>");
			System.exit(1);
		}
	}

}
