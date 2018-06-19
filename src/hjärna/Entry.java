package hjärna;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

class Entry extends JPanel {
	
	private static final long serialVersionUID = 273901778084834880L;
	
	private JLabel field;
	
	public Entry(String msg) {
		field = new JLabel();
		field.setText(msg);
		field.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(field);
	}
	
}