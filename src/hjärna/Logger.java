package hjärna;

import java.io.BufferedWriter;
import java.io.File;

public class Logger {

	public enum Level {
		INFO,
		WARNING,
		ERROR,
	}
	
	public BufferedWriter writer;
	
	public Logger(String path) {
		File file = new File(path);
		// TODO: implement
	}
	
	public void put(String msg) {
		put(msg, Level.INFO);
	}
	
	public void put(String msg, Level level) {
		// TODO: implement
		System.out.println(msg);
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
	
}
