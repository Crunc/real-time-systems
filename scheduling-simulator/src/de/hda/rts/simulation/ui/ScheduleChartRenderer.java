package de.hda.rts.simulation.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.hda.rts.simulation.Resource;
import de.hda.rts.simulation.Task;
import de.hda.rts.simulation.data.ScheduleModel;
import de.hda.rts.simulation.util.Tasks;

public class ScheduleChartRenderer implements TableCellRenderer {

	private TaskLabel labelCell = new TaskLabel();
	private StepCell stepCell = new StepCell();
	
	private Color defaultColor = Color.white;
	private Color blockedColor = Color.black;
	private Color notReleasedColor = Color.lightGray;
	
	private List<Color> colors = Lists.newArrayList(Color.cyan, Color.red, Color.green, Color.magenta);
	private Map<Task, Color> taskColors = Maps.newTreeMap(Tasks.NAME_COMPARATOR);
	

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof ScheduleModel.Step) {
			stepCell.update(table, (ScheduleModel.Step) value, isSelected, hasFocus, row, column);
			return stepCell;
		}
		else if (value instanceof Task){
			labelCell.update((Task) value);
			return labelCell;
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	protected synchronized Color getColor(ScheduleModel.Step step) {
		Color color;
		
		switch (step.type) {
		case EXECUTING:
			color = taskColors.get(step.task);
			
			if (color == null) {
				if (!colors.isEmpty()) {
					color = colors.remove(0);
				}
				else {
					color = Color.black;
				}
				
				taskColors.put(step.task, color);
			}
			break;
			
//		case BLOCKED:
//			color = blockedColor;
//			break;
		
//		case NOT_RELEASED:
//			color = notReleasedColor;
//			break;
			
		default:
			color = defaultColor;
			break;
		
		}
		
		return color;
	}
	
	protected String getLabel(ScheduleModel.Step step) {
		if (step.type == ScheduleModel.StepType.WAITING || step.resource == null || step.resource == Resource.NO_RESOURCE) {
			return null;
		}
		
		return step.resource.getName();
	}
	
	private class StepCell extends JLabel {
		private static final long serialVersionUID = -6239665620665589451L;

		private StepCell() {
			setOpaque(true); // MUST do this for background to show up.
		}
		
		private void update(JTable table, ScheduleModel.Step step, boolean isSelected, boolean hasFocus, int row, int column) {
			Color color = getColor(step);
			setBackground(color);
			setText(getLabel(step));
			setToolTipText(step.toString());
			
			if (step.type == ScheduleModel.StepType.BLOCKED) {
				setForeground(Color.white);
			}
			else {
				setForeground(Color.darkGray);
			}
		}
	}
	
	private class TaskLabel extends JPanel {
		private static final long serialVersionUID = 4506348987453741486L;

		private JLabel nameLabel;
		private JLabel cLabel;
		private JLabel pLabel;
		private JLabel dLabel;
		
		public TaskLabel() {
			init();
		}
		
		private void init() {
			setPreferredSize(new Dimension(140, 20));
			setMaximumSize(new Dimension(140, 20));
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
			add(getNameLabel());
			add(Box.createRigidArea(new Dimension(5,0)));
			add(getPLabel());
			add(Box.createRigidArea(new Dimension(5,0)));
			add(getDLabel());
			add(Box.createRigidArea(new Dimension(5,0)));
			add(getCLabel());
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
		
		public void update(Task task) {
			nameLabel.setText(task.getInfo().getName());
			cLabel.setText(String.format("C:%d/%d", task.getComputed(), task.getInfo().getComputationTime()));
			pLabel.setText(String.format("P:%d", task.getInfo().getPeriod()));
			dLabel.setText(String.format("D:%d", task.getInfo().getDeadline()));
		}
	}
}
