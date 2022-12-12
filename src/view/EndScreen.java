package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class EndScreen extends JPanel{
	public JTextArea j;
	
	public EndScreen() {
		this.setLayout(new BorderLayout(650,300));
		j = new JTextArea();
		j.setFont(new Font(Font.MONOSPACED, Font.BOLD, 35));
		j.setEditable(false);
		JPanel p = new JPanel(new BorderLayout()) {
			@Override
			  protected void paintComponent(Graphics g) {

			    super.paintComponent(g);
			    Image bgImage;
			    ImageIcon i = new ImageIcon("empt.png");
			    bgImage= i.getImage();
			        g.drawImage(bgImage, 0, 0, this.getSize().width, this.getSize().height, this);
			}	
		};
		j.setOpaque(false);
		JLabel j1 = new JLabel(new ImageIcon("empt.png"));
		JLabel j2 = new JLabel(new ImageIcon("empt.png"));
		p.add(j, BorderLayout.CENTER);
		this.add(j1, BorderLayout.EAST);
		this.add(new JLabel(new ImageIcon("empt.png")), BorderLayout.NORTH);
		this.add(new JLabel(new ImageIcon("empt.png")), BorderLayout.SOUTH);
		this.add(j2, BorderLayout.WEST);
		this.add(p, BorderLayout.CENTER);
	}
	@Override
	  protected void paintComponent(Graphics g) {

	    super.paintComponent(g);
	    Image bgImage;
	    ImageIcon i = new ImageIcon("gameover.png");
	    bgImage= i.getImage();
	        g.drawImage(bgImage, 0, 0, this.getSize().width, this.getSize().height, this);
	}
}
