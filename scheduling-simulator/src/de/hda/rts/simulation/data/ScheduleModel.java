package de.hda.rts.simulation.data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import de.hda.rts.simulation.Task;
import de.hda.rts.simulation.util.Tasks;

public class ScheduleModel extends Observable {

	private final String name;
	private final List<Task> tasks;
	private final Map<Task, List<Step>> steps;
	private int stepCount = 0;
	
	public ScheduleModel(String name, List<Task> tasks) {
		this.name = name;
		this.tasks = tasks;
		steps = Tasks.newTaskMap();
		
		for (Task task: this.tasks) {
			steps.put(task, new LinkedList<Step>());
		}
	}
	
	public void addStep(Task task) {
		int idx = 0;
		int taskIdx = -1;
		for (Task t: tasks) {
			if (t.equals(task)) {
				steps.get(t).add(new Step(task));
				taskIdx = idx;
			}
			else {
				steps.get(t).add(new Step(null));
			}
			
			++idx;
		}
		
		++stepCount;
		setChanged();
		notifyObservers(taskIdx);
	}
	
	public List<Task> getTasks() {
		return tasks;
	}
	
	public int taskCount() {
		return steps.size();
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
