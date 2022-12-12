package view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainScreen extends JPanel{
	
	private JLabel bckgnd;
	public JButton btn;
	
	public MainScreen() {
		this.setLayout(new BorderLayout(0,10));
		btn = new JButton(new ImageIcon("pokebtn.png"));
		btn.setOpaque(false);
		btn.setBorderPainted(false);
		btn.setFocusable(false);
		btn.setContentAreaFilled(false);
		//this.add(new JLabel(new ImageIcon("empt.png")), BorderLayout.CENTER);
		//this.add(new JLabel(new ImageIcon("empt.png")), BorderLayout.EAST);
		this.add(btn, BorderLayout.CENTER);
		btn.setName("Start");
	}
	@Override
	  protected void paintComponent(Graphics g) {

	    super.paintComponent(g);
	    Image bgImage;
	    ImageIcon i = new ImageIcon("title.png");
	    bgImage= i.getImage();
	        g.drawImage(bgImage, 0, 0, this.getSize().width, this.getSize().height, this);
	}
}
