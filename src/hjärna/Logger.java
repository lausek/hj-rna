package hjärna;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public enum Level {
		INFO,
		WARNING,
		ERROR,
	}
	
	private OutputStream stream;
	public BufferedWriter writer;
	
	public Logger(String path) {
		try {
			File file = new File(path);
			stream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			if (stream == null) {
				stream = System.out;
			}
			put("Logger couldn't open logfile. Writing to stdout.", Level.WARNING);
		}
	}
	
	public void put(String msg) {
		put(msg, Level.INFO);
	}
	
	public void put(String msg, Level level) {
		Calendar calendar = Calendar.getInstance();
		String formatted = String.format("[%s] %s: %s\n", dateFormat.format(calendar.getTime()), level.toString(), msg);
		try {
			stream.write(formatted.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
