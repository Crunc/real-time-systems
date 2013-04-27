package de.hda.rts.simulation.ui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.hda.rts.simulation.Scheduling;

public class ScheduleChartFrame extends JFrame {
	private static final long serialVersionUID = 5334279915240146307L;
	
	private final Scheduling scheduling;
	private ScheduleChartPane scheduleChartPane;
	
	public ScheduleChartFrame(Scheduling scheduling) {
		this.scheduling = scheduling;
		init();
	}
	
	private void init() {
		setTitle(scheduling.toString());
		setLocationRelativeTo(null);
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(getScheduleChartPane());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
		setContentPane(contentPanel);
		
		pack();
		setVisible(true);
	}
	
	private ScheduleChartPane getScheduleChartPane() {
		if (scheduleChartPane == null) {
			scheduleChartPane = new ScheduleChartPane(scheduling);
		}
		
		return scheduleChartPane;
	}
}
