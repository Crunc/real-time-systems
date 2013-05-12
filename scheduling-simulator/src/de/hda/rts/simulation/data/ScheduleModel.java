package de.hda.rts.simulation.data;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Observable;

import com.google.common.base.Preconditions;

import de.hda.rts.simulation.Resource;
import de.hda.rts.simulation.Task;
import de.hda.rts.simulation.util.Tasks;

public class ScheduleModel extends Observable {

	private final String name;
	private final NavigableSet<Task> tasks;
	private final Map<Task, List<Step>> steps;
	private int stepCount = 0;
	
	public ScheduleModel(String name, NavigableSet<Task> tasks) {
		this.name = name;
		this.tasks = tasks;
		steps = Tasks.newTaskMap();
		
		for (Task task: this.tasks) {
			steps.put(task, new LinkedList<Step>());
		}
	}
	
//	public void addSteps(Task task, Resource resource) {
//		for (Task t: tasks) {
//			if (t.equals(task)) {
//				getSteps(t).add(new Step(StepType.EXEC, 0, task, resource));
//			}
//			else {
//				getSteps(t).add(new Step(StepType.WAIT, 0, t, null));
//			}
//		}
//		
//		++stepCount;
//		setChanged();
//		notifyObservers(stepCount - 1);
//	}
	
	public void addStep(StepType type, Task task, Resource resource) {
		getSteps(task).add(new Step(type, 0, task, resource));
	}
	
	public void finishStep() {
		++stepCount;
		setChanged();
		notifyObservers(stepCount - 1);
	}
	
	public NavigableSet<Task> getTasks() {
		return tasks;
	}
	
	public Task getTask(int index) {
		Preconditions.checkElementIndex(index, tasks.size());
		
		int idx = 0;
		Iterator<Task> iter = tasks.iterator();
		
		while (iter.hasNext() && idx < index) {
			iter.next();
			++idx;
		}
		
		if (iter.hasNext()) {
			return iter.next();
		}
		
		return null;
	}

	public Step getStep(Task task, int i) {
		return steps.get(task).get(i);
	}
	
	public List<Step> getSteps(Task task) {
		return steps.get(task);
	}
	
	public int taskCount() {
		return steps.size();
	}
	
	public int getStepCount() {
		return steps.values().iterator().next().size();
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
		WAIT, BLOCK, EXEC, DONE;
	}
	
	public class Step {		
		public final StepType type;
		public final int priority;
		public final Resource resource;
		public final Task task;
		
		private Step(StepType type, int priority, Task task, Resource resource) {
			this.type = type;
			this.priority = priority;
			this.resource = resource;
			this.task = task;
		}
		
		@Override
		public String toString() {
			if (task != null && resource != null) {
				return MessageFormat.format("{0} ({1})", task.getName(), resource.getName());
			}
			
			return "";
		}
	}
}
