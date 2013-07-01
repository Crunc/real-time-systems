package de.hda.rts.java2can;

import java.io.PrintStream;

import jssc.SerialPort;
import jssc.SerialPortException;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.hda.rts.java2can.util.Hex;

public class MessageSender {

	private static final PrintStream out = System.out;
	private static final PrintStream err = System.err;

	public void sendMessage(String comPortName, byte[] message) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(comPortName),
				"comPortName must not be null nor empty");
		Preconditions
				.checkArgument(message != null, "message must not be null");

		out.print("sending message on [");
		out.print(comPortName);
		out.print("]: ");
		out.println(Hex.encodeHex(message));

		try {
			SerialPort port = new SerialPort(comPortName);
			port.openPort();
			port.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			port.writeBytes(message);
			port.closePort();
		} catch (SerialPortException e) {
			err.println(e.getMessage());
		}
	}
}
