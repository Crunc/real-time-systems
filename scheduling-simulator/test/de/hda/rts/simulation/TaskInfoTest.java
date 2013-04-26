package de.hda.rts.simulation;

import org.junit.Assert;
import org.junit.Test;

public class TaskInfoTest {

	@Test
	public void testParseTask() {
		TaskInfo task = TaskInfo.parseTask("A 1 2 3");
		Assert.assertNotNull(task);
		Assert.assertEquals("A", task.getName());
		Assert.assertEquals(1, task.getComputationTime());
		Assert.assertEquals(2, task.getPeriod());
		Assert.assertEquals(3, task.getDeadline());
		
		task = TaskInfo.parseTask("    B  4    5     6    ");
		Assert.assertNotNull(task);
		Assert.assertEquals("B", task.getName());
		Assert.assertEquals(4, task.getComputationTime());
		Assert.assertEquals(5, task.getPeriod());
		Assert.assertEquals(6, task.getDeadline());
		
		task = TaskInfo.parseTask("C 7 8");
		Assert.assertNotNull(task);
		Assert.assertEquals("C", task.getName());
		Assert.assertEquals(7, task.getComputationTime());
		Assert.assertEquals(8, task.getPeriod());
		Assert.assertEquals(8, task.getDeadline());
		
		try {
			TaskInfo.parseTask(null);
			Assert.fail();
		}
		catch (Exception e) {
			
		}
		
		try {
			TaskInfo.parseTask("");
			Assert.fail();
		}
		catch (Exception e) {
			
		}
		
		try {
			TaskInfo.parseTask("A 1");
			Assert.fail();
		}
		catch (Exception e) {
			
		}
		
		try {
			TaskInfo.parseTask("A 1 F");
			Assert.fail();
		}
		catch (Exception e) {
			
		}
	}
}
