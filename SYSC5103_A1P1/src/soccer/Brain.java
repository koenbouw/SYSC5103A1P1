package soccer;
//	File:			Brain.java

//	Author:		Krzysztof Langner
//	Date:			1997/04/28
//
//    Modified by:	Paul Marlow

import java.util.regex.Pattern;

class Brain extends Thread implements SensorInput {

	// ===========================================================================
	// Private members
	private SendCommand m_krislet; // robot which is controled by this brain
	private Memory m_memory; // place where all information is stored
	private char m_side;
	volatile private boolean m_timeOver;
	private String m_playMode;
	private AgentFunction agentFunction;

	// ---------------------------------------------------------------------------
	// This constructor:
	// - stores connection to krislet
	// - starts thread for this object
	public Brain(SendCommand krislet, String team, char side, int number, String playMode) {
		agentFunction = new AgentFunction("agentConfiguration.txt");
		m_timeOver = false;
		m_krislet = krislet;
		m_memory = new Memory();
		// m_team = team;
		m_side = side;
		// m_number = number;
		m_playMode = playMode;
		start();
	}

	public void run() {
		ObjectInfo ball, goal;
		int actionCode;

		// first put it somewhere on my side
		if (Pattern.matches("^before_kick_off.*", m_playMode))
			m_krislet.move(-Math.random() * 52.5, 34 - Math.random() * 68.0);

		/*- ActionCodes
		0: turn 40 and wait for new info
		1: turn to direction of ball
		2: dash towards ball with variable rate
		3: kick ball in direction of goal
		4: dash towards ball with constant rate of 20
		*/
		while (!m_timeOver) {

			ball = m_memory.getObject("ball");

			if (m_side == 'l')
				goal = m_memory.getObject("goal r");
			else
				goal = m_memory.getObject("goal l");

			actionCode = agentFunction.getAction(detectEnvironment(ball, goal));

			switch (actionCode) {
			case 0:
				m_krislet.turn(40);
				m_memory.waitForNewInfo();
				break;
			case 1:
				m_krislet.turn(ball.m_direction);
				break;
			case 2:
				m_krislet.dash(10 * ball.m_distance);
				break;
			case 3:
				m_krislet.kick(100, goal.m_direction);
				break;
			case 4:
				m_krislet.dash(20);
				break;
			}

			// sleep one step to ensure that we will not send
			// two commands in one cycle.
			try {
				Thread.sleep(2 * SoccerParams.simulator_step);
			} catch (Exception e) {
			}
		}
		m_krislet.bye();
	}

	public void hear(int time, String message) {
		if (message.compareTo("time_over") == 0)
			m_timeOver = true;

	}

	/**
	 * 
	 * @return environmentCode
	 */
	public int detectEnvironment(ObjectInfo ball, ObjectInfo goal) {
		/*- EnvironmentCodes
		 0: ball location unknown
		 1: ball location known, too far to shoot, not facing ball
		 2: ball location known, too far to shoot, less than 15m away, facing ball
		 3: ball location known, too far to shoot, more than 15m away, facing ball
		 4: ball location known, close enough to shoot, goal location unknown
		 5: ball location known, close enough to shoot, goal location known
		 */
		int environmentCode = -1;

		if (ball == null) {
			environmentCode = 0;
		} else if (ball.m_distance > 1.0) {
			if (ball.m_direction != 0)
				environmentCode = 1;
			else if (ball.m_distance < 15.0)
				environmentCode = 2;
			else
				environmentCode = 3;
		} else {
			if (goal == null) {
				environmentCode = 4;
			} else {
				environmentCode = 5;
			}
		}
		return environmentCode;
	}

	public void see(VisualInfo info) {
	}

	public void hear(int time, int direction, String message) {
	}
}
