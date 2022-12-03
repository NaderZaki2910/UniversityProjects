package visualization;

import java.awt.Color;

import java.awt.Image;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import code.Hos;
import code.Pad;

public class gridPanel extends JPanel {
	public static final Color VERY_DAK_GREEN = new Color(0 ,155, 0);
	public static final Color DARK_YELLOW = new Color(255 ,155, 0);
	boolean neo;
	boolean agent;
	int dam;
	Hos hos;
	boolean pill;
	boolean TB;
	boolean newAgent;
	Pad pad;

	public JLabel neoLabel=new JLabel();
	public JLabel otherLabel=new JLabel();

	
	public static int dim=50;
	public gridPanel() {
		super();
		this.setLayout(null);
		setLayout(null);
		this.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
		updateView();
		revalidate();
		repaint();

	}

	public void updateView() {
		this.remove(neoLabel);
		this.remove(otherLabel);
		if (neo) {
			neoLabel = new JLabel("NEO: "+dam, SwingConstants.CENTER);
			neoLabel.setForeground(Color.GRAY);
			neoLabel.setBounds(0, 25, dim, dim);
			neoLabel.setVisible(true);
			this.add(neoLabel);
		}
		if(agent) {
			otherLabel=new JLabel("AGENT",SwingConstants.CENTER);
			otherLabel.setForeground(Color.RED);
			otherLabel.setBounds(0, 0, dim, dim);
			otherLabel.setVisible(true);
			this.add(otherLabel);
			return;
		}
		if(hos!=null) {
			otherLabel=new JLabel("H: "+hos.damage,SwingConstants.CENTER);
			otherLabel.setForeground(VERY_DAK_GREEN);
			otherLabel.setBounds(0, 0, dim, dim);
			otherLabel.setVisible(true);
			this.add(otherLabel);
			return;
		}
		if(TB) {
			otherLabel=new JLabel("TB",SwingConstants.CENTER);
			otherLabel.setForeground(Color.BLACK);
			otherLabel.setBounds(0, 0, dim, dim);
			otherLabel.setVisible(true);
			this.add(otherLabel);
			return;
		}
		if(newAgent) {
			otherLabel=new JLabel("MUTANT",SwingConstants.CENTER);
			otherLabel.setForeground(Color.MAGENTA);
			otherLabel.setBounds(0, 0, dim, dim);
			otherLabel.setVisible(true);
			this.add(otherLabel);
			return;
		}
		if(pad!=null) {
			otherLabel=new JLabel("PAD: "+pad.FinishLoc.x+"-"+pad.FinishLoc.y,SwingConstants.CENTER);
			otherLabel.setForeground(DARK_YELLOW);
			otherLabel.setBounds(0, 0, dim, dim);
			otherLabel.setVisible(true);
			this.add(otherLabel);
			return;
		}
		if(pill) {
			otherLabel=new JLabel("PILL",SwingConstants.CENTER);
			otherLabel.setForeground(DARK_YELLOW);
			otherLabel.setBounds(0, 0, dim, dim);
			otherLabel.setVisible(true);
			this.add(otherLabel);
			return;
		}
		
		revalidate();
		repaint();
	}

	public static void main(String[] args) {

	}

}
