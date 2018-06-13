package hjärna;

import java.io.IOException;
import java.util.Optional;

public class Control {

	private static final int C_ARG_SPAWN = 1;
	private static final int C_ARG_FORCE = 2;

	private static boolean serverIsRunning() {
		Optional<String> command = ProcessHandle.current().info().command();
		if (!command.isPresent()) {
			return false;
		}
		return ProcessHandle
				.allProcesses()
				.anyMatch(process -> {
					Optional<String> running = process.info().command();
					if (!running.isPresent()) {
						return false;
					}
					return command.get().equals(running.get());
				});
	}
	
	public static void main(String[] args) {

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
