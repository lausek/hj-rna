package hjärna;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

class Entry extends JPanel {

	private static final long serialVersionUID = 273901778084834880L;

	private JLabel field;

	public Entry(String[] msg) {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		for (int i = 0; i < msg.length; i++) {
			field = new JLabel();
			field.setText(msg[i]);
			field.setBorder(new EmptyBorder(5, 5, 5, 5));
			add(field);
		}
	}

}