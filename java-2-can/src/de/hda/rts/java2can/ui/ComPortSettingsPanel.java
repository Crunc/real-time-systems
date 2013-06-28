package de.hda.rts.java2can.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ComPortSettingsPanel extends JPanel {

	/**
	 * Implements {@link Serializable}
	 */
    private static final long serialVersionUID = 6251438798987817907L;

    private JLabel comPortNameLabel;
    
    private ComPortChooser comPortChooser;
    
	public ComPortSettingsPanel() {
		init();
	}

	private void init() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(Box.createRigidArea(new Dimension(5,0)));
		add(getComPortNameLabel());
		add(Box.createRigidArea(new Dimension(5,0)));
		add(getComPortChooser());
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
		}
		
	    return comPortChooser;
    }

	public String getPortName() {
	    return comPortChooser.getSelectedPortName();
    }
}
