package hjärna;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.Request;
import net.Response;

public class GUI extends JFrame implements KeyListener {

	public static final String WINDOW_TITLE = "hjärna";
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 150;

	private static final long serialVersionUID = -6306066100675358193L;

	private Socket socket;
	private ObjectOutputStream requestStream;
	private ObjectInputStream responseStream;

	private List<String> poolList;
	private JTextField searchQuery;
	private JComboBox<Object> searchPool;
	private JList<Entry> resultBox;

	public GUI() throws UnknownHostException, IOException {
		socket = new Socket(Server.host, Server.port);
		requestStream = new ObjectOutputStream(socket.getOutputStream());
		responseStream = new ObjectInputStream(socket.getInputStream());
		poolList = getPools();
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		setTitle(WINDOW_TITLE);
		setBounds(screen.width / 2 - WINDOW_WIDTH / 2, screen.height / 2 - WINDOW_HEIGHT / 2, WINDOW_WIDTH,
				WINDOW_HEIGHT);
		setLayout(new BorderLayout());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				super.windowClosed(arg0);
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		initializePanel();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				searchQuery.requestFocusInWindow();
			}
		});
	}

	private void initializePanel() {
		JPanel searchBox = new JPanel();
		searchBox.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new java.awt.Insets(10, 10, 10, 10);
		c.ipady = 5;
		c.ipadx = 5;

		searchPool = new JComboBox<>(poolList.toArray());

		searchQuery = new JTextField();
		searchQuery.addKeyListener(this);
		searchQuery.setMargin(new Insets(5, 5, 5, 5));

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 0;
		c.gridx = 0;
		searchBox.add(searchPool, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 0;
		c.gridx = 1;
		c.weightx = 1.0d;
		searchBox.add(searchQuery, c);

		resultBox = new JList<>();
		resultBox.setCellRenderer(new ListCellRenderer<Entry>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends Entry> arg0, Entry entry, int arg2,
					boolean arg3, boolean arg4) {
				return entry;
			}
		});

		JScrollPane scrollPane = new JScrollPane(resultBox);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		add(searchBox, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	private List<String> getPools() {
		try {
			Request request = new Request();
			request.setType(Request.Type.INIT);
			send(request);

			Response<String> response = receive();

			return response.results;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private <T> Response<T> receive() throws ClassNotFoundException, IOException {
		return (Response<T>) responseStream.readObject();
	}

	private void send(Request request) throws IOException {
		requestStream.writeObject(request);
		requestStream.flush();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (1 < searchQuery.getText().length()) {
			Request request = new Request();
			request.setQuery(searchQuery.getText());
			request.setPool((String) searchPool.getModel().getSelectedItem());
			try {
				send(request);

				Vector<Entry> updateResults = new Vector<>();
				for (String[] line : this.<String[]>receive().results) {
					updateResults.add(new Entry(line));
				}
				resultBox.setListData(updateResults);
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

}
