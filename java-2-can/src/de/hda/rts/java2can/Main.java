package de.hda.rts.java2can;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.hda.rts.java2can.ui.CanMessageFrame;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
            public void run() {
	            start();
            }
		});
	}
	
	private static void start() {
		JFrame gui = new CanMessageFrame();
		gui.setVisible(true);
    }
}
