package hjärna;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class Entry extends JPanel {
	
	private static final long serialVersionUID = 273901778084834880L;
	
	private JLabel field;
	
	public Entry(String msg) {
		field = new JLabel();
		field.setText(msg);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(field);
	}
	
}