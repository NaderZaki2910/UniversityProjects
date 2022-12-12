package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.xml.soap.Detail;

import Audio.AudioPlayer;
import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CannotTreatException;
import exceptions.CitizenAlreadyDeadException;
import exceptions.IncompatibleTargetException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import model.disasters.Collapse;
import model.disasters.Disaster;
import model.disasters.Fire;
import model.disasters.Injury;
import model.events.ChangeListener;
import model.events.CollapseListener;
import model.events.DeathListener;
import model.events.DisasterListener;
import model.events.EvolutionListener;
import model.events.IntroubleListener;
import model.events.SOSListener;
import model.events.UnitArrivedListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.units.Ambulance;
import model.units.DiseaseControlUnit;
import model.units.Evacuator;
import model.units.GasControlUnit;
import model.units.Unit;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulatable;
import simulation.Simulator;
import view.EndScreen;
import view.GameScreen;
import view.Icons;
import view.MainScreen;
import view.SimulationView;

public class CommandCenter implements SOSListener,CollapseListener,DeathListener,EvolutionListener,IntroubleListener
				,ActionListener,ChangeListener,UnitArrivedListener,DisasterListener {

	private static CommandCenter cmd;
	private SimulationView view;
	static int i = 0;
	private Simulator engine;
	private AudioInputStream audioInputStream;
	private AudioInputStream audioInputStream1;
	private Clip clip = null;
	private Clip clip1 = null;
	private ArrayList<ResidentialBuilding> visibleBuildings;
	private ArrayList<Citizen> visibleCitizens;
	private ArrayList<Citizen> c = new ArrayList<>();
	private ArrayList<JButton[]> gridbtn = new ArrayList<>();
	private ResidentialBuilding[][] gridB = new ResidentialBuilding[10][10];
	private ArrayList<ArrayList<ArrayList<JButton>>> gridC = new ArrayList<>();
	private ArrayList<JToggleButton> units = new ArrayList<>();
	private JToggleButton toggled = new JToggleButton();
	private ArrayList<JButton> btns = new ArrayList<>();
	private Icons ic = new Icons(); 
	
	
 	@SuppressWarnings("unused")
	private ArrayList<Unit> emergencyUnits;

	public CommandCenter() throws Exception {
		GameScreen gm = new GameScreen();
		MainScreen mn = new MainScreen();
		EndScreen en =  new EndScreen();
		view = new SimulationView(gm, mn, en);
		view.getBtns().add(mn.btn);
		playMusic("title.wav");
		engine = new Simulator(this, this, this, this, this, this, this, this);
		visibleBuildings = new ArrayList<ResidentialBuilding>();
		visibleCitizens = new ArrayList<Citizen>();
		emergencyUnits = engine.getEmergencyUnits();
		gridbtn = view.getGrid();
		c = engine.sendCitizens();
		ArrayList<ResidentialBuilding> b = engine.Locations();
		btns = view.getBtns();
		for(int i = 0 ; i < btns.size() ; i++) {
			btns.get(i).addActionListener(this);
		}
		gridbtn.get(0)[0].addActionListener(this);
		for(int i = 0 ; i < b.size() ; i++) {
			int x = b.get(i).getLocation().getX();
			int y = b.get(i).getLocation().getY();
			gridB[x][y] = b.get(i);
			gridbtn.get(x)[y].setIcon(randIcB());
			gridbtn.get(x)[y].setName("grid");
			gridbtn.get(x)[y].addActionListener(this);
		}
		for(int i = 0 ; i < 10 ; i++) {
			gridC.add(new ArrayList<>());
			for(int j = 0 ; j < 10 ; j++) {
				gridC.get(i).add(new ArrayList<>());
			}
		}
		for(int i = 0 ; i < c.size() ; i++) {
			//System.out.println(c.get(i).getName());
			int x = c.get(i).getLocation().getX();
			int y = c.get(i).getLocation().getY();
			if(gridB[x][y] == null) {
				gridC.get(x).get(y).add(gridbtn.get(x)[y]);
				gridC.get(x).get(y).get(gridC.get(x).get(y).size()-1).setIcon(randIcCSmall());
			}
			else {
				gridC.get(x).get(y).add(new JButton(c.get(i).getName()));
				view.getPnlCit1().add(gridC.get(x).get(y).get(gridC.get(x).get(y).size()-1));
				gridC.get(x).get(y).get(gridC.get(x).get(y).size()-1).setIcon(randIcCBig());
			}
			gridC.get(x).get(y).get(gridC.get(x).get(y).size()-1).setOpaque(false);
			gridC.get(x).get(y).get(gridC.get(x).get(y).size()-1).setBorderPainted(false);
			gridC.get(x).get(y).get(gridC.get(x).get(y).size()-1).setContentAreaFilled(false);
			gridC.get(x).get(y).get(gridC.get(x).get(y).size()-1).setName(c.get(i).getName());
			gridC.get(x).get(y).get(gridC.get(x).get(y).size()-1).addActionListener(this);
			gridC.get(x).get(y).get(gridC.get(x).get(y).size()-1).setForeground(new Color(40,40,40));
			
		}
		for(int i = 0 ;  i < emergencyUnits.size() ; i++) {
			if(emergencyUnits.get(i) instanceof Evacuator) {
				units.add(new JToggleButton("Evacuator"));
				view.getPnlUnit().add(units.get(i));
				units.get(i).setIcon(ic.getIcEvaIdle().get(0));
			}
			else {
				if(emergencyUnits.get(i) instanceof Ambulance) {
					units.add(new JToggleButton("Ambulance"));
					view.getPnlUnit().add(units.get(i));
					units.get(i).setIcon(ic.getIcInjIdle().get(0));
				}
				else {
					if(emergencyUnits.get(i) instanceof DiseaseControlUnit) {
						units.add(new JToggleButton("Disease Control Unit"));
						view.getPnlUnit().add(units.get(i));
						units.get(i).setIcon(ic.getIcInfIdle().get(0));
					}
					else {
						if(emergencyUnits.get(i) instanceof GasControlUnit) {
							units.add(new JToggleButton("Gas Control Unit"));
							view.getPnlUnit().add(units.get(i));
							units.get(i).setIcon(ic.getIcGasIdle().get(0));
						}
						else {
							units.add(new JToggleButton("Fire Truck"));
							view.getPnlUnit().add(units.get(i));
							units.get(i).setIcon(ic.getIcFirIdle().get(0));
						}
					}
				}
			}
			units.get(i).setOpaque(false);
			units.get(i).setContentAreaFilled(false);
			units.get(i).setBorderPainted(false);
			units.get(i).setFocusable(false);
			units.get(i).setForeground(new Color(40,40,40));
			units.get(i).addActionListener(this);
		}
		view.invalidate();
		view.validate();
		view.repaint();
	}

	private ImageIcon randIcB() {
		return ic.getIcB2().get(0);
	}
	
	private ImageIcon randIcCSmall() {
		Random r = new Random();
		return ic.getIcSmall().get(r.nextInt(14));
	}
	private ImageIcon randIcCBig() {
		Random r = new Random();
		return ic.getIcBig().get(r.nextInt(14));
	}
	@Override
	public void receiveSOSCall(Rescuable r) {
		
		if (r instanceof ResidentialBuilding) {
			
			if (!visibleBuildings.contains(r))
				visibleBuildings.add((ResidentialBuilding) r);
			
		} else {
			
			if (!visibleCitizens.contains(r))
				visibleCitizens.add((Citizen) r);
		}

	}

	@Override
	public void changeIcon(Citizen c) {
		int x = c.getLocation().getX();
		int y = c.getLocation().getY();
		
		ImageIcon im = new ImageIcon();
		for(int i = 0 ; i < gridC.get(x).get(y).size() ; i++) {
			if(gridC.get(x).get(y).get(i) != null) {
				if(gridC.get(x).get(y).get(i).getName().equals(c.getName())) {
					if(gridB[x][y] != null ) {
						gridC.get(x).get(y).get(i).setIcon(iconChanger('b'));
					}
					else {
						gridC.get(x).get(y).get(i).setIcon(iconChanger('s'));
					}
					
				}
			}
		}
		view.invalidate();
		view.validate();
		view.repaint();
	}
	
	private ImageIcon iconChanger(char t) {
		Random r = new Random();
		//System.out.println(i++);
		final int c = r.nextInt(2)+14;
		switch(c) {
			
			case(14):
				if(t == 's') {
					return ic.getIcSmall().get(14);
				}
				else {
					return ic.getIcBig().get(14);
				}
			case(15):
				if(t == 's') {
					return ic.getIcSmall().get(15);
				}
				else {
					return ic.getIcBig().get(15);
				}
			default:
				return ic.getIcSmall().get(14);
		}
	}
	
	@Override
	public void ChangeIcon(ResidentialBuilding b) {
		int x = b.getLocation().getX();
		int y = b.getLocation().getY();
		gridbtn.get(x)[y].setIcon(ic.getIcB2().get(4));
		view.invalidate();
		view.validate();
		view.repaint();
	}
	
	public static void main(String[] args) throws Exception {
		cmd = new CommandCenter();
	}

	@Override
	public void evolve(Unit u, int ev) {
		if(u instanceof Evacuator) {
			units.get(emergencyUnits.indexOf(u)).setIcon(ic.getIcEvaIdle().get(ev));
		}
		else {
			if(u instanceof Ambulance) {
				units.get(emergencyUnits.indexOf(u)).setIcon(ic.getIcInjIdle().get(ev));
			}
			else {
				if(u instanceof DiseaseControlUnit) {
					units.get(emergencyUnits.indexOf(u)).setIcon(ic.getIcInfIdle().get(ev));
				}
				else {
					if(u instanceof GasControlUnit) {
						units.get(emergencyUnits.indexOf(u)).setIcon(ic.getIcGasIdle().get(ev));
					}
					else {
						units.get(emergencyUnits.indexOf(u)).setIcon(ic.getIcFirIdle().get(ev));
					}
				}
			}
		}
		view.invalidate();
		view.validate();
		view.repaint();
	}

	@Override
	public void inTrouble(Disaster d, Rescuable r) {
		ImageIcon im = new ImageIcon();
		if(r instanceof Citizen) {
			if(d instanceof Injury) {
				int x = r.getLocation().getX();
				int y = r.getLocation().getY();
				for(int i = 0 ; i < gridC.get(x).get(y).size(); i++) {
					if(gridC.get(x).get(y).get(i) != null) {
						if(gridC.get(x).get(y).get(i).getName().equals(((Citizen) r).getName())) {
							if(gridB[x][y] == null) {
								im = ic.getIcSmallInj().get(ic.getIcSmall().indexOf((ImageIcon)gridC.get(x).get(y).get(i).getIcon()));
							}
							else {
								im = ic.getIcBigInj().get(ic.getIcBig().indexOf((ImageIcon)gridC.get(x).get(y).get(i).getIcon()));
							}
							gridC.get(x).get(y).get(i).setIcon(im);
						}
					}
				}
			}
			else {
				int x = r.getLocation().getX();
				int y = r.getLocation().getY();
				for(int i = 0 ; i < gridC.get(x).get(y).size() ; i++) {
					if(gridC.get(x).get(y).get(i) != null) {
						if(gridC.get(x).get(y).get(i).getName().equals(((Citizen) r).getName())) {
							if(gridB[x][y] == null) {
								im = ic.getIcSmallInf().get(ic.getIcSmall().indexOf((ImageIcon)gridC.get(x).get(y).get(i).getIcon()));
							}
							else {
								im = ic.getIcBigInf().get(ic.getIcBig().indexOf((ImageIcon)gridC.get(x).get(y).get(i).getIcon()));
							}
							gridC.get(x).get(y).get(i).setIcon(im);
						}
					}
				}
			}
		}
		else {
			if(d instanceof Collapse) {
				int x = r.getLocation().getX();
				int y = r.getLocation().getY();
				changeiconb(gridB[x][y], null, true, false);
			}
			else {
				if(d instanceof Fire) {
					int x = r.getLocation().getX();
					int y = r.getLocation().getY();
					changeiconb(gridB[x][y], null, true, false);
				}
				else {
					int x = r.getLocation().getX();
					int y = r.getLocation().getY();
					changeiconb(gridB[x][y], null, true, false);
				}
			}
		}
		view.invalidate();
		view.validate();
		view.repaint();
	}

	public void playbtn(String file) {
		try {
			if(clip != null && clip.isActive())
			clip.stop();
			audioInputStream = AudioSystem.getAudioInputStream(new File(file).getAbsoluteFile()); 
			clip = AudioSystem.getClip(); 
			clip.open(audioInputStream);
			clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} 
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getSource() instanceof JToggleButton) {
			JToggleButton btn = (JToggleButton) e.getSource();
			if(btn.isSelected()) {
				toggled = btn;
				for(int i = 0 ; i < units.size() ; i++) {
					if(units.get(i) != btn) {
						units.get(i).setSelected(false);
					}
				}
			}
			else {
				toggled = new JToggleButton();
			}
			view.getDetails().setText(emergencyUnits.get(units.indexOf(btn)).getStatus());
			
		}
		else {
			JButton but = (JButton) e.getSource();
			if(but.getName().equals("Cycle") || but.getName().equals("close")||but.getName().equals("Start")) {
				switch(but.getName()) {
					case("Cycle"):
						playbtn("BW2MenuSelect.wav");
						try {
							engine.nextCycle();
							view.getLog().setText(engine.getLog());
						} catch (CitizenAlreadyDeadException e1) {
							view.getLog().setText(engine.getLog() + "\n" + e1.getMessage());
							e1.printStackTrace();
						} catch (BuildingAlreadyCollapsedException e1) {
							view.getLog().setText(engine.getLog() + "\n" + e1.getMessage());
							e1.printStackTrace();
						}
						if(engine.checkGameOver()) {
							view.ChangeScreenEnd("Game Over."+"\n"+"Casualties: "+ engine.calculateCasualties());
						}break;
					case("close"):
						playbtn("BW2CloseMenu.wav");
						view.dispose();
						break;
					case("Start"):
						playbtn("BW2MenuChoose.wav");
						try {
							playMusic("pokemongame.wav");
						} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						view.ChangeScreenGame();
						break;
				}
					
			}
			else {
				if(but.getName().equals("grid")||but.getName().equals("base")) {
					if(but.getName().equals("grid")) {
						int[] loc = new int[2];
						for(int i  = 0 ; i < gridbtn.size() ; i++) {
							for(int j = 0 ; j < gridbtn.get(i).length ; j++) {
								if(gridbtn.get(i)[j] == but) {
									loc[0] = i;
									loc[1] = j;
								}
							}
						}
						view.getDetails().setText(gridB[loc[0]][loc[1]].getStatus());
						view.getPnlCit1().removeAll();
						for(int i = 0 ; i < gridC.get(loc[0]).get(loc[1]).size() ; i++) {
							view.getPnlCit1().add(gridC.get(loc[0]).get(loc[1]).get(i));
						}
						if(gridC.get(loc[0]).get(loc[1]).size() == 0) {
							JLabel j = new JLabel(new ImageIcon("empt.png"));
							j.setOpaque(false);
							view.getPnlCit1().add(j);
						}
						if(toggled.isSelected()) {
							int ind = units.indexOf(toggled);
							try {
								emergencyUnits.get(ind).respond(gridB[loc[0]][loc[1]]);
								switch (emergencyUnits.get(ind).returnType()) {
								case "inj1":
									units.get(ind).setIcon(ic.getIcInj().get(emergencyUnits.get(ind).getEvolver()));
									break;
								case "inf1":
									units.get(ind).setIcon(ic.getIcInf().get(emergencyUnits.get(ind).getEvolver()));
									break;
								case "eva1":
									units.get(ind).setIcon(ic.getIcEva().get(emergencyUnits.get(ind).getEvolver()));
									break;
								case "gas1":
									units.get(ind).setIcon(ic.getIcGas().get(emergencyUnits.get(ind).getEvolver()));
									break;
								case "fir1":
									units.get(ind).setIcon(ic.getIcFir().get(emergencyUnits.get(ind).getEvolver()));
									break;
								}
							} catch (IncompatibleTargetException | CannotTreatException e1) {
								view.getLog().setText(engine.getLog() + "\n" + e1.getMessage());
								//e1.printStackTrace();
							}
						}
					}
					else {
						view.getDetails().setText("");
						JLabel j = new JLabel(new ImageIcon("empt.png"));
						j.setOpaque(false);
						view.getPnlCit1().removeAll();
						view.getPnlCit1().add(j);
						view.getDetails().setText(view.getDetails().getText() + "Citizens Rescued: "+"\n");
						for(int i = 0 ; i < c.size(); i++) {
							//System.out.println(i);
							if(c.get(i).getLocation().getX() == 0 && c.get(i).getLocation().getY() == 0)
							{
								view.getDetails().setText(view.getDetails().getText() + c.get(i).getStatus()+"\n");
							}
						}
						if(c.size() == 0) {
							view.getDetails().setText(view.getDetails().getText() + "Non yet... "+"\n");
						}
						view.getDetails().setText(view.getDetails().getText() + "Units Found: "+"\n");
						//System.out.println(emergencyUnits.get(2).getLocation().getX()+""+emergencyUnits.get(2).getLocation().getY());
						for(int i = 0 ; i < emergencyUnits.size(); i++) {
							if(emergencyUnits.get(i).getLocation().getX() == 0 && emergencyUnits.get(i).getLocation().getY() == 0)
							{
								view.getDetails().setText(view.getDetails().getText() + emergencyUnits.get(i).getStatus()+"\n");
							}
						}
						if(emergencyUnits.size() == 0) {
							view.getDetails().setText(view.getDetails().getText() + "NO Units There "+"\n");
						}
					}
				}
				else {
					int [] loc = new int[3];
					for(int i = 0 ; i < c.size() ; i++) {
						if(c.get(i).getName().equals(but.getName())) {
							view.getDetails().setText(c.get(i).getStatus());
							if(toggled.isSelected()) {
								int ind = units.indexOf(toggled);
								try {
									emergencyUnits.get(ind).respond(c.get(i));
									switch (emergencyUnits.get(ind).returnType()) {
									case "inj1":
										units.get(ind).setIcon(ic.getIcInj().get(emergencyUnits.get(ind).getEvolver()));
										break;
									case "inf1":
										units.get(ind).setIcon(ic.getIcInf().get(emergencyUnits.get(ind).getEvolver()));
										break;
									case "eva1":
										units.get(ind).setIcon(ic.getIcEva().get(emergencyUnits.get(ind).getEvolver()));
										break;
									case "gas1":
										units.get(ind).setIcon(ic.getIcGas().get(emergencyUnits.get(ind).getEvolver()));
										break;
									case "fir1":
										units.get(ind).setIcon(ic.getIcFir().get(emergencyUnits.get(ind).getEvolver()));
										break;
									}
								} catch (IncompatibleTargetException | CannotTreatException e1) {
									view.getLog().setText(engine.getLog() + "\n" + e1.getMessage());
									//e1.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
		view.invalidate();
		view.validate();
		view.repaint();
	}
	
	public void playMusic(String file) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		if(clip1 != null)
			clip1.stop();
		audioInputStream1 = 
				AudioSystem.getAudioInputStream(new File(file).getAbsoluteFile()); 
		
		// create clip reference 
		clip1 = AudioSystem.getClip(); 
		
		// open audioInputStream to the clip 
		clip1.open(audioInputStream1); 
		
		clip1.loop(Clip.LOOP_CONTINUOUSLY); 
	}
	
	
	public void disableAll() {
		for(int i = 0 ; i < gridC.size() ; i++) 
		{
			for(int j = 0 ; j < gridC.get(i).size() ; j++)
			{
				for(int z = 0 ; z < gridC.get(i).get(j).size() ; z++) {
					gridC.get(i).get(j).get(z).setEnabled(false);
				}
			}
		}
		btns.get(0).setEnabled(false);
		for(int i = 0 ; i < gridbtn.size() ; i++) 
		{
			for(int j = 0 ; j < gridbtn.get(i).length ; j++)
			{
				gridbtn.get(i)[j].setEnabled(false);
			}
		}
		for(int i = 0 ; i < units.size() ; i++) 
		{
			units.get(i).setEnabled(false);
		}
		view.getPnlCit1().setEnabled(false);
		view.getPnlGrid().setEnabled(false);
		view.getPnlInfUnit().setEnabled(false);
	}
	@Override
	public void newAddress(Address a, Simulatable s) {
		ArrayList<JButton> temp = new ArrayList<>();
		if(s instanceof Citizen) {
			for(int i = 0 ; i < gridC.get(a.getX()).get(a.getY()).size() ; i++) {
				if(gridC.get(a.getX()).get(a.getY()).get(i).getName().equals(((Citizen) s).getName()))
				{
					temp.add(gridC.get(a.getX()).get(a.getY()).remove(i)) ;
					break;
				}
			}
			int z = temp.size();
			for(int i = 0 ; i < z ; i++) {
				gridC.get(0).get(0).add(temp.remove(0));
					if(ic.getIcBig().indexOf(gridC.get(0).get(0).get(i).getIcon()) > -1)
						gridC.get(0).get(0).get(i).setIcon(ic.getIcSmall().get(ic.getIcBig().indexOf(gridC.get(0).get(0).get(i).getIcon())));
				
			}
		}
	}

	@Override
	public void UnitArrived(Unit u, Rescuable r, Rescuable preR) {
		if(r != null && r instanceof ResidentialBuilding) {
			ResidentialBuilding b = (ResidentialBuilding) r;
			gridB[r.getLocation().getX()][r.getLocation().getY()].getCurUnits().add(u);
			changeiconb(b, u, true, true);
			if(preR != null) {
				gridB[preR.getLocation().getX()][preR.getLocation().getY()].getCurUnits().remove(u);
				if(!preR.getDisaster().isActive())
					changeiconb(gridB[preR.getLocation().getX()][preR.getLocation().getY()], u, false, false);
				else
					changeiconb(gridB[preR.getLocation().getX()][preR.getLocation().getY()], u, true, false);
			}
		}
		else {
			if(r != null && gridB[r.getLocation().getX()][r.getLocation().getY()] != null) {
				gridB[r.getLocation().getX()][r.getLocation().getY()].getCurUnits().add(u);
				c.get(c.indexOf(r)).setHealer(u);
			}
			else {
				if(r != null)
				c.get(c.indexOf(r)).setHealer(u);
			}
			if(preR != null &&gridB[preR.getLocation().getX()][preR.getLocation().getY()] != null) {
				gridB[preR.getLocation().getX()][preR.getLocation().getY()].getCurUnits().remove(u);
				c.get(c.indexOf(preR)).setHealer(null);
			}
			else {
				if(preR != null )
				c.get(c.indexOf(preR)).setHealer(null);
			}
		}
		}

	public void changeiconb(ResidentialBuilding b, Unit u, boolean active, boolean add) {
		boolean inj = false;
		boolean inf = false;
		boolean eva = false;
		boolean gas = false;
		boolean fir = false;
		for(int i = 0 ; i < b.getCurUnits().size() ; i++ ) {
			if(b.getCurUnits().get(i).returnType().equals("inj1")) {
				inj = true;
			}
			else {
				if(b.getCurUnits().get(i).returnType().equals("inf1")) {
					inf = true;
				}
				else {
					if(b.getCurUnits().get(i).returnType().equals("eva1")) {
						eva = true;
					}
					else {
						if(b.getCurUnits().get(i).returnType().equals("gas1")){
							gas = true;
						}
						else {
							if(b.getCurUnits().get(i).returnType().equals("fir1")) {
								fir = true;
							}
						}
					}
				}
			}
		}
		if(b.getStructuralIntegrity() == 0) {
			gridbtn.get(b.getLocation().getX())[b.getLocation().getY()].setIcon(ic.getIcB2().get(4));
		}
		else {
		if(active) {
			if(add) {
				String x = "";
				if(u != null)
					x = "b2"+u.returnType();
				else
					x = x + "b2";
				if(b.getDisaster() instanceof Collapse) {
					if(inj) {
						x=x+"inj1";
					}
					if(inf) {
						x=x+"inf1";
					}
					x=x+"col.png";
					//System.out.println(x+1);
				}
				else {
					
					if(b.getDisaster() instanceof Fire) {
						if(inj) {
							x=x+"inj1";
						}
						if(inf) {
							x=x+"inf1";
						}
						x=x+"burn.png";
					}
					else {
						if(inj) {
							x=x+"inj1";
						}
						if(inf) {
							x=x+"inf1";
						}
						x=x+"gas.png";
					}
				}
				//System.out.println(x+2);
				gridbtn.get(b.getLocation().getX())[b.getLocation().getY()].setIcon(new ImageIcon(x));
			}
			else {
				String x = "";
				if(u != null)
					x = "b2"+u.returnType();
				else
					x = x + "b2";
				if(b.getDisaster() instanceof Collapse) {
					if(inj) {
						x=x+"inj1";
					}
					if(inf) {
						x=x+"inf1";
					}
					x=x+"col.png";
				}
				else {
					if(b.getDisaster() instanceof Fire) {
						if(inj) {
							x=x+"inj1";
						}
						if(inf) {
							x=x+"inf1";
						}
						x=x+"burn.png";
					}
					else {
						if(inj) {
							x=x+"inj1";
						}
						if(inf) {
							x=x+"inf1";
						}
						x=x+"gas.png";
					}
					//System.out.println(x+3);
					gridbtn.get(b.getLocation().getX())[b.getLocation().getY()].setIcon(new ImageIcon(x));
				}
			}
		}
		else {
			String x= "b2";
			if(add) {
				x=x+u.returnType();
				if(inj) {
					x=x+"inj1";
				}
				if(inf) {
					x=x+"inf1";
				}
				x=x+".png";
				//System.out.println(x+4);
				gridbtn.get(b.getLocation().getX())[b.getLocation().getY()].setIcon(new ImageIcon(x));
			}
			else {
				if(eva) {
					x = x+ "eva1";
				}
				if(fir) {
					x=x+"fir1";
				}
				if(gas) {
					x=x+"gas1";
				}
				if(inj) {
					x=x+"inj1";
				}
				if(inf) {
					x=x+"inf1";
				}
				x = x+".png";
				gridbtn.get(b.getLocation().getX())[b.getLocation().getY()].setIcon(new ImageIcon(x));
			}
		}
	}
}
	@Override
	public void ChangeHappened(Rescuable target, boolean active) {
		if(active) {
			if(target instanceof Citizen) {
				Citizen c = (Citizen) target;
				if(c.getDisaster() instanceof Injury) {
					for(int i = 0 ; i < gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).size() ; i++) {
						if(gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).getName().equals(c.getName())) {
							if(gridB[c.getLocation().getX()][c.getLocation().getY()] == null)
								gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).setIcon(ic.getIcBigInj().get(ic.getIcBig().indexOf((ImageIcon)gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).getIcon())));
							else {
								gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).setIcon(ic.getIcSmallInj().get(ic.getIcSmall().indexOf((ImageIcon)gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).getIcon())));
							}
						}
					}
				}
				else {
					for(int i = 0 ; i < gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).size() ; i++) {
						if(gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).getName().equals(c.getName())) {
							if(gridB[c.getLocation().getX()][c.getLocation().getY()] == null)
								gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).setIcon(ic.getIcBigInf().get(ic.getIcBig().indexOf((ImageIcon)gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).getIcon())));
							else {
								gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).setIcon(ic.getIcSmallInf().get(ic.getIcSmall().indexOf((ImageIcon)gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).getIcon())));
							}
						}
					}
				}
			}
			else {
				ResidentialBuilding b = (ResidentialBuilding) target;
				if(b.getDisaster() instanceof Collapse) {
					gridbtn.get(b.getLocation().getX())[b.getLocation().getY()].setIcon(ic.getIcB2().get(3));
				}
				else {
					if(b.getDisaster() instanceof Fire) {
						gridbtn.get(b.getLocation().getX())[b.getLocation().getY()].setIcon(ic.getIcB2().get(1));
					}
					else {
						gridbtn.get(b.getLocation().getX())[b.getLocation().getY()].setIcon(ic.getIcB2().get(2));
					}
				}
			}
		}
		else {
			if(target instanceof Citizen) {
				Citizen c = (Citizen) target;
				if(c.getDisaster() instanceof Injury) {
					for(int i = 0 ; i < gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).size() ; i++) {
						if(gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).getName().equals(c.getName())) {
							if(gridB[c.getLocation().getX()][c.getLocation().getY()] != null)
								gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).setIcon(ic.getIcBig().get(ic.getIcBigInj().indexOf((ImageIcon)gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).getIcon())));
							else {
								gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).setIcon(ic.getIcSmall().get(ic.getIcSmallInj().indexOf((ImageIcon)gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).getIcon())));
							}
						}
					}
				}
				else {
					for(int i = 0 ; i < gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).size() ; i++) {
						if(gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).getName().equals(c.getName())) {
							if(gridB[c.getLocation().getX()][c.getLocation().getY()] != null)
								gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).setIcon(ic.getIcBig().get(ic.getIcBigInf().indexOf((ImageIcon)gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).getIcon())));
							else {
								gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).setIcon(ic.getIcSmall().get(ic.getIcSmallInf().indexOf((ImageIcon)gridC.get(c.getLocation().getX()).get(c.getLocation().getY()).get(i).getIcon())));
							}
						}
					}
				}
			}
			else {
				ResidentialBuilding b = (ResidentialBuilding) target;
				//System.out.println(b.getDisaster().getType());
				if(b.getDisaster().isActive())
					changeiconb(b, null, true, false);
				else
					changeiconb(b, null, false, false);
			}
	}
	}				
}		