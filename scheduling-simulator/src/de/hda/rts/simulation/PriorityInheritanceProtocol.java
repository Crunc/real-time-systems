package de.hda.rts.simulation;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.hda.rts.simulation.util.Log;
import de.hda.rts.simulation.util.Resources;
import de.hda.rts.simulation.util.Tasks;

public class PriorityInheritanceProtocol extends Scheduling {
	private static final String TAG = "PIP";
	
	private Map<Task, Integer> priorities = Maps.newTreeMap(Tasks.NAME_COMPARATOR);
	
	@Override
	protected void calculatePriorities() {
		priorities.clear();
		
		Map<Resource, Set<Task>> resourceMap = Maps.newTreeMap(Resources.NAME_COMPARATOR);
		
		for (Resource res: getResources()) {
			resourceMap.put(res, Sets.newTreeSet(Tasks.NAME_COMPARATOR));
		}
		
		for (Task task: getTasks()) {
			if (task.isWaiting()) {
				String resName = task.getNextExecutionStep();
				
				if (resName != null) {
					Resource res = getResource(resName);
					
					if (res != null && res != Resource.NO_RESOURCE) {
						resourceMap.get(res).add(task);
					}
				}
			}
		}
		
		for (Map.Entry<Resource, Set<Task>> entry: resourceMap.entrySet()) {
			int maxPrio = 0;
			
			for (Task task: entry.getValue()) {
				maxPrio = Math.max(maxPrio, task.getPriority());
			}
			
			for (Task task: entry.getValue()) {
				priorities.put(task, maxPrio);
			}
		}
		
		StringBuilder dbg = new StringBuilder();
		for (Map.Entry<Task, Integer> entry: priorities.entrySet()) {
			dbg.append(entry.getKey().getName())
				.append(": ").append(entry.getValue()).append(" | ");
			
		}
		
		Log.d(TAG, dbg.toString());
	}

	@Override
	protected int getPriority(Task task) {
		Integer priority = priorities.get(task);
		
		if (priority == null) {
			priority = task.getPriority();
		}
		
		return priority;
	}
}
