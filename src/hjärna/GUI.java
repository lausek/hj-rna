package hjärna;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import net.Request;
import net.Response;

public class GUI extends JFrame implements KeyListener {

	public static final String WINDOW_TITLE = "hjärna";
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 100;

	private static final long serialVersionUID = -6306066100675358193L;

	private Socket socket;
	private JTextField searchFor;
	private JList<Entry> resultBox;

	public GUI() {
		try {
			socket = new Socket(Server.host, Server.port);
		} catch (IOException e) {
			// TODO: server not available
			e.printStackTrace();
			return;
		}

		setTitle(WINDOW_TITLE);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLayout(new BorderLayout());
		
		initializePanel();
		
		setVisible(true);
	}

	private void initializePanel() {
		searchFor = new JTextField();
		searchFor.addKeyListener(this);
		
		resultBox = new JList<>();
		resultBox.setCellRenderer(new ListCellRenderer<Entry>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends Entry> arg0, Entry entry, int arg2,
					boolean arg3, boolean arg4) {
				return entry;
			}
		});
		
		add(searchFor, BorderLayout.NORTH);
		add(resultBox, BorderLayout.CENTER);
	}

	private void send() {
		try {
			ObjectOutputStream requestStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream responseStream = new ObjectInputStream(socket.getInputStream());
					
			Request request = new Request();
			request.setQuery(searchFor.getText());
			requestStream.writeObject(request);
			requestStream.flush();
			
			Response response = (Response) responseStream.readObject();
			
			Vector<Entry> updateResults = new Vector<>();
			for (String line : response.results) {
				updateResults.add(new Entry(line));
			}
			resultBox.setListData(updateResults);
			
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (1 < searchFor.getText().length()) {
			send();
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

}
