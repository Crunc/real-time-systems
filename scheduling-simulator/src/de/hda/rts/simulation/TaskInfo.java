package de.hda.rts.simulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TaskInfo {
	private final String name;
	private final int computationTime;
	private final int period;
	private final int deadline;
	private final String strRepresentation;

	private TaskInfo(String taskName, int c, int p, int d) {
		name = taskName;
		computationTime = c;
		period = p;
		deadline = d;
		strRepresentation = new StringBuilder(name).append(" C:")
				.append(computationTime).append(" P:").append(period)
				.append(" D:").append(deadline).toString();
	}

	public String getName() {
		return name;
	}

	public int getComputationTime() {
		return computationTime;
	}

	public int getPeriod() {
		return period;
	}

	public int getDeadline() {
		return deadline;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskInfo other = (TaskInfo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return strRepresentation;
	}

	public static TaskInfo parseTask(String config) {
		if (config == null) {
			throw new IllegalArgumentException("config must not be null");
		}

		StringTokenizer tokens = new StringTokenizer(config, " ");

		if (tokens.countTokens() < 3) {
			throw new IllegalArgumentException(
					"config must contain at least a name, a computationTime and a period: "
							+ config);
		}

		// ----------------------------------------------------------
		// name
		// ----------------------------------------------------------
		String nameToken = tokens.nextToken().trim();

		if (nameToken == null || nameToken.length() == 0) {
			throw new IllegalArgumentException(
					"config must start with a task name: " + config);
		}

		// ----------------------------------------------------------
		// computationTime
		// ----------------------------------------------------------
		int computationTime = -1;
		String computationTimeToken = tokens.nextToken().trim();

		if (computationTimeToken == null || computationTimeToken.length() == 0) {
			throw new IllegalArgumentException(
					"config must contain a computationTime: " + config);
		}

		try {
			computationTime = Integer.parseInt(computationTimeToken);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException(
					"computationTime must be a numeric value: " + config);
		}

		// ----------------------------------------------------------
		// period
		// ----------------------------------------------------------
		int period = -1;
		String periodToken = tokens.nextToken().trim();
		if (periodToken == null || periodToken.length() == 0) {
			throw new IllegalArgumentException("config must contain a period: "
					+ config);
		}

		try {
			period = Integer.parseInt(periodToken);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException(
					"period must be a numeric value: " + config);
		}

		// ----------------------------------------------------------
		// deadline
		// ----------------------------------------------------------
		int deadline = period;
		if (tokens.hasMoreTokens()) {

			String deadlineToken = tokens.nextToken().trim();
			if (deadlineToken != null && deadlineToken.length() > 0) {
				try {
					deadline = Integer.parseInt(deadlineToken);
				} catch (NumberFormatException ex) {
					throw new IllegalArgumentException(
							"deadline must be a numeric value: " + config);
				}
			}
		}

		return new TaskInfo(nameToken, computationTime, period, deadline);
	}

	public static List<TaskInfo> parseConfiguration(InputStream inStream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inStream));
		List<TaskInfo> tasks = new ArrayList<TaskInfo>();

		int lineCount = 0;
		String line = null;

		do {
			try {
				++lineCount;
				line = reader.readLine();
				
				if (line != null) {
					line = line.trim();
					
					if(!isComment(line)) {
						TaskInfo task = TaskInfo.parseTask(line);
						tasks.add(task);
					}
				}
			} catch (IOException ex) {
				System.err.println("failed to read line " + lineCount);
			} catch (IllegalArgumentException ex) {
				System.err.println("failed to parse line " + lineCount + ": "
						+ ex.getMessage());
			}
		} while (line != null);

		return tasks;
	}
	
	private static boolean isComment(String line) {
		return line.startsWith("#") || line.startsWith("//");
	}
}
