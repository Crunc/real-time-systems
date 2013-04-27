package de.hda.rts.simulation.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.hda.rts.simulation.Scheduling;

public class ScheduleChartPane extends JPanel {
	private static final long serialVersionUID = -9032945695938831857L;
	
	private final Scheduling scheduling;
	
	private ScheduleChart scheduleChart;
	private JButton stepButton;
	
	public ScheduleChartPane(Scheduling scheduling) {
		this.scheduling = scheduling;
		init();
	}
	
	private void init() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(getScheduleChart());
		add(getStepButton());

		setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
	}
	
	private ScheduleChart getScheduleChart() {
		if (scheduleChart == null) {
			scheduleChart = new ScheduleChart(scheduling.getAnalyzation(), scheduling.getModel());
		}
		
		return scheduleChart;
	}
	
	private JButton getStepButton() {
		if (stepButton == null) {
			stepButton = new JButton(">");
			stepButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					scheduling.doNextStep(400);
					scheduleChart.repaint();
				}
			});
		}
		
		return stepButton;
	}
}
