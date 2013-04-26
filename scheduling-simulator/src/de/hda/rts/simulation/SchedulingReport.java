package de.hda.rts.simulation;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SchedulingReport {

	private final String name;
	private final Map<Task, List<Step>> steps;
	private int stepCount = 0;
	
	public SchedulingReport(String name, List<Task> tasks) {
		this.name = name;
		steps = new TreeMap<Task, List<Step>>(new Comparator<Task>() {
			@Override
			public int compare(Task left, Task right) {
				return left.getInfo().getName().compareTo(right.getInfo().getName());
			}
		});
		
		for (Task task: tasks) {
			steps.put(task, new LinkedList<Step>());
		}
	}
	
	public void addStep(Task task) {		
		for (Map.Entry<Task, List<Step>> taskSteps: steps.entrySet()) {
			if (taskSteps.getKey().equals(task)) {
				taskSteps.getValue().add(new Step(task));
			}
			else {
				taskSteps.getValue().add(new Step(null));
			}
		}
		
		++stepCount;
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();

		out.append(name).append("\n");
		
		for (int i = 0; i < stepCount; ++i) {
			out.append("---");
		}
		out.append("\n");
		
		for (Map.Entry<Task, List<Step>> taskSteps: steps.entrySet()) {
			for (Step step : taskSteps.getValue()) {
				out.append(step).append("  ");
			}
			out.append("| ").append(taskSteps.getKey()).append("\n");
		}

		for (int i = 0; i < stepCount; ++i) {
			out.append("---");
		}
		out.append("\n");
		
		for (int i = 1; i <= stepCount; ++i) {
			if (i < 10) {
				out.append(i).append("  ");
			}
			else if (i < 100) {
				out.append(i).append(" ");
			}
			else {
				out.append(i);
			}
			
		}
		out.append("\n");
		
		return out.toString();
	}
	
	private class Step {		
		public Task task;
		
		private Step(Task task) {
			this.task = task;
		}
		
		@Override
		public String toString() {
			if (task != null) {
				return task.getInfo().getName();
			}
			else {
				return " ";
			}
		}
	}
}
