package jku.win.systemsoftware.simpledebugger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class Redirection extends Thread {
	
	private Reader in;
	private Writer out;
	
	public Redirection(InputStream is, OutputStream os) {
		super();
		in = new InputStreamReader(is);
		out = new OutputStreamWriter(os);
	}
	
	public void run() {
		char[] buf = new char[1024];
		try {
			for (int length = in.read(buf); length >= 0; length = in.read(buf)){
				out.write(buf, 0, length);
			}
			out.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
