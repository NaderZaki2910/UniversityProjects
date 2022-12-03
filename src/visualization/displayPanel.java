package visualization;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class displayPanel extends JPanel{
	public JTextArea j;
	public displayPanel() {
		super();
		j=new JTextArea();
		//JScrollPane sc=new JScrollPane(j);
		j.setEditable(false);
		this.setLayout(new BorderLayout());
		this.add(j,BorderLayout.CENTER);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		
	}
}
