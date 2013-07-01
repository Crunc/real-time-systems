package de.hda.rts.java2can.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.common.base.Strings;

public class ComPortSettingsPanel extends JPanel {

	/**
	 * Implements {@link Serializable}
	 */
    private static final long serialVersionUID = 6251438798987817907L;

    private JLabel comPortNameLabel;
    
    private ComPortChooser comPortChooser;

	private ComPortSelectedListener comPortSelectedListener;
    
	public interface ComPortSelectedListener {
		void onPortSelected(String portName);
	}
	
	public void setComPortSelectedListener(ComPortSelectedListener listener) {
		comPortSelectedListener = listener;
	}
	
	public ComPortSettingsPanel() {
		init();
	}

	private void init() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(Box.createRigidArea(new Dimension(5,0)));
		add(getComPortNameLabel());
		add(Box.createRigidArea(new Dimension(5,0)));
		add(getComPortChooser());
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				String portName = comPortChooser.getSelectedPortName();
				
				if (!Strings.isNullOrEmpty(portName)) {
					onComportSelected(portName);
				}
			}
		});
	}

	private Component getComPortNameLabel() {
		if( comPortNameLabel == null) {
			comPortNameLabel = new JLabel("COM Port:");
		}
		
	    return comPortNameLabel;
    }

	private Component getComPortChooser() {
		if (comPortChooser == null) {
			comPortChooser = new ComPortChooser();
			comPortChooser.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent event) {
					if (event.getStateChange() == ItemEvent.SELECTED) {
						String portName = (String) event.getItem();
						onComportSelected(portName);
					}
				}
			});
		}
		
	    return comPortChooser;
    }

	private void onComportSelected(String portName) {
		if (comPortSelectedListener != null) {
			comPortSelectedListener.onPortSelected(portName);
		}
	}

	public String getPortName() {
	    return comPortChooser.getSelectedPortName();
    }
}
