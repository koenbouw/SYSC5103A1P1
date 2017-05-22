package soccer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AgentFunction {

	// environment code X stores action code in agentFunction[X]
	private int[] agentFunction;
	private final int NUM_ENVIRONMENTS = 5;
	private final int NUM_ACTIONS = 5;

	public AgentFunction(String fileName) {
		agentFunction = new int[NUM_ENVIRONMENTS];
		File file = new File(fileName);
		parseTextFile(file);
	}

	private void parseTextFile(File file) {
		Scanner scanner;
		String line;
		int actionCode;
		int EnvironmentCode;

		try {
			scanner = new Scanner(file);
			while (scanner.hasNext()) {
				line = scanner.nextLine().replaceAll("\\s", "");

				// End of function codes
				if (line.charAt(0) == '-') {
					break;
				}

				// read environment index
				EnvironmentCode = Integer.parseInt(line.substring(line.indexOf("Environment") + 11, line.indexOf(':')));
				actionCode = Integer.parseInt(line.substring(line.indexOf("Action") + 6));

				if (EnvironmentCode > NUM_ENVIRONMENTS || actionCode > NUM_ACTIONS) {
					throw new IllegalArgumentException("Found out of bounds environment code or action code");
				}
				agentFunction[EnvironmentCode] = actionCode;

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public int getAction(int environmentCode) {
		return agentFunction[environmentCode];
	}

	public static void main(String[] args) {
		AgentFunction agentFunction = new AgentFunction("agentConfiguration.txt");
	}
}
