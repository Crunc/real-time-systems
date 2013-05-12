package de.hda.rts.simulation;

import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import de.hda.rts.simulation.util.Log;

public class TaskConfigParser extends DefaultHandler2 {

	private static final String TAG = TaskConfigParser.class.getSimpleName();

	private final TaskConfig.Builder config;

	private TaskInfo.Builder taskInfo;
	private ResourceInfo.Builder resourceInfo;

	private Map<String, SaxStartAction> startActions = Maps.newHashMap();
	private Map<String, SaxEndAction> endActions = Maps.newHashMap();

	private interface SaxStartAction {
		void handle(String uri, String localName, String qName, Attributes attributes);
	}

	private interface SaxEndAction {
		void handle(String uri, String localName, String qName);
	}

	private TaskConfigParser() {
		config = TaskConfig.builder();
		initStartActions();
		initEndActions();
	}

	public static TaskConfig parse(InputStream inStream) {
		Preconditions.checkArgument(inStream != null, "inStream must not be null");

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			
			TaskConfigParser handler = new TaskConfigParser(); 
			parser.parse(inStream, handler);
			return handler.config.build();
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void initStartActions() {
		startActions.put("resources", new SaxStartAction() {
			@Override
			public void handle(String uri, String localName, String qName, Attributes attributes) {
				String noRes = attributes.getValue("no-resource");
				config.noResource(noRes);
			}
		});

		startActions.put("resource", new SaxStartAction() {
			@Override
			public void handle(String uri, String localName, String qName, Attributes attributes) {
				String name = attributes.getValue("name");

				resourceInfo = ResourceInfo.builder();

				if (name != null) {
					resourceInfo.name(name);
				}
			}
		});

		startActions.put("task", new SaxStartAction() {
			@Override
			public void handle(String uri, String localName, String qName, Attributes attributes) {
				String name = attributes.getValue("name");
				String execution = attributes.getValue("execution");
				int period = parseInt(attributes, "period", 0);
				int deadline = parseInt(attributes, "deadline", 0);
				int priority = parseInt(attributes, "priority", 0);

				taskInfo = TaskInfo.builder();

				if (!Strings.isNullOrEmpty(name)) {
					taskInfo.name(name);
				}

				if (!Strings.isNullOrEmpty(execution)) {
					taskInfo.execution(execution);
				}

				if (period > 0) {
					taskInfo.period(period);
				}

				if (deadline > 0) {
					taskInfo.deadline(deadline);
				}

				if (priority > 0) {
					taskInfo.priority(priority);
				}
			}
		});
	}

	void initEndActions() {
		endActions.put("resource", new SaxEndAction() {
			@Override
			public void handle(String uri, String localName, String qName) {
				ResourceInfo res = resourceInfo.build();
				resourceInfo = null;
				config.resourceInfo(res);				
			}
		});

		endActions.put("task", new SaxEndAction() {
			@Override
			public void handle(String uri, String localName, String qName) {
				TaskInfo ti = taskInfo.build();
				taskInfo = null;
				config.taskInfo(ti);				
			}
		});
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		SaxStartAction action = startActions.get(qName);

		if (action != null) {
			action.handle(uri, localName, qName, attributes);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		SaxEndAction action = endActions.get(qName);

		if (action != null) {
			action.handle(uri, localName, qName);
		}
	}

	int parseInt(Attributes attributes, String name, int defaultValue) {
		Preconditions.checkArgument(attributes != null, "attributes must not be null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "name must not be null nor empty");

		String value = attributes.getValue(name);

		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException ex) {
				Log.e(TAG, "failed to parse attribute {0}=\"{1}\"", name, value);
			}
		}

		return defaultValue;
	}
}
