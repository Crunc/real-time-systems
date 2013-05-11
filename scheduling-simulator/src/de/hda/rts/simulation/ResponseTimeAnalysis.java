package de.hda.rts.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ResponseTimeAnalysis extends Scheduling {

	@Override
	protected Comparator<Task> getPriorityComparator() {
		return new Comparator<Task>() {
			private final List<Task> rtaList = sortByResponseTime(getTasks());
			
			@Override
			public int compare(Task left, Task right) {
				Integer leftRt = rtaList.indexOf(left);
				Integer rightRt = rtaList.indexOf(right);
				return leftRt - rightRt;
			}
		};
	}
	
//	private Map<Task, Integer> calculateResponseTimes(List<Task> tasks) {
//		// copy the tasks to preserve their order
//		List<Task> tasksByPeriod = new ArrayList<Task>(tasks);
//		
//		// sort the copied tasks by period
//		Collections.sort(tasksByPeriod, Tasks.PERIOD_COMPARATOR);
//		
//		Map<Task, Integer> responseTimes = Tasks.newTaskMap();
//		
//		for (int idx = 0; idx < tasksByPeriod.size(); ++idx) {
//			final Task task = tasksByPeriod.get(idx);
//			final int responseTime = calculateResponseTime(idx, tasksByPeriod, responseTimes);
//
//			responseTimes.put(task, responseTime);
//		}
//		
//		print(responseTimes);
//		return responseTimes;
//	}
	
	private List<Task> sortByResponseTime(List<Task> tasks) {
		List<Task> set = new ArrayList<Task>(tasks);

		boolean ok = true;
		for (int k = set.size() - 1; ok && k >= 0; --k) {
			
			int rta = Integer.MAX_VALUE;
			
			for (int next = k; ok && next >= 0; --next) {
				if (next > k) {
					Collections.swap(set, k, next);
				}
				
				rta = calculateResponseTime(k, set);				
				
				if (rta > set.get(k).getInfo().getDeadline()) {
					ok = false;
				}
			}
		}
		
		if (ok) {
			return set;
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return "RTA";
	}
	
	void print(Map<Task, Integer> responseTimes) {
		for (Task t : responseTimes.keySet()) {
			System.out.println(t.getInfo().getName() + " => " + responseTimes.get(t));
		}
	}
}
