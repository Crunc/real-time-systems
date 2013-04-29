package de.hda.rts.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ResponseTimeAnalysis extends Scheduling {
//	private final List<Task> rtaList;
	
	public ResponseTimeAnalysis(List<TaskInfo> taskInfos) {
		super(taskInfos);
//		rtaList = sortByResponseTime(getTasks());
		
//		if (rtaList == null) {
//			System.err.println("no optimal priorities were found");
//		}
	}

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

			System.out.printf("[%d] %s: %d\n", k, set.get(k), rta);
		}
		
		if (ok) {
			return set;
		}
		else {
			return null;
		}
	}
	
	private int calculateResponseTime(int taskIdx, List<Task> tasks) {
		int c = tasks.get(taskIdx).getInfo().getComputationTime();
		
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
					TaskInfo info =  tasks.get(idx).getInfo();
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
