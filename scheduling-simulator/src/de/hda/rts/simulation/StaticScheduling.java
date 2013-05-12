package de.hda.rts.simulation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.hda.rts.simulation.util.Tasks;

public abstract class StaticScheduling extends Scheduling {

	private Map<Task, Integer> priorities;
	
	@Override
	protected void calculatePriorities() {
		if (priorities == null) {
			priorities = Maps.newTreeMap(Tasks.NAME_COMPARATOR);
			
			List<Task> tasks = Lists.newArrayList(getTasks());
			int priority = tasks.size();
			Collections.sort(tasks, getPriorityComparator());
			
			for (Task task: tasks) {
				priorities.put(task, priority);
				--priority;
			}
		}
	}
	
	@Override
	protected int getPriority(Task task) {
		return priorities.get(task);
	}
	
	protected abstract Comparator<Task> getPriorityComparator();
}
