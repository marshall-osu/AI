package Code;

import java.util.HashSet;
import java.util.Set;

/**
 * StateRover: A class for running a StateRover based on input data file from
 * the user
 *
 * @author Tanner Marshall
 * @version 1.0
 * @date 9/30/2020
 */

//Possible directions of the rover
enum Direction {
	NORTH, SOUTH, EAST;
}

//Possible actions of the rover
enum Action {
	GRAB, LOOKNORTH, LOOKEAST, LOOKSOUTH, GONORTH, GOEAST, GOSOUTH;
}

public class StateRover {
	// current position of the rover
	private int[] position;
	// the current column and the column to the right of the rover
	// (0=unseen,1=boulder/null,2=clear,3=visited)
	private int[][] radar;
	// current direction of rover
	private Direction dir;
	// samples already collected
	private Set<Integer> collection;

	public StateRover() {

		// initialize the rover position
		position = new int[2];
		position[0] = 1;
		position[1] = 1;

		// initialize the rover's "radar"
		radar = new int[2][2];
		for (int i = 0; i < radar[0].length; i++) {
			for (int j = 0; j < radar[0].length; j++) {
				radar[i][j] = 0;
			}
		}

		// set initial direction
		dir = Direction.EAST;

		// create set to collect sample values
		collection = new HashSet<>();
	}

	/**
	 * Updates the rover's "radar" to reflect its current position and vision
	 * 
	 * @param vp the current vision precept of the rover
	 */
	private void updateState(VisionPercept vp) {

		// update the radar to reflect the rover's current position
		radar[0][position[1] % 2] = 3;

		if (vp != null) {

			// find value to put in radar
			int vision = vp.isClear() ? 2 : 1;

			switch (dir) {

			// position[1]%2 takes rover 'y' and puts it in terms of a 2d array 'y' (adding 1 flips it)
			case NORTH:
				// if facing north and northern position doesn't have an identifier give it one
				if (radar[0][position[1] % 2 - 1] == 0) {
					radar[0][position[1] % 2 - 1] = vision;
				}
				break;
			case EAST:
				// if facing east and eastern position doesn't have an identifier give it one
				if (radar[1][position[1] % 2] == 0) {
					radar[1][position[1] % 2] = vision;
				}
				break;
			case SOUTH:
				// if facing south and southern position doesn't have an identifier give it one
				if (radar[0][position[1] % 2 + 1] == 0) {
					radar[0][position[1] % 2 + 1] = vision;
				}
				break;
			default:
			}
		} else {
			// extra check to make sure the rover recognizes the end of data
			if (dir == Direction.EAST) {
				radar[1][position[1] % 2] = 1;
			}
		}
	}

	/**
	 * Updates the rover's position or direction to reflect its action
	 * 
	 * @param action the action which has been performed
	 */
	private void updateStateFromAction(Action action) {
		switch (action) {
		case LOOKNORTH:
			dir = Direction.NORTH;
			break;
		case LOOKEAST:
			dir = Direction.EAST;
			break;
		case LOOKSOUTH:
			dir = Direction.SOUTH;
			break;
		case GONORTH:
			position[1]++;
			break;
		case GOEAST:
			shift();
			position[0]++;
			break;
		case GOSOUTH:
			position[1]--;
			break;
		default:
		}
	}

	/**
	 * Shifts the rover's "radar" to reflect the rover moving east
	 */
	private void shift() {
		//copy right column into current column
		radar[0][0] = radar[1][0];
		radar[0][1] = radar[1][1];
		//clear right column
		radar[1][0] = 0;
		radar[1][1] = 0;
	}

