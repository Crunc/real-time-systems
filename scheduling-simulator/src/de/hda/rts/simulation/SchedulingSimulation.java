package de.hda.rts.simulation;

import javax.swing.SwingUtilities;

import de.hda.rts.simulation.ui.MainFrame;

public class SchedulingSimulation {
	
	@SuppressWarnings("unchecked")
	private static final Class<? extends Scheduling>[] SCHEDULINGS = (Class<? extends Scheduling>[]) new Class<?>[] {
		RateMonotonicScheduling.class,
		DeadlineMonotonicScheduling.class,
		ResponseTimeAnalysis.class,
		EarliestDeadlineFirst.class
	};
	
	private static final String[] RESOURCES = {
		"praktikum1_excercise3.txt", 
		"slide_example_s11.txt", 
		"slide_example_s14.txt",
		"slide_example_s16.txt",
		"slide_example_s20.txt"
	};
	
	public static void main(String... args) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new MainFrame(SCHEDULINGS, RESOURCES);
            }
        });
	}
}
