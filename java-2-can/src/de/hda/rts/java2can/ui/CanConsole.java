package de.hda.rts.java2can.ui;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.io.Serializable;

import javax.swing.JTextArea;

import de.hda.rts.java2can.util.Hex;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class CanConsole extends JTextArea {

	/**
	 * Implements {@link Serializable}
	 */
	private static final long serialVersionUID = 2322409227041027763L;

	private static final int MESSAGE_SIZE = 8;

	private Thread readThread;

	private String portName;

	private boolean running;

	public CanConsole() {
		init();
	}

	private void init() {
		setEditable(false);
		setPreferredSize(new Dimension(0, 200));
	}

	public void reset(String port) {
		stop();
		setText("");
		portName = port;
		running = true;
		readThread = new Thread(new CanAppender());
		readThread.start();
	}

	public void stop() {
		if (running) {
			try {
				running = false;
				readThread.join();
			} catch (InterruptedException e) {
				System.err
						.println("interrupted while waiting for read thread to stop");
			}
		}
	}

	private void writeLine(char[] line) {
		writeLine(new String(line));
	}
	
	private void writeLine(String line) {
		append(line);
		append("\n");
	}

	private class CanAppender implements Runnable {

		@Override
		public void run() {
			SerialPort port = new SerialPort(portName);

			try {
				while (running && !Thread.interrupted()) {
					port.openPort();
					port.setParams(SerialPort.BAUDRATE_9600,
							SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);

					int available = port.getInputBufferBytesCount();

					if (available > MESSAGE_SIZE) {
						byte[] message = port.readBytes(MESSAGE_SIZE);
						char[] hexMessage = Hex.encodeHex(message);
						writeLine(hexMessage);
					}

					port.closePort();
					writeLine("waiting on " + port.getPortName());
					Thread.sleep(5000);
				}
			} catch (InterruptedException e) {
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}
	};
}
