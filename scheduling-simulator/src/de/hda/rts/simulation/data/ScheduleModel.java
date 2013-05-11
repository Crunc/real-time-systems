package de.hda.rts.simulation.data;

import java.text.MessageFormat;
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
				getSteps(t).add(new Step(StepType.EXECUTE, 0, "E"));
				taskIdx = idx;
			}
			else {
				getSteps(t).add(new Step(StepType.WAIT, 0, ""));
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
	
	public Task getTask(int index) {
		return tasks.get(index);
	}
	
	public List<Step> getSteps(Task task) {
		return steps.get(task);
	}
	
	public int taskCount() {
		return steps.size();
	}
	
	public int getStepCount() {
		return stepCount;
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
	
	public enum StepType {
		WAIT, EXECUTE, RESOURCE;
	}
	
	public class Step {		
		public final StepType type;
		public final int priority;
		public final String resource;
		
		private Step(StepType type, int priority, String resource) {
			this.type = type;
			this.priority = priority;
			this.resource = resource;
		}
		
		@Override
		public String toString() {
			return MessageFormat.format("{0}", resource);
		}
	}
}
