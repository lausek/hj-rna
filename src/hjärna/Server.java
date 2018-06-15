package hjärna;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hjärna.Logger.Level;
import hjärna.parser.Parser;
import net.Request;
import net.Response;

public class Server {

	public static final String host = "localhost";
	public static final int port = 7777;

	private Logger logger;
	private ServerSocket socket;
	private Map<String, SearchPool> pools;
	private boolean running = true;

	public Server() throws UnknownHostException, IOException {
		logger = new Logger(Control.getConfigPath() + "/server.log");
		socket = new ServerSocket(port);
		pools = new HashMap<>();

		loadPools();

		handle();
	}

	private void loadPools() throws FileNotFoundException, IOException {
		Map<String, Object> config;
		Path configFile;
		
		configFile = Paths.get(Control.getConfigPath() + "/config.toml");
		
		// if no config file exists -> initialize with empty one
		if (!Files.exists(configFile)) {
			try (PrintWriter pw = new PrintWriter(configFile.toFile())) {
				pw.append("");
			}
		} else {

			// try to load config file
			config = Parser.loadFile(configFile);

			// check if the config file contains search pool
			if (!config.containsKey("pool")) {
				logger.put("No pools defined", Level.ERROR);
				throw new IOException();
			}
			
			// create the pools from config
			List<Map<String, Object>> rawPools = (List<Map<String, Object>>) config.get("pool");
			for (Map<String, Object> rawPool : rawPools) {
				String name = (String) rawPool.get("name");
				SearchPool pool = SearchPool.factory(rawPool);
				
				this.pools.put(name, pool);
			}
		}
	}

	private List<String> search(Request request) {
		if (request.pool != null) {
			return pools.get(request.pool).search(request);
		} else {
			String pool = pools.keySet().iterator().next();
			return pools.get(pool).search(request);
		}
	}

	private void cleanup() {
		logger.put("Server is shutting down...");
		for (SearchPool pool : pools.values()) {
			if (pool.hasPendingChanges()) {
				try {
					pool.serialize();
				} catch(IOException e) {
					logger.put(e.getMessage(), Level.WARNING);
				}
			}
		}
	}
	
	private void handle() {
		Socket client;
		Request request;

		while (running) {
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
						logger.put(e1.getMessage(), Level.WARNING);
						break;
					}
				} while (client.isConnected());

			} catch (IOException e) {
				logger.put(e.getMessage(), Level.WARNING);
				continue;
			} finally {
				try {
					client.close();
				} catch (IOException e) {
				}
				logger.put("Client disconnected...");
			}
		}
		
		cleanup();
	}

}