	/**
	 * Interprets precepts to give a proper action given the rovers current state
	 * 
	 * @param sp the sample precept of the rover
	 * @param vp the vision precept of the rover
	 */
	public Action ReflexAgentWithState(SamplePercept sp, VisionPercept vp) {

		//update the rover's state
		updateState(vp);

		//if rover doesn't have current locations sample, grab it
		if (!collection.contains(sp.value())) {
			//add grabbed sample to collection
			collection.add(sp.value());
			return Action.GRAB;
		}
		
		//if the rover hasen't seen a position in the current column or hasen't visited it
		if (radar[0][(position[1] + 1) % 2] != 3 && radar[0][(position[1] + 1) % 2] != 1) {
			//if position is clear move to it
			if (radar[0][(position[1] + 1) % 2] == 2) {
				return position[1] == 2 ? Action.GOSOUTH : Action.GONORTH;
			//if position hasn't been seen look at it
			} else {
				return position[1] == 2 ? Action.LOOKSOUTH : Action.LOOKNORTH;
			}
		}//if all of column has been visited and directly right of the rover is a boulder
		if (radar[1][position[1] % 2] == 1) {
			//if boulder in current column or two boulders in right column end rover
			if (radar[0][(position[1] + 1) % 2] == 1 || radar[1][(position[1] + 1) % 2] == 1) {
				return null;
			//if not move to other column position
			} else {
				return position[1] == 2 ? Action.GOSOUTH : Action.GONORTH;
			}
		}
		//if position to the right of rover is unseen, look at it
		if (radar[1][position[1] % 2] == 0) {
			return Action.LOOKEAST;
		//if position to the right is seen then we know it's not a boulder so it must be clear, go to it
		} else {
			return Action.GOEAST;
		}
	}
	
	/**
	 * Gets the current vision precept based of the rover's current state
	 * 
	 * @param mrs the sensors for the state rover
	 */
	private VisionPercept getVisionPercept(MovingRoverSensors mrs) {
		VisionPercept vp = null;
		switch (dir) {
		case NORTH:
			//get the vision precept north of the rover
			vp = mrs.getVisionPercept(position[0], position[1] + 1);
			break;
		case EAST:
			//get the vision precept east of the rover
			vp = mrs.getVisionPercept(position[0] + 1, position[1]);
			break;
		case SOUTH:
			//get the vision precept south of the rover
			vp = mrs.getVisionPercept(position[0], position[1] + 1);
			break;
		default:
		}
		
		return vp;
	}

	/**
	 * Runs the state rover till it has seen all potential samples
	 * 
	 * @param mrs the sensors for the state rover
	 */
	public void run(MovingRoverSensors mrs) {
		
		//get sample precept from current position
		SamplePercept sp = mrs.getSamplePercept(position[0], position[1]);
		//get vision precept form current position and direction
		VisionPercept vp = this.getVisionPercept(mrs);

		//initialize output variables
		Action action;
		String vision;
		int moves = 0;

		//run until the agent returns null to stop the rover
		while ((action = this.ReflexAgentWithState(sp, vp)) != null) {

			//interpret the vision precept for output
			if (vp == null) {
				vision = "NULL";
			} else {
				vision = vp.isClear() ? "CLEAR" : "BOULDER";
			}

			//out print position, direction, precepts, and location
			System.out.println("Position: <" + position[0] + ">,<" + position[1] + "> Looking: " + dir + " Perceived: <"
					+ sp.value() + "," + vision + "> Action: " + action.toString());

			//update state to reflect action
			this.updateStateFromAction(action);
			
			//get sample precept from current position
			sp = mrs.getSamplePercept(position[0], position[1]);
			//get vision precept form current position and direction
			vp = this.getVisionPercept(mrs);
			
			//increment the moves
			moves++;
		}
		
		//interpret the final vision precept for output
		if (vp == null) {
			vision = "NULL";
		} else {
			vision = vp.isClear() ? "CLEAR" : "BOULDER";
		}

		System.out.println("Position: <" + position[0] + ">,<" + position[1] + "> Looking: " + dir + " Perceived: <"
				+ sp.value() + "," + vision + "> Action: STOP");
		
		System.out.println("Total Components Collected: " + collection.size() + " Total Moves: " + moves);
	}

	public static void main(String[] args) {
		StateRover rover = new StateRover();
		MovingRoverSensors mrs = new MovingRoverSensors(args[0]);
		rover.run(mrs);
	}
}
