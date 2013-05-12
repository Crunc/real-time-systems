package de.hda.rts.simulation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SchedulingTest {

	protected static TaskConfig config;
	protected Scheduling scheduling;
	
	@BeforeClass
	public static void setUpClass() {
		config = TaskConfig.builder()
				.noResource("E")
				.resourceInfo(ResourceInfo.builder().name("V").build())
				.resourceInfo(ResourceInfo.builder().name("Q").build())
				.taskInfo(TaskInfo.builder().name("A").execution("E").period(3).build())
				.taskInfo(TaskInfo.builder().name("B").execution("E").period(6).build())
				.taskInfo(TaskInfo.builder().name("C").execution("E").period(5).build())
				.taskInfo(TaskInfo.builder().name("D").execution("EE").period(10).deadline(9).build())
				.build();
	}
	
	@Before
	public void setUp() {
		scheduling = new RateMonotonicScheduling();
		scheduling.initialize(config);
	}
	
	@Test
	public void testCalculateResponseTime() {
		int rtaA = scheduling.rta(scheduling.getTask("A"));
		int rtaB = scheduling.rta(scheduling.getTask("B"));
		int rtaC = scheduling.rta(scheduling.getTask("C"));
		int rtaD = scheduling.rta(scheduling.getTask("D"));
		
		assertEquals(1, rtaA);
		assertEquals(3, rtaB);
		assertEquals(2, rtaC);
		assertEquals(9, rtaD);
	}
}
