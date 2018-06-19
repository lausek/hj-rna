package hjärna;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.Request;

public class SearchPool implements Serializable {

	private static final long serialVersionUID = -857056150410418414L;
	
	private List<String> content;
	private String name;
	// are there changes waiting for serialization?
	private boolean changesPending;

	private SearchPool() {
	}

	public static SearchPool factory(Map<String, Object> rawPool) throws IOException {
		SearchPool pool;
		String name = (String) rawPool.get("name");
		Path cacheFile = Paths.get(Control.getConfigPath() + "/cache/" + name);

		if (Files.exists(cacheFile)) {
			// TODO: load file from cache
			try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(cacheFile.toFile()))) {
				pool = (SearchPool) stream.readObject();
				return pool;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		pool = new SearchPool();
		if (rawPool.containsKey("file")) {
			List<Map<String, Object>> files = (List<Map<String, Object>>) rawPool.get("file");
			pool.name = name;
			pool.content = new ArrayList<>();
			for (Map<String, Object> file : files) {
				pool.loadFile(file);
			}
			pool.serialize();
		}
		return pool;
	}

	private void loadFile(Map<String, Object> fileDescription) throws IOException {
		String path, type;

		if (!fileDescription.containsKey("path")) {
			throw new IOException("File need path");
		}

		path = (String) fileDescription.get("path");
		type = fileDescription.containsKey("type") ? (String) fileDescription.get("type") : "text";

		try (FileReader stream = new FileReader(new File(path))) {
			try (BufferedReader reader = new BufferedReader(stream)) {
				String line;
				while ((line = reader.readLine()) != null) {
					content.add(line);
				}
			}
		}
	}

	public boolean hasPendingChanges() {
		return this.changesPending;
	}

	public void serialize() throws FileNotFoundException, IOException {
		File cacheFolder = new File(Control.getConfigPath() + "/cache/");
		if (!cacheFolder.exists()) {
			cacheFolder.mkdirs();
		}

		Path cacheFile = Paths.get(Control.getConfigPath() + "/cache/" + name);
		try (FileOutputStream fileStream = new FileOutputStream(cacheFile.toFile())) {
			try (ObjectOutputStream objectStream = new ObjectOutputStream(fileStream)) {
				objectStream.writeObject(this);
			}
		}
	}

	public List<String> search(Request request) {
		return content
				.stream()
				.filter(line -> line.contains(request.query))
				.collect(Collectors.toList());
	}

}
