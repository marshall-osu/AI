package Code;

/**
 * ReflexRover: A class for running a ReflexRover based on input data file from
 * the user
 *
 * @author Tanner Marshall
 * @version 1.0
 * @date 9/30/2020
 */

public class ReflexRover {

	/**
	 * Interprets input data to return the proper action for the given input
	 * 
	 * @param percept the input data for the agent
	 */
	private char SimpleReflexAgent(int percept) {
		// return G (GRAB) if the precept is divisible by 5 otherwise return N (NOOP)
		return percept % 5 == 0 ? 'G' : 'N';
	}

	/**
	 * Runs the Reflex Rover until there is no more input data
	 * 
	 * @param rss the sensor to provide input to the agent function
	 */
	public void run(RoverSampleSensor rss) {
		SamplePercept sp;

		// loop until there is no more input data
		while ((sp = rss.getPercept()) != null) {
			// print the precept from sensor and action from action function given the
			// precept
			System.out.println("Perceived: " + sp.value() + " Action: " + this.SimpleReflexAgent(sp.value()));
		}
	}

	public static void main(String[] args) {
		// initialize sensors and rover then run
		RoverSampleSensor rss = new RoverSampleSensor(args[0]);
		ReflexRover rover = new ReflexRover();
		rover.run(rss);
	}
}
