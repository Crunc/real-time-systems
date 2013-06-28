package de.hda.rts.java2can.ui;

import java.awt.BorderLayout;
import java.io.PrintStream;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.hda.rts.java2can.MessageSender;

public class CanMessageFrame extends JFrame {

	/**
	 * Implements {@link Serializable}
	 */
    private static final long serialVersionUID = -6252586763588571222L;
    
    private static final PrintStream out = System.out;
    private static final PrintStream err = System.err;
    
    private static final String TITLE = "Send a CAN Message";

    private final MessageSender sender;
    
	private JPanel layoutPanel;
	
	private ComPortSettingsPanel comPortSettingsPanel;
	
	private CanMessagePanel canMessagePanel;

    public CanMessageFrame() {
		sender = new MessageSender();
    	init();
    }
    
	private void init() {
		setTitle(TITLE);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(getLayoutPanel());
		
		pack();
		setVisible(true);
	}
	
	private JPanel getLayoutPanel() {
		if (layoutPanel == null) {
			layoutPanel = new JPanel();
			layoutPanel.setLayout(new BorderLayout());
			
			layoutPanel.add(getComPortSettingsPanel(), BorderLayout.NORTH);
			layoutPanel.add(getMessagePanel(), BorderLayout.SOUTH);
		}
		
		return layoutPanel;
	}
	
	private ComPortSettingsPanel getComPortSettingsPanel() {
	    if (comPortSettingsPanel == null) {
	    	comPortSettingsPanel = new ComPortSettingsPanel();
	    }
	    
	    return comPortSettingsPanel;
    }

	private CanMessagePanel getMessagePanel() {
		if (canMessagePanel == null) {
			canMessagePanel = new CanMessagePanel();
			
			canMessagePanel.setOnSendClickedHandler(new CanMessagePanel.OnSendClickedHandler() {
				
				@Override
				public void onSendClicked(int id, byte[] data) {
					String comPortName = comPortSettingsPanel.getPortName();
					
					if (comPortName != null) {
						byte idh = (byte) (id >> 8);
						byte idl = (byte) id;
						
						byte[] message = new byte[2 + data.length];
						message[0] = idh;
						message[1] = idl;
						System.arraycopy(data, 0, message, 2, data.length);
						
						sender.sendMessage(comPortName, message);
					}
					else {
						err.println("no COM port selected");
					}
				}
			});
		}
		
		return canMessagePanel;
	}
}
