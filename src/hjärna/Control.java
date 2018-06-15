package hjärna;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Control {

	public static final String CONFIG_PATH = "/.hjaerna";

	// command line flags
	private static final int C_ARG_SPAWN = 1;
	private static final int C_ARG_FORCE = 2;
	
	private static boolean serverIsRunning() {
		Optional<String> command = ProcessHandle.current().info().command();
		if (!command.isPresent()) {
			return false;
		}
		return ProcessHandle.allProcesses().anyMatch(process -> {
			Optional<String> running = process.info().command();
			if (!running.isPresent()) {
				return false;
			}
			return command.get().equals(running.get());
		});
	}

	private static void initializeConfig() throws IOException {
		Path directory = Paths.get(Control.getConfigPath());
		if (!directory.toFile().exists() || directory.toFile().isFile()) {
			// TODO: create config dir
			directory.toFile().mkdirs();
		}
	}
	
	public static String getConfigPath() {
		return System.getProperty("user.home") + CONFIG_PATH;
	}
	
	public static void main(String[] args) throws IOException {

		int spawnServer = 0;

		for (String arg : args) {
			switch (arg) {
			case "--spawn":
				spawnServer |= C_ARG_SPAWN;
				break;

			case "--force":
				spawnServer |= C_ARG_FORCE;
				break;
			}
		}

		initializeConfig();

		switch (spawnServer) {
		case 1:
			if (serverIsRunning()) {
				// TODO: message
				return;
			}
			// fallthrough
		case 3:
			try {
				new Server();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		default:
			new GUI();
			break;
		}
		
	}

}
