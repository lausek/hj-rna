package hjärna;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.Request;
import net.Response;

public class Server {

	public static final String host = "localhost";
	public static final int port = 7777;

	private Logger logger;
	private ServerSocket socket;
	private Map<String, SearchPool> pools;

	public Server() throws UnknownHostException, IOException {
		logger = new Logger(Control.CONFIG_PATH + "/server.log");
		socket = new ServerSocket(port);
		pools = new HashMap<>();

		loadPools();

		handle();
	}

	private void loadPools() {
		// TODO: load pools
	}

	private List<String> search(Request request) {
		List<String> found = new java.util.ArrayList<>();
		found.add("here");
		found.add("result");
		return found;
	}

	private void handle() {
		Socket client;
		Request request;

		while (true) {
			try {
				logger.put("Listening...");
				client = socket.accept();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			logger.put("Client connected");
			
			try {
				ObjectInputStream requestStream = new ObjectInputStream(client.getInputStream());
				ObjectOutputStream responseStream = new ObjectOutputStream(client.getOutputStream());

				do {
					try {
						request = (Request) requestStream.readObject();
						logger.put(request.toString());

						Response response = new Response();
						switch (request.type) {
						case SEARCH:
							response.setResults(search(request));
							break;
						default:
							break;
						}

						responseStream.writeObject(response);
						responseStream.flush();
					} catch (ClassNotFoundException | IOException e1) {
						e1.printStackTrace();
						break;
					}
				} while (client.isConnected());

				client.close();

			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

		}
	}

}
