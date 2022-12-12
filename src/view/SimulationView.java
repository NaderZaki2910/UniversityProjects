package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import sun.audio.*;
import view.GameScreen.CustomTextArea;

import javax.*;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.jws.soap.SOAPBinding.Style;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import javax.swing.*;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class SimulationView extends JFrame {
	private JMenuBar bar;
	private JMenu m;
	private JRootPane root;
	GameScreen gmSc;
	MainScreen mnSc;
	EndScreen enSc;
	
	public SimulationView(GameScreen gmSc, MainScreen mnSc, EndScreen enSc ) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout(100, 100));
		this.gmSc = gmSc;
		this.mnSc = mnSc;
		this.enSc = enSc;
		root = new JRootPane();
		bar = new JMenuBar();
		this.setRootPane(root);
		JButton j = new JButton(new ImageIcon("close.png"));
		j.setBorderPainted(false);
		j.setContentAreaFilled(false);
		j.setOpaque(false);
		j.setFocusable(false);
		j.setDefaultCapable(true);
		j.setName("close");
		gmSc.btns.add(j);
		bar.setLayout(new BorderLayout());
		bar.setBackground(new Color(40,40,40));
		bar.add(j, BorderLayout.EAST);
		gmSc.btns.get(0).setVisible(false);
		bar.add(gmSc.btns.get(0));
		bar.setBorderPainted(false);
		root.setJMenuBar(bar);
		this.setContentPane(mnSc);
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setVisible(true);
	}

	public void ChangeScreenGame() {
		this.setContentPane(gmSc);
		getBtns().get(0).setVisible(true);
		this.invalidate();
		this.validate();
		this.repaint();
	}
	
	public void ChangeScreenEnd(String message) {
		this.setContentPane(enSc);
		getBtns().get(0).setVisible(false);
		enSc.j.setText(message);
		this.invalidate();
		this.validate();
		this.repaint();
	}
	public Container getPnlCit1() {
		return gmSc.getPnlCit1();
	}

	public Container getPnlUnit() {
		return gmSc.getPnlUnit();
	}

	public ArrayList<JButton[]> getGrid() {
		return gmSc.getGrid();
	}

	public ArrayList<JButton> getBtns() {
		return gmSc.getBtns();
	}

	public CustomTextArea getDetails() {
		return gmSc.getDetails();
	}

	public CustomTextArea getLog() {
		return gmSc.getLog();
	}

	public JPanel getPnlInfUnit() {
		return gmSc.getPnlInfUnit();
	}

	public JPanel getPnlGrid() {
		return gmSc.getPnlGrid();
	}	
}	
