package de.hda.rts.java2can.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;

import javax.swing.JTextField;

import com.google.common.primitives.Chars;

import de.hda.rts.java2can.util.Hex;

public class JHexField extends JTextField {

	/**
	 * Implements {@link Serializable}
	 */
	private static final long serialVersionUID = -2949594236545963446L;

	private static final char[] ALLOWED_CHARS = { 
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'A', 'B', 'C', 'D', 'E', 'F' };
	
	private int length = -1;

	public JHexField(int length) {
		super();
		this.length = length;
		
		init();
	}
	
	private void init() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = getText();
				
				if (text.length() == length) {
					e.consume();
				}
				else {
					char c = e.getKeyChar();
					
					if (!Chars.contains(ALLOWED_CHARS, c)) {
						char cu = Character.toUpperCase(c);
						
						if (Chars.contains(ALLOWED_CHARS, cu)) {
							e.setKeyChar(cu);
						}
						else {
							e.consume();
						}
					}
				}
			}
		});
	}
	
	public byte[] getData() {
		String input = getText();
		
		if (input == null || input.length() == 0) {
			return new byte[0];
		}
		
		int len = Math.min(length, input.length());
		
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
}
