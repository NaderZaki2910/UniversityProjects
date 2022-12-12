package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class GameScreen extends JPanel{
	// to store current position 
		private Long currentFrame; 
		private Clip clip; 
			
			// current status of clip 
		private String status; 
		private AudioInputStream audioInputStream; 
		private static String filePath; 
		private JPanel pnlAll, pnlCit1, pnlInfUnit, pnlCitGrid, pnlGrid, pnlInfo, pnlUnit, text, t3, t4, t5;
		private JLayeredPane gmended;
		private JScrollPane scroll1,scroll2, scroll3, scroll4;
		private JLabel bckgnd;
		public GridBagConstraints co;
		private CustomTextArea Log, Details;
		private ArrayList<JButton[]> grid = new ArrayList<>();
		ArrayList<JButton> btns = new ArrayList<>();
		private ArrayList<ArrayList<JButton>> cit = new ArrayList<>();
		private ArrayList<JButton> units = new ArrayList<>();
		
		public GameScreen() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			pnlAll = new JPanel(new BorderLayout(10,10));
			bckgnd = new JLabel(new ImageIcon("bckgnd.png"));
			CitGridmaker();
			InfUnitmaker();
			this.setLayout(new BorderLayout(10, 10));
			this.add(pnlAll, BorderLayout.CENTER);
			pnlAll.add(t3, BorderLayout.CENTER);
			pnlAll.add(t4, BorderLayout.EAST);
			pnlCitGrid.add(pnlCit1, BorderLayout.NORTH);
			pnlAll.setBackground(new Color(0, 0, 0, 0));
			pnlAll.setOpaque(false);
			
		}
		public JLayeredPane getGmended() {
			return gmended;
		}

		public void setGmended(JLayeredPane gmended) {
			this.gmended = gmended;
		}
		
		@SuppressWarnings("serial")
		private void Gridmaker(){
			pnlGrid = new JPanel(new GridLayout(10, 10, 0, 0)){
				@Override
				  protected void paintComponent(Graphics g) {

				    super.paintComponent(g);
				    Image bgImage;
				    ImageIcon i = new ImageIcon("Grid.png");
				    bgImage= i.getImage();
				        g.drawImage(bgImage, 0, 0, this.getSize().width, this.getSize().height, this);
				}
			};
			for(int i = 0 ; i < 10 ; i++) {
				grid.add(new JButton[10]);
			}
			grid.get(0)[0] = new JButton();
			grid.get(0)[0].setFocusable(false);
			grid.get(0)[0].setOpaque(false);
			grid.get(0)[0].setContentAreaFilled(false);
			grid.get(0)[0].setBorderPainted(false);
			grid.get(0)[0].setName("base");
			pnlGrid.add(grid.get(0)[0]);
			pnlGrid.setOpaque(false);
			grid.get(0)[0].setIcon(new ImageIcon("basex.png"));
			for(int i = 0 ; i < 10 ; i++) {
				for(int j = 0 ; j < 10 ; j++) {
					if( i != 0 || j != 0) {
						grid.get(i)[j] = new JButton();
						grid.get(i)[j].setFocusable(false);
						grid.get(i)[j].setOpaque(false);
						grid.get(i)[j].setContentAreaFilled(false);
						grid.get(i)[j].setBorderPainted(false);
						grid.get(i)[j].setName("");
						pnlGrid.add(grid.get(i)[j]);
					}
				}
			}
			pnlGrid.setBackground(new Color(0, 0, 0, 0));
		}
		
		private void Citmaker() {
			pnlCit1 = new JPanel(new GridLayout(1, 0, 0, 0)) {
				@Override
				  protected void paintComponent(Graphics g) {

				    super.paintComponent(g);
				    Image bgImage;
				    ImageIcon i = new ImageIcon("citpanel1.png");
				    bgImage= i.getImage();
				        g.drawImage(bgImage, 0, 0, this.getSize().width, this.getSize().height, this);
				}
			};
			pnlCit1.setOpaque(false);
			pnlCit1.setBackground(new Color(0, 0, 0, 0));
			scroll4 = new JScrollPane(pnlCit1);
			scroll4.setOpaque(false);
			scroll4.getViewport().setOpaque(false);
			scroll4.setBackground(new Color(0, 0, 0, 0));
			scroll4.getViewport().setBackground(new Color(0, 0, 0, 0));
		}
		
		public JPanel getPnlCit1() {
			return pnlCit1;
		}

		public void setPnlCit1(JPanel pnlCit1) {
			this.pnlCit1 = pnlCit1;
		}

		private void CitGridmaker(){
			pnlCitGrid = new JPanel(new BorderLayout(0, 15));
			Citmaker();
			Gridmaker();
			//pnlCitGrid.add(pnlCit1, BorderLayout.NORTH);
			scroll4.setPreferredSize(new Dimension(pnlGrid.getWidth(), 200));
			pnlCitGrid.add(pnlGrid, BorderLayout.CENTER);
			t3 = new JPanel(new BorderLayout(30,30));
			
			t3.add(pnlCitGrid, BorderLayout.CENTER);
			JLabel j1 = new JLabel(),j2 = new JLabel(), j3 = new JLabel(), j4 = new JLabel(); 
			Color c = new Color(0, 0, 0, 0);
			pnlCitGrid.setBackground(c);
			j1.setBackground(c);
			j2.setBackground(c);
			j3.setBackground(c);
			j4.setBackground(c);
			t3.setBackground(c);
			t3.add(j1, BorderLayout.EAST);
			t3.add(j2, BorderLayout.SOUTH);
			t3.add(j3, BorderLayout.WEST);
			t3.add(j4, BorderLayout.NORTH);
			
		}

		public Clip getClip() {
			return clip;
		}

		public String getStatus() {
			return status;
		}

		public AudioInputStream getAudioInputStream() {
			return audioInputStream;
		}

		public static String getFilePath() {
			return filePath;
		}

		public JPanel getPnlAll() {
			return pnlAll;
		}

		public JPanel getT4() {
			return t4;
		}

		public JScrollPane getScroll1() {
			return scroll1;
		}

		public JScrollPane getScroll2() {
			return scroll2;
		}

		public JScrollPane getScroll3() {
			return scroll3;
		}

		public JScrollPane getScroll4() {
			return scroll4;
		}

		public GridBagConstraints getCo() {
			return co;
		}

		private void Unitsmaker() {
			pnlUnit = new JPanel(new GridLayout(0, 2, 10, 10)) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				  protected void paintComponent(Graphics g) {

				    super.paintComponent(g);
				    Image bgImage;
				    ImageIcon i = new ImageIcon("empt.png");
				    bgImage= i.getImage();
				        g.drawImage(bgImage, 0, 0, this.getSize().width, this.getSize().height, this);
				}
			};
			pnlUnit.setOpaque(false);
			scroll3 = new JScrollPane(pnlUnit);
			scroll3.setPreferredSize(new Dimension(550, 400));
			scroll3.getViewport().setOpaque(false);
			scroll3.getViewport().setBackground(new Color(0, 0, 0, 0));
			scroll3.setOpaque(false);
		}
		
		class ImagePanel extends JComponent {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private Image image;
		    public ImagePanel(ImageIcon ic) {
		        this.image = ic.getImage();
		    }
		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        g.drawImage(image, 0, 0, this);
		    }
		}

		
		
		private void Infomaker(){
			pnlInfo = new JPanel(new BorderLayout()){
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				  protected void paintComponent(Graphics g) {

				    super.paintComponent(g);
				    Image bgImage;
				    ImageIcon i = new ImageIcon("barbar.png");
				    bgImage= i.getImage();
				        g.drawImage(bgImage, 0, 0, this.getSize().width, this.getSize().height, this);
				}
			};
			
			Log= new CustomTextArea();
			Details = new CustomTextArea();
			JPanel j1 = new JPanel(new BorderLayout()){
				@Override
				  protected void paintComponent(Graphics g) {

				    super.paintComponent(g);
				    Image bgImage;
				    ImageIcon i = new ImageIcon("textpanel2.png");
				    bgImage= i.getImage();
				        g.drawImage(bgImage, 0, 0, this.getSize().width, this.getSize().height, this);
				}
			};
			JPanel j2 = new JPanel(new BorderLayout()){
				@Override
				  protected void paintComponent(Graphics g) {

				    super.paintComponent(g);
				    Image bgImage;
				    ImageIcon i = new ImageIcon("textpanel2.png");
				    bgImage= i.getImage();
				        g.drawImage(bgImage, 0, 0, this.getSize().width, this.getSize().height, this);
				}
			};
			JPanel j3 = new JPanel(new BorderLayout());
			JPanel j4 = new JPanel(new BorderLayout());
			Border border1 = BorderFactory.createTitledBorder("Log");
			Border border2 = BorderFactory.createTitledBorder("Info");
			scroll1 = new JScrollPane(Log);
			scroll2 = new JScrollPane(Details);
			j3.add(j1, BorderLayout.CENTER);
			j4.add(j2, BorderLayout.CENTER);
			j4.setForeground(Color.GREEN);
			j3.setForeground(Color.GREEN);
			j3.setOpaque(false);
			j4.setOpaque(false);
			j3.setBorder(border1);
			j4.setBorder(border2);
			text = new JPanel(new GridLayout(0, 1, 20, 20));
			text.add(j3);
			text.add(j4);
			scroll1.setOpaque(false);
			scroll2.setOpaque(false);
			scroll1.getViewport().setOpaque(false);
			scroll2.getViewport().setOpaque(false);
			scroll1.getViewport().setBackground(new Color(0, 0, 0, 0));
			scroll2.getViewport().setBackground(new Color(0, 0, 0, 0));
			j1.add(scroll1, BorderLayout.CENTER);
			j2.add(scroll2, BorderLayout.CENTER);
			Log.setForeground(Color.GREEN);
			Details.setForeground(Color.GREEN);
			Log.setOpaque(false);
			Details.setOpaque(false);
			scroll1.setBackground(new Color(0, 0, 0, 0));
			scroll2.setBackground(new Color(0, 0, 0, 0));
			Log.setBackground(new Color(0, 0, 0, 0));
			Details.setBackground(new Color(0, 0, 0, 0));
			Log.setEditable(false);
			Details.setEditable(false);
			text.setBackground(new Color(0, 0, 0, 0));
			pnlInfo.add(text, BorderLayout.CENTER);
			btns.add(new JButton("Next Cycle"));
			//pnlInfo.add(btns.get(btns.size()-1), BorderLayout.NORTH);
			btns.get(btns.size()-1).setForeground(new Color(192,192,192));
			btns.get(btns.size()-1).setIcon(new ImageIcon("button play.png"));
			btns.get(btns.size()-1).setFocusable(false);
			btns.get(btns.size()-1).setOpaque(false);
			btns.get(btns.size()-1).setName("Cycle");
			btns.get(btns.size()-1).setContentAreaFilled(false);
			btns.get(btns.size()-1).setBorderPainted(false);
			pnlInfo.setBackground(new Color(0, 0, 0, 0));
		}
		public class CustomTextArea extends JTextArea {

	        /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private BufferedImage image;

	        public CustomTextArea() {
	            super(20, 20);
	            try {
	                image = ImageIO.read(new File("empt.png"));
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }

	        @Override
	        public boolean isOpaque() {
	            return false;
	        }

	        @Override
	        protected void paintComponent(Graphics g) {
	            Graphics2D g2d = (Graphics2D) g.create();
	            g2d.setColor(getBackground());
	            g2d.fillRect(0, 0, getWidth(), getHeight());
	            if (image != null) {
	                int x = getWidth() - image.getWidth();
	                int y = getHeight() - image.getHeight();
	                g2d.drawImage(image, x, y, this);    
	            }
	            super.paintComponent(g2d);
	            g2d.dispose();
	        }
		}
		private void InfUnitmaker() {
			t4 = new JPanel(new BorderLayout(20,20)) {
				@Override
				  protected void paintComponent(Graphics g) {

				    super.paintComponent(g);
				    Image bgImage;
				    ImageIcon i = new ImageIcon("bar.png");
				    bgImage= i.getImage();
				        g.drawImage(bgImage, 0, 0, this.getSize().width, this.getSize().height, this);
				}
			};
			pnlInfUnit = new JPanel(new BorderLayout(20, 20));
			JLabel j =  new JLabel();
			Infomaker();
			Unitsmaker();
			Citmaker();
			j.setBackground(new Color(0, 0, 0, 0));
			pnlInfUnit.setOpaque(false);
			pnlInfUnit.add(pnlInfo, BorderLayout.CENTER);
			pnlInfUnit.add(scroll3, BorderLayout.NORTH);
			pnlInfUnit.setBackground(new Color(0, 0, 0, 0));
			t4.add(pnlInfUnit, BorderLayout.CENTER);
			t4.add(j, BorderLayout.WEST);
			t4.setOpaque(false);
		}

		public JPanel getPnlInfUnit() {
			return pnlInfUnit;
		}

		public void setPnlInfUnit(JPanel pnlInfUnit) {
			this.pnlInfUnit = pnlInfUnit;
		}

		public JPanel getPnlCitGrid() {
			return pnlCitGrid;
		}

		public void setPnlCitGrid(JPanel pnlCitGrid) {
			this.pnlCitGrid = pnlCitGrid;
		}

		public JPanel getPnlGrid() {
			return pnlGrid;
		}

		public void setPnlGrid(JPanel pnlGrid) {
			this.pnlGrid = pnlGrid;
		}

		public JPanel getPnlInfo() {
			return pnlInfo;
		}

		public void setPnlInfo(JPanel pnlInfo) {
			this.pnlInfo = pnlInfo;
		}

		public JPanel getPnlUnit() {
			return pnlUnit;
		}

		public void setPnlUnit(JPanel pnlUnit) {
			this.pnlUnit = pnlUnit;
		}

		public JPanel getText() {
			return text;
		}

		public void setText(JPanel text) {
			this.text = text;
		}

		public JPanel getT3() {
			return t3;
		}

		public void setT3(JPanel t3) {
			this.t3 = t3;
		}

		public JLabel getBckgnd() {
			return bckgnd;
		}

		public void setBckgnd(JLabel bckgnd) {
			this.bckgnd = bckgnd;
		}

		public CustomTextArea getLog() {
			return Log;
		}

		public void setLog(CustomTextArea log) {
			Log = log;
		}

		public CustomTextArea getDetails() {
			return Details;
		}

		public void setDetails(CustomTextArea details) {
			Details = details;
		}

		public ArrayList<JButton[]> getGrid() {
			return grid;
		}

		public void setGrid(ArrayList<JButton[]> grid) {
			this.grid = grid;
		}

		public ArrayList<JButton> getBtns() {
			return btns;
		}

		public void setBtns(ArrayList<JButton> btns) {
			this.btns = btns;
		}

		public ArrayList<ArrayList<JButton>> getCit() {
			return cit;
		}

		public void setCit(ArrayList<ArrayList<JButton>> cit) {
			this.cit = cit;
		}

		public ArrayList<JButton> getUnits() {
			return units;
		}

		public void setUnits(ArrayList<JButton> units) {
			this.units = units;
		}

		public JPanel getT5() {
			return t5;
		}

		public void setT5(JPanel t5) {
			this.t5 = t5;
		}
	}
