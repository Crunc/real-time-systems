package de.hda.rts.simulation.ui;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.hda.rts.simulation.Task;
import de.hda.rts.simulation.data.ScheduleModel;

public class ScheduleChart extends JScrollPane implements Observer {

	private static final int BASE_SIZE = 20;
	private static final int STEPS_OFFSET = 1;
	
	private static final long serialVersionUID = -7826978161006937802L;
	
	private final String title;
	
	private final ScheduleModel model;

	private JTable chartTable;
	
	public ScheduleChart(String title, ScheduleModel scheduleModel) {
		this.title = title;
		model = scheduleModel;
		init();
	}
	
	private void init() {
		model.addObserver(this);
		
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		setColumnHeaderView(new JLabel(title));
		setViewportView(getChartTable());
		
		setPreferredSize(new Dimension(BASE_SIZE * 40, BASE_SIZE * (model.taskCount() + 2)));
	}
	
	private JTable getChartTable() {
		if (chartTable == null) {
			final ScheduleChartRenderer renderer = new ScheduleChartRenderer();
			chartTable = new JTable(new ChartTableModel(), new ChartTableColumnModel()) {
				private static final long serialVersionUID = -662720828408890825L;
				
				@Override
				public TableCellRenderer getCellRenderer(int row, int column) {
					return renderer;
				}
			};
		}
		
		return chartTable;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg != null && arg instanceof Integer) {
			chartTable.invalidate();
			invalidate();
		}
	} 
	
	private class ChartTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1171450430824610654L;
		
		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Task";
			default:
				return Integer.toString(column);
			}
		}
		
		@Override
		public int getRowCount() {
			if (model == null) {
				return 0;
			}
			
			return model.taskCount();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Task task = model.getTask(rowIndex);

			switch (columnIndex) {
			case 0:
				return task;
			default:
				ScheduleModel.Step step = model.getStep(task, columnIndex - STEPS_OFFSET);
				return step;
			}
		}

		@Override
		public int getColumnCount() {
			return model.getStepCount() + STEPS_OFFSET;
		}
	}
	
	private class ChartTableColumnModel extends DefaultTableColumnModel implements Observer {

		private static final long serialVersionUID = -4297256125424522378L;
		
		private ChartTableColumnModel() {
			TableColumn labelCol = new TableColumn(0);
			labelCol.setPreferredWidth(100);
			addColumn(labelCol);
			model.addObserver(this);
		}
		
		@Override
		public void update(Observable o, Object arg) {
			int step = (Integer) arg;
			
			TableColumn stepCol = new TableColumn(step + STEPS_OFFSET);
			stepCol.setMaxWidth(BASE_SIZE);
			
			addColumn(stepCol);
		}
	}
}
