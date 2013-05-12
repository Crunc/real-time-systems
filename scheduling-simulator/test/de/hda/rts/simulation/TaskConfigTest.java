package de.hda.rts.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

public class TaskConfigTest {

	private static final String XML =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<task-config>" +
"	<resources no-resource=\"E\">" +
"		<resource name=\"Q\" />" +
"		<resource name=\"V\" />" +
"	</resources>" +
"	<tasks>" +
"		<task name=\"D\" " +
"		    priority=\"4\" " +
"		    execution=\"EEQVE\" " +
"		    release-time=\"4\"" +
"			period=\"20\" />" +
"		" +
"		<task name=\"C\" " +
"		    priority=\"3\" " +
"		    execution=\"EVVE\" " +
"		    release-time=\"2\"" +
"			period=\"20\" />" +
"		" +
"		<task name=\"B\" " +
"		    priority=\"2\" " +
"		    execution=\"EE\" " +
"		    release-time=\"2\"" +
"			period=\"20\" />" +
"		" +
"		<task name=\"A\" " +
"		    priority=\"1\" " +
"		    execution=\"EQQQQE\" " +
"		    release-time=\"0\"" +
"			period=\"20\" />" +
"	</tasks>" +
"</task-config>";
	
	@Test
	public void testParsing() throws Exception {
		InputStream inStream = new ByteArrayInputStream(XML.getBytes("UTF-8"));
		
		TaskConfig cfg = TaskConfigParser.parse(inStream);
		
		assertNotNull(cfg);
		
		ResourceInfo rQ = cfg.getResource("Q");
		assertNotNull(rQ);
		
		ResourceInfo rV = cfg.getResource("V");
		assertNotNull(rV);
		
		TaskInfo tA = cfg.getTaskInfo("A");
		assertNotNull(tA);
		assertEquals("A", tA.getName());
		assertEquals(1, tA.getPriority());
		assertEquals(20, tA.getPeriod());
		assertEquals(20, tA.getDeadline());
		
		TaskInfo tB = cfg.getTaskInfo("B");
		assertNotNull(tB);
		assertEquals("B", tB.getName());
		assertEquals(2, tB.getPriority());
		assertEquals(20, tB.getPeriod());
		assertEquals(20, tB.getDeadline());
		
		TaskInfo tC = cfg.getTaskInfo("C");
		assertNotNull(tC);
		assertEquals("C", tC.getName());
		assertEquals(3, tC.getPriority());
		assertEquals(20, tC.getPeriod());
		assertEquals(20, tC.getDeadline());
		
		TaskInfo tD = cfg.getTaskInfo("D");
		assertNotNull(tD);
		assertEquals("D", tD.getName());
		assertEquals(4, tD.getPriority());
		assertEquals(20, tD.getPeriod());
		assertEquals(20, tD.getDeadline());
	}
}
