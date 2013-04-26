package de.hda.rts.simulation;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SchedulingSimulation {
	
	private static final String[] RESOURCES = {
		"praktikum1_excercise3.txt", 
		"slide_example_s11.txt", 
		"slide_example_s14.txt",
		"slide_example_s16.txt"
	};
	
	public static void main(String... args) throws FileNotFoundException {
		for (String resource: RESOURCES) {
			InputStream resourceStream = openResource(resource);
			if (resourceStream != null) {
				System.out.print("--- ");
				System.out.print(resource);
				System.out.println(" ----------------------------------------------");
				System.out.println();
				
				List<TaskInfo> tasks = TaskInfo.parseConfiguration(resourceStream);
				
				for (Scheduling scheduling : createSchedulings(tasks)) {
					scheduling.makeSchedule(400);
					scheduling.printReport(System.out);
				}
				
				System.out.print("---");
				for (int i = 0; i < resource.length() + 2; ++i) {
					System.out.print("-");
				}
				System.out.println("----------------------------------------------");
			}
			else {
				System.err.println("failed to open resource: " + resource);
			}
			System.out.println();
			System.out.println();
		}
	}

	private static List<Scheduling> createSchedulings(Collection<TaskInfo> tasks) {
		Scheduling[] schedulings = new Scheduling[] { 
				new RateMonotonicScheduling(new ArrayList<TaskInfo>(tasks)),
				new DeadlineMonotonicScheduling(new ArrayList<TaskInfo>(tasks))
			};

		return Arrays.asList(schedulings);
	}

	private static InputStream openResource(String path) {
		return SchedulingSimulation.class.getClassLoader().getResourceAsStream(
				path);
	}
}
