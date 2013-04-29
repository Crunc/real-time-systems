package de.hda.rts.simulation.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import de.hda.rts.simulation.Task;
import de.hda.rts.simulation.data.ScheduleModel;

public class ScheduleChart extends JScrollPane implements Observer {

	private static final int BASE_SIZE = 20;
	
	private static final Color CLEAR_COLOR = Color.white;
	private static final Color STEP_BACKGROUND_COLOR = new Color(0xFFFAFAFA);
	private static final Color EMPTY_STEP_BACKGROUND_COLOR = new Color(0xFFEFEFEF);
	private static final Font STEP_FONT = new Font("Arial", Font.PLAIN, 8);
	
	private static final Color[] ROW_COLORS = {
		Color.cyan, Color.red, Color.green, Color.magenta
	};
	
	private static final long serialVersionUID = -7826978161006937802L;
	
	private final String title;
	
	private final ScheduleModel model;

	private final List<Integer> steps;
	
	private JPanel labelPanel;
	
	private ChartPanel chartPanel;
	
	public ScheduleChart(String title, ScheduleModel model) {
		this.title = title;
		this.model = model;
		this.steps = new ArrayList<Integer>(200);
		init();
	}
	
	private void init() {
		model.addObserver(this);
		
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		setColumnHeaderView(new JLabel(title));
		setRowHeaderView(getLabelPanel());
		setViewportView(getChartPanel());
		
		setPreferredSize(new Dimension(BASE_SIZE * 40, BASE_SIZE * (model.taskCount() + 2)));
	}
	
	private JPanel getLabelPanel() {
		if (labelPanel == null) {
			labelPanel = new ChartLabelPanel();
		}
		
		return labelPanel;
	}
	
	private ChartPanel getChartPanel() {
		if (chartPanel == null) {
			chartPanel = new ChartPanel();
		}
		
		return chartPanel;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg != null && arg instanceof Integer) {
			steps.add((Integer) arg);
			chartPanel.invalidate();
			invalidate();
		}
	} 
	
	private class ChartPanel extends JPanel {
		private static final long serialVersionUID = 1978177582257630561L;

		public ChartPanel() {
			init();
		}
		
		private void init() {
			setPreferredSize(new Dimension(BASE_SIZE * 200, BASE_SIZE * model.taskCount()));
		}
		
		public void paintComponent(Graphics g) {
	        super.paintComponent(g);       

	        // clear the complete background with the clear color
	        g.setColor(CLEAR_COLOR);
	        g.fillRect(0, 0, getWidth(), getHeight());

	        // the height of a column [px]
	        int colHeight = model.taskCount() * BASE_SIZE;
	        
	        for (int idx = 0; idx < steps.size(); ++idx) {

	        	int taskIdx = steps.get(idx);
	        	
	        	int x = idx * BASE_SIZE;
	        	
	        	g.setColor(STEP_BACKGROUND_COLOR);
        		g.fillRect(x, 0, BASE_SIZE, model.taskCount() * BASE_SIZE);
	        	
	        	if (taskIdx > -1) {
		        	Task task = model.getTasks().get(taskIdx);
		        	int y = steps.get(idx) * BASE_SIZE;
		        	
		        	final Color c = ROW_COLORS[taskIdx % ROW_COLORS.length];
		        	
		        	g.setColor(c);
		        	g.fillRect(x + 1, y + 1, BASE_SIZE - 2, BASE_SIZE - 2);
		        	
		        	g.setColor(Color.black);
		        	g.setFont(STEP_FONT);
		        	g.drawString(String.valueOf(idx), x + BASE_SIZE / 2 - 4, y + BASE_SIZE / 2 + 4);
	        	}
	        	else {
	        		g.setColor(EMPTY_STEP_BACKGROUND_COLOR);
	        		g.fillRect(x, 0, BASE_SIZE, colHeight);
	        	}
	        }
	    }
		
		@Override
		public Dimension getPreferredSize() {
			int width = Math.max(BASE_SIZE * 200, ScheduleChart.this.getPreferredSize().width);
			return new Dimension(width, BASE_SIZE * model.taskCount());
		}
	}
	
	private class ChartLabelPanel extends JPanel implements Observer {
		private static final long serialVersionUID = -5212153513130318501L;
		
		private final List<TaskLabel> taskLabels;
		
		public ChartLabelPanel() {
			this.taskLabels = new ArrayList<TaskLabel>(model.taskCount());
			model.addObserver(this);
			init();
		}
		
		private void init() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			for (Task task: model.getTasks()) {
				TaskLabel label = new TaskLabel(task);
				taskLabels.add(label);
				add(label);
			}
			
			updateLabels();
		}
		
		private void updateLabels() {
			for (TaskLabel taskLabel: taskLabels) {
				taskLabel.update();
			}
		}

		@Override
		public void update(Observable o, Object arg) {
			updateLabels();
		}
	}
	
	private class TaskLabel extends JPanel {
		private static final long serialVersionUID = 4506348987453741486L;

		private final Task task;
		
		private JLabel nameLabel;
		private JLabel cLabel;
		private JLabel pLabel;
		private JLabel dLabel;
		
		public TaskLabel(Task task) {
			this.task = task;
			init();
		}
		
		private void init() {
			setPreferredSize(new Dimension(BASE_SIZE * 7, BASE_SIZE));
			setMaximumSize(new Dimension(BASE_SIZE * 7, BASE_SIZE));
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
			add(getNameLabel());
			add(Box.createRigidArea(new Dimension(5,0)));
			add(getPLabel());
			add(Box.createRigidArea(new Dimension(5,0)));
			add(getDLabel());
			add(Box.createRigidArea(new Dimension(5,0)));
			add(getCLabel());
			
			update();
		}
		
		private JLabel getNameLabel() {
			if (nameLabel == null) {
				nameLabel = new JLabel();
			}
			
			return nameLabel;
		}
		
		private JLabel getCLabel() {
			if (cLabel == null) {
				cLabel = new JLabel();
			}
			
			return cLabel;
		}
		
		private JLabel getPLabel() {
			if (pLabel == null) {
				pLabel = new JLabel();
			}
			
			return pLabel;
		}
		
		private JLabel getDLabel() {
			if (dLabel == null) {
				dLabel = new JLabel();
			}
			
			return dLabel;
		}
		
		public void update() {
			nameLabel.setText(task.getInfo().getName());
			cLabel.setText(String.format("C:%d/%d", task.getComputed(), task.getInfo().getComputationTime()));
			pLabel.setText(String.format("P:%d", task.getInfo().getPeriod()));
			dLabel.setText(String.format("D:%d", task.getInfo().getDeadline()));
		}
	}
}
