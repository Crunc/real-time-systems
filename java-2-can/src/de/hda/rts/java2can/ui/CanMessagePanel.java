package de.hda.rts.java2can.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.hda.rts.java2can.util.Hex;

public class CanMessagePanel extends JPanel {

	/**
	 * Implements {@link Serializable}
	 */
	private static final long serialVersionUID = 1L;
	
	private static final PrintStream out = System.out;
	
	private JLabel canIdLabel;

	private JTextField canIdField;

	private JLabel canDataLabel;
	
	private JHexField canDataField;
	
	private JButton sendButton;
	
	private OnSendClickedHandler onSendClickedHandler;
	
	public interface OnSendClickedHandler {
		void onSendClicked(int id, byte[] data);
	}
	
	public CanMessagePanel() {
		init();
	}

	private void init() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(Box.createRigidArea(new Dimension(5,0)));
		add(getCanIdLabel());
		add(Box.createRigidArea(new Dimension(5,0)));
		add(getCanIdField());
		add(Box.createRigidArea(new Dimension(5,0)));
		add(getCanDataLabel());
		add(Box.createRigidArea(new Dimension(5,0)));
		add(getCanDataField());
		add(Box.createRigidArea(new Dimension(5,0)));
		add(getSendButton());
	}
	
	public void setOnSendClickedHandler(OnSendClickedHandler handler) {
		onSendClickedHandler = handler;
	}
	
	private void onSendButtonClicked(ActionEvent e) {
		int id = readCanIdField();
		byte[] data = readCanDataField();

		if (onSendClickedHandler != null) {
			onSendClickedHandler.onSendClicked(id, data);
		}
	}

	private int readCanIdField() {
		try {
			return Integer.parseInt(canIdField.getText());
		}
		catch (NumberFormatException e) {
			return 0xFFFF;
		}
	}
	
	private byte[] readCanDataField() {
		return canDataField.getData();
    }
	
	private byte[] readInput(String input, int maxLen) {
		if (input == null || input.length() == 0) {
			return new byte[0];
		}
		
		int len = Math.min(maxLen, input.length());
		
		char[] buf = null;
		
		if (len % 2 == 0) {
			buf = new char[len];
			input.getChars(0, Math.min(buf.length, input.length()), buf, 0);
		}
		else {
			buf = new char[1 + len];
			buf[0] = '0';
			input.getChars(0, Math.min(buf.length - 1, input.length()), buf, 1);
		}

		return Hex.decodeHex(buf);
	}

	private JLabel getCanIdLabel() {
		if (canIdLabel == null) {
			canIdLabel = new JLabel("CAN ID:");
		}

		return canIdLabel;
	}

	private JTextField getCanIdField() {
		if (canIdField == null) {
			canIdField = new JTextField();
			canIdField.setPreferredSize(new Dimension(100, 25));
		}

		return canIdField;
	}
	
	private JLabel getCanDataLabel() {
		if (canDataLabel == null) {
			canDataLabel = new JLabel("Data:");
		}

		return canDataLabel;
	}

	private JTextField getCanDataField() {
		if (canDataField == null) {
			canDataField = new JHexField(8);
			canDataField.setPreferredSize(new Dimension(100, 25));
		}

		return canDataField;
	}

	private JButton getSendButton() {
	    if (sendButton == null) {
	    	sendButton = new JButton("Send");
	    	sendButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onSendButtonClicked(e);
				}
			});
	    }
	    
	    return sendButton;
    }
}
