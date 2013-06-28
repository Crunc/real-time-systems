package de.hda.rts.java2can.ui;

import java.io.Serializable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import jssc.SerialPortList;

public class ComPortChooser extends JComboBox<String> {

	/**
	 * Implements {@link Serializable}
	 */
    private static final long serialVersionUID = -7151413585719456464L;


    public ComPortChooser() {
    	super();
    	init();
    }
    
    private void init() {
    	setModel(new DefaultComboBoxModel<String>() {
            private static final long serialVersionUID = -2152880581757291229L;

			{
    			for(String portName: SerialPortList.getPortNames()) {
    				addElement(portName);
    			}
    		}
    	});
    }
    
    public String getSelectedPortName() {
    	return (String) getSelectedItem();
    }
}
