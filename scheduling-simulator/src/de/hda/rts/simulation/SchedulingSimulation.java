package de.hda.rts.simulation;

import javax.swing.SwingUtilities;

import de.hda.rts.simulation.ui.MainFrame;

public class SchedulingSimulation {
	
	@SuppressWarnings("unchecked")
	private static final Class<? extends Scheduling>[] SCHEDULINGS = (Class<? extends Scheduling>[]) new Class<?>[] {
		RateMonotonicScheduling.class,
		DeadlineMonotonicScheduling.class,
		EarliestDeadlineFirst.class,
		PriorityInheritanceProtocol.class
	};
	
	private static final String[] RESOURCES = {
		"pip-example.xml",
		"praktikum1_excercise3.xml"
	};
	
	public static void main(String... args) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new MainFrame(SCHEDULINGS, RESOURCES);
            }
        });
	}
}
