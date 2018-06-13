package hjärna;

import javax.swing.JPanel;
import javax.swing.JTextField;

class Entry extends JPanel {
	
	private static final long serialVersionUID = 273901778084834880L;
	
	private JTextField field;
	
	public Entry(String msg) {
		field = new JTextField();
		field.setText(msg);
		
		add(field);
	}
	
}