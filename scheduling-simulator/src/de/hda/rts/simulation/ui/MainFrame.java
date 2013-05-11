package de.hda.rts.simulation.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;

import de.hda.rts.simulation.Scheduling;
import de.hda.rts.simulation.TaskConfig;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 5852868179983533113L;
	
	private static final String TITLE = "Scheduling Simulation";
	private static final String LIST_LABEL = "Choose a task configuration";
	
	private final Class<? extends Scheduling>[] schedulingTypes;
	private final String[] resources;
	private ListModel resourceDataModel;
	
	private JLabel listLabel;
	private JList resourceList;
	
	public MainFrame(Class<? extends Scheduling>[] schedulingTypes, String[] resources) {
		this.schedulingTypes = schedulingTypes;
		this.resources = resources;
		init();
	}
	
	private void init() {
		setTitle(TITLE);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel resourcePanel = new JPanel();
		resourcePanel.setLayout(new BoxLayout(resourcePanel, BoxLayout.PAGE_AXIS));
		
		resourcePanel.add(getListLabel());
		resourcePanel.add(Box.createRigidArea(new Dimension(0,5)));
		resourcePanel.add(getResourceList());
		resourcePanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		for (Class<? extends Scheduling> schedulingType: schedulingTypes) {
			resourcePanel.add(createSchedulingButton(schedulingType));
			resourcePanel.add(Box.createRigidArea(new Dimension(0,5)));
		}
		
		resourcePanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
		
		setContentPane(resourcePanel);
		
		pack();
		setVisible(true);
	}
	
	private JLabel getListLabel() {
		if (listLabel == null) {
			listLabel = new JLabel(LIST_LABEL);
			listLabel.setAlignmentX(LEFT_ALIGNMENT);
		}
		
		return listLabel;
	}
	
	private JList getResourceList() {
		if (resourceList == null) {
			resourceList = new JList(getResourceDateModel());
			resourceList.setAlignmentX(LEFT_ALIGNMENT);
		}
		
		return resourceList;
	}
	
	private ListModel getResourceDateModel() {
		if (resourceDataModel == null) {
			DefaultListModel model = new DefaultListModel();
			
			for (String resource: resources) {
				model.addElement(resource);
			}
			
			resourceDataModel = model;
		}
		
		return resourceDataModel;
	}
	
	private JButton createSchedulingButton(final Class<? extends Scheduling> schedulingType) {
		JButton button = new JButton(schedulingType.getSimpleName());
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String resourcePath =  (String) resourceList.getSelectedValue();
				if (resourcePath != null) {
					InputStream resourceStream = openResource(resourcePath);
					
					if (resourceStream != null) {
						TaskConfig config = new TaskConfig().parse(resourceStream);
						Scheduling scheduling = null;
						
						try {
							Constructor<? extends Scheduling> ctor = schedulingType.getConstructor();
							scheduling = ctor.newInstance();
							scheduling.initialize(config);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						
						if (scheduling != null) {
							ScheduleChartFrame frame = new ScheduleChartFrame(scheduling);
							frame.setTitle(new StringBuilder()
									.append(scheduling).append(" (")
									.append(resourcePath).append(")")
									.toString());
						}
					}
				}
			}
		});
		
		return button;
	}
	
	private InputStream openResource(String path) {
		return getClass().getClassLoader().getResourceAsStream(path);
	}
}
