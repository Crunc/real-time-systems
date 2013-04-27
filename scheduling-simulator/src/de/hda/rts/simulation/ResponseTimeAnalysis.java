package de.hda.rts.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import de.hda.rts.simulation.util.Tasks;

public class ResponseTimeAnalysis extends Scheduling {
	private final Map<Task, Integer> responseTimes;
	
	public ResponseTimeAnalysis(List<TaskInfo> taskInfos) {
		super(taskInfos);
		responseTimes = calculateResponseTimes(getTasks());
	}

	@Override
	protected Comparator<Task> getPriorityComparator() {
		return new Comparator<Task>() {
			@Override
			public int compare(Task left, Task right) {
				Integer leftRt = responseTimes.get(left);
				Integer rightRt = responseTimes.get(right);
				return leftRt - rightRt;
			}
		};
	}
	
	private Map<Task, Integer> calculateResponseTimes(List<Task> tasks) {
		// copy the tasks to preserve their order
		List<Task> tasksByPeriod = new ArrayList<Task>(tasks);
		
		// sort the copied tasks by period
		Collections.sort(tasksByPeriod, Tasks.PERIOD_COMPARATOR);
		
		Map<Task, Integer> responseTimes = Tasks.newTaskMap();
		
		for (int idx = 0; idx < tasksByPeriod.size(); ++idx) {
			final Task task = tasksByPeriod.get(idx);
			final int responseTime = calculateResponseTime(idx, tasksByPeriod, responseTimes);

			responseTimes.put(task, responseTime);
		}
		
		print(responseTimes);
		return responseTimes;
	}
	
	private int calculateResponseTime(int taskIdx, List<Task> tasksByPeriod, Map<Task, Integer> responseTimes) {
		int c = tasksByPeriod.get(taskIdx).getInfo().getComputationTime();
		
		if (taskIdx == 0) {
			return c;
		}
		else {
			
			int rOld = c;
			int r = -1;
			
			while (r != rOld) {
				rOld = r;
				r = c;
				
				for (int idx = taskIdx - 1; idx >= 0; --idx) {
					TaskInfo info =  tasksByPeriod.get(idx).getInfo();
					int ta = info.getPeriod();
					int ca = info.getComputationTime();
					
					int s = (int) Math.ceil((double) rOld / ta);
					
					r += s*ca;
				}
			}
			
			return r;
		}		
	}
	
	@Override
	public String toString() {
		return "RTA";
	}
	
	private void print(Map<Task, Integer> responseTimes) {
		for (Task t : responseTimes.keySet()) {
			System.out.println(t.getInfo().getName() + " => " + responseTimes.get(t));
		}
	}
}
