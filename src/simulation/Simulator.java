package simulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CitizenAlreadyDeadException;
import model.disasters.Collapse;
import model.disasters.Disaster;
import model.disasters.Fire;
import model.disasters.GasLeak;
import model.disasters.Infection;
import model.disasters.Injury;
import model.events.ChangeListener;
import model.events.CollapseListener;
import model.events.DeathListener;
import model.events.DisasterListener;
import model.events.EvolutionListener;
import model.events.IntroubleListener;
import model.events.SOSListener;
import model.events.UnitArrivedListener;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import model.units.Ambulance;
import model.units.DiseaseControlUnit;
import model.units.Evacuator;
import model.units.FireTruck;
import model.units.GasControlUnit;
import model.units.PoliceUnit;
import model.units.Unit;
import model.units.UnitState;

public class Simulator implements WorldListener {
	private int currentCycle;
	private ArrayList<ResidentialBuilding> buildings;
	private ArrayList<Citizen> citizens;
	private ArrayList<Unit> emergencyUnits;
	private ArrayList<Disaster> plannedDisasters;
	private ArrayList<Disaster> executedDisasters;
	private Address[][] world;
	private SOSListener emergencyService;
	private ChangeListener AddChanger;
	private EvolutionListener evo;
	private CollapseListener changer;
	private UnitArrivedListener arrived;
	private IntroubleListener emergency;
	private DeathListener deather;
	private DisasterListener Dlistener;
	private String Log = "";

	public Simulator(SOSListener l, CollapseListener c, DeathListener d, EvolutionListener ev, IntroubleListener trouble, ChangeListener AddChanger, UnitArrivedListener arrived, DisasterListener Dlistener) throws Exception {
		emergencyService = l;
		changer = c;
		deather = d;
		evo = ev;
		this.Dlistener = Dlistener;
		this.arrived = arrived;
		this.AddChanger = AddChanger;
		emergency = trouble;
		buildings = new ArrayList<ResidentialBuilding>();
		citizens = new ArrayList<Citizen>();
		emergencyUnits = new ArrayList<Unit>();
		plannedDisasters = new ArrayList<Disaster>();
		executedDisasters = new ArrayList<Disaster>();

		world = new Address[10][10];
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 10; j++)
				world[i][j] = new Address(i, j);

		loadUnits("units.csv");
		loadBuildings("buildings.csv");
		loadCitizens("citizens.csv");
		loadDisasters("disasters.csv");
		for (int i = 0; i < buildings.size(); i++) {
			ResidentialBuilding building = buildings.get(i);
			building.doit = true;
			for (int j = 0; j < citizens.size(); j++) {
				Citizen citizen = citizens.get(j);
				citizen.doit = true;
				if (citizen.getLocation() == building.getLocation())
					building.getOccupants().add(citizen);
			}
		}
	}
	public Simulator(SOSListener l) throws Exception {
		emergencyService = l;
		buildings = new ArrayList<ResidentialBuilding>();
		citizens = new ArrayList<Citizen>();
		emergencyUnits = new ArrayList<Unit>();
		plannedDisasters = new ArrayList<Disaster>();
		executedDisasters = new ArrayList<Disaster>();

		world = new Address[10][10];
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 10; j++)
				world[i][j] = new Address(i, j);

		loadUnits("units.csv");
		loadBuildings("buildings.csv");
		loadCitizens("citizens.csv");
		loadDisasters("disasters.csv");
		for (int i = 0; i < buildings.size(); i++) {
			ResidentialBuilding building = buildings.get(i);
			for (int j = 0; j < citizens.size(); j++) {
				Citizen citizen = citizens.get(j);
				if (citizen.getLocation() == building.getLocation())
					building.getOccupants().add(citizen);
			}
		}
	}

	public ArrayList<ResidentialBuilding> Locations(){
		return buildings;
	}
	
	private void loadUnits(String path) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();
		while (line != null) {
			String[] info = line.split(",");
			String id = info[1];
			int steps = Integer.parseInt(info[2]);
			switch (info[0]) {
			case "AMB": {
				Ambulance a = new Ambulance(id, world[0][0], steps, this);
				a.setEv(evo);
				a.setArrived(arrived);
				emergencyUnits.add(a);

			}
				break;
			case "DCU": {
				DiseaseControlUnit d = new DiseaseControlUnit(id, world[0][0],
						steps, this);
				d.setEv(evo);
				d.setArrived(arrived);
				emergencyUnits.add(d);
			}
				break;
			case "EVC": {
				Evacuator e = new Evacuator(id, world[0][0], steps, this,
						Integer.parseInt(info[3]));
				e.setEv(evo);
				e.setArrived(arrived);
				emergencyUnits.add(e);
			}
				break;
			case "FTK": {
				FireTruck f = new FireTruck(id, world[0][0], steps, this);
				f.setEv(evo);
				f.setArrived(arrived);
				emergencyUnits.add(f);
			}
				break;
			case "GCU": {
				GasControlUnit g = new GasControlUnit(id, world[0][0], steps,
						this);
				g.setEv(evo);
				g.setArrived(arrived);
				emergencyUnits.add(g);
			}
				break;

			}
			line = br.readLine();
		}
		br.close();

	}

	private void loadBuildings(String path) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();
		while (line != null) {
			String[] info = line.split(",");
			int x = Integer.parseInt(info[0]);
			int y = Integer.parseInt(info[1]);
			ResidentialBuilding b = new ResidentialBuilding(world[x][y]);
			b.setEmergencyService(emergencyService);
			b.setChanger(changer);
			b.setEmergency(emergency);
			buildings.add(b);
			line = br.readLine();
		}
		br.close();
	}

	private void loadCitizens(String path) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();
		while (line != null) {
			String[] info = line.split(",");
			int x = Integer.parseInt(info[0]);
			int y = Integer.parseInt(info[1]);
			String id = info[2];
			String name = info[3];
			int age = Integer.parseInt(info[4]);
			Citizen c = new Citizen(world[x][y], id, name, age, this);
			c.setEmergencyService(emergencyService);
			c.setDeather(deather);
			c.setEmergency(emergency);
			citizens.add(c);
			line = br.readLine();
		}
		br.close();
	}

	private void loadDisasters(String path) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();
		while (line != null) {
			String[] info = line.split(",");
			int startCycle = Integer.parseInt(info[0]);
			ResidentialBuilding building = null;
			Citizen citizen = null;
			if (info.length == 3)
				citizen = getCitizenByID(info[2]);
			else {
				int x = Integer.parseInt(info[2]);
				int y = Integer.parseInt(info[3]);
				building = getBuildingByLocation(world[x][y]);
			}
			switch (info[1]) {
			case "INJ":
				plannedDisasters.add(new Injury(startCycle, citizen));
				break;
			case "INF":
				plannedDisasters.add(new Infection(startCycle, citizen));
				break;
			case "FIR":
				plannedDisasters.add(new Fire(startCycle, building));
				break;
			case "GLK":
				plannedDisasters.add(new GasLeak(startCycle, building));
				break;
			}
			plannedDisasters.get(plannedDisasters.size()-1).setDlistener(Dlistener);
			line = br.readLine();
		}
		br.close();
	}

	private Citizen getCitizenByID(String id) {
		for (int i = 0; i < citizens.size(); i++) {
			if (citizens.get(i).getNationalID().equals(id))
				return citizens.get(i);
		}
		return null;
	}

	private ResidentialBuilding getBuildingByLocation(Address location) {
		for (int i = 0; i < buildings.size(); i++) {
			if (buildings.get(i).getLocation() == location)
				return buildings.get(i);
		}
		return null;
	}

	@Override
	public void assignAddress(Simulatable s, int x, int y) {
		Address ad = new Address(0, 0);
		if (s instanceof Citizen) {
			ad = ((Citizen) s).getLocation();
			((Citizen) s).setLocation(world[x][y]);
			}
		else {
			ad = ((Unit) s).getLocation();
			((Unit) s).setLocation(world[x][y]);
		}
			
		AddChanger.newAddress(ad, s);
	}

	public void setAddChanger(ChangeListener addChanger) {
		AddChanger = addChanger;
	}
	public void setEmergencyService(SOSListener emergency) {
		this.emergencyService = emergency;
	}

	public void nextCycle() throws CitizenAlreadyDeadException, BuildingAlreadyCollapsedException {

		currentCycle++;
		Log = Log + "\n" +"Cycle No. : " + currentCycle;
		for (int i = 0; i < plannedDisasters.size(); i++) {
			Disaster d = plannedDisasters.get(i);
			if (d.getStartCycle() == currentCycle) {
				plannedDisasters.remove(d);
				i--;
				if (d instanceof Fire)
					handleFire(d);
				else if (d instanceof GasLeak)
					handleGas(d);
				else {
					d.strike();
					executedDisasters.add(d);
					Log = Log + "\n" + "	"+ d.getType() + " At (" 
							+ d.getTarget().getLocation().getX() + "," 
							+ d.getTarget().getLocation().getY() + ").";
				}
			}
		}

		for (int i = 0; i < buildings.size(); i++) {
			ResidentialBuilding b = buildings.get(i);
			if (b.getFireDamage() >= 100) {
				b.getDisaster().setActive(false);
				b.setFireDamage(0);
				Collapse c = new Collapse(currentCycle, b);
				c.setDlistener(Dlistener);;
				c.strike();
				executedDisasters.add(c);
				Log = Log + "\n" + "	Collapse Disaster At (" + b.getLocation().getX() + "," + b.getLocation().getY() + ").";
			}
		}

		for (int i = 0; i < emergencyUnits.size(); i++) {
			if(emergencyUnits.get(i) instanceof PoliceUnit) {
				((PoliceUnit)emergencyUnits.get(i)).cycleStep();
			}
			else {
				emergencyUnits.get(i).cycleStep();
			}
		}

		for (int i = 0; i < executedDisasters.size(); i++) {
			Disaster d = executedDisasters.get(i);
			if (d.getStartCycle() < currentCycle && d.isActive())
				d.cycleStep();
		}

		for (int i = 0; i < buildings.size(); i++) {
			buildings.get(i).cycleStep();
		}

		for (int i = 0; i < citizens.size(); i++) {
			citizens.get(i).cycleStep();
		}


	}

	public String getLog() {
		return Log;
	}

	public void setLog(String log) {
		Log = log;
	}

	private void handleGas(Disaster d) throws CitizenAlreadyDeadException, BuildingAlreadyCollapsedException {
		ResidentialBuilding b = (ResidentialBuilding) d.getTarget();
		if (b.getFireDamage() != 0) {
			b.setFireDamage(0);
			Collapse c = new Collapse(currentCycle, b);
			c.setDlistener(Dlistener);
			c.strike();
			executedDisasters.add(c);
			Log = Log + "\n" + "	Collapse Disaster At (" + b.getLocation().getX() + "," + b.getLocation().getY() + ").";
		} else {
			d.strike();
			executedDisasters.add(d);
			Log = Log + "\n" + "	Gas Leak Disaster At (" + b.getLocation().getX() + "," + b.getLocation().getY() + ").";
		}
	}

	private void handleFire(Disaster d) throws CitizenAlreadyDeadException, BuildingAlreadyCollapsedException {
		ResidentialBuilding b = (ResidentialBuilding) d.getTarget();
		if (b.getGasLevel() == 0) {
			d.strike();
			executedDisasters.add(d);
			Log = Log + "\n" + "	Fire Disaster At (" + b.getLocation().getX() + "," + b.getLocation().getY() + ").";
		} else if (b.getGasLevel() < 70) {
			b.setFireDamage(0);
			Collapse c = new Collapse(currentCycle, b);
			c.setDlistener(Dlistener);
			c.strike();
			executedDisasters.add(c);
			Log = Log + "\n" + "	Collapse Disaster At (" + b.getLocation().getX() + "," + b.getLocation().getY() + ").";
		} else {
			b.setStructuralIntegrity(0);
			Log = Log + "\n" + "	Building At (" + b.getLocation().getX() + "," + b.getLocation().getY() + ") Collapsed.";
		}
	}

	public boolean checkGameOver() {

		if (plannedDisasters.size() != 0)
			return false;

		for (int i = 0; i < executedDisasters.size(); i++) {
			if (executedDisasters.get(i).isActive()) {

				Disaster d = executedDisasters.get(i);
				Rescuable r = d.getTarget();
				if (r instanceof Citizen) {
					Citizen c = (Citizen) r;
					if (c.getState() != CitizenState.DECEASED)
						return false;
				} else {

					ResidentialBuilding b = (ResidentialBuilding) r;
					if (b.getStructuralIntegrity() != 0)
						return false;
				}

			}

		}

		for (int i = 0; i < emergencyUnits.size(); i++) {
			if (emergencyUnits.get(i).getState() != UnitState.IDLE)
				return false;
		}
		return true;
	}

	public int calculateCasualties() {
		int count = 0;
		for (int i = 0; i < citizens.size(); i++) {
			if (citizens.get(i).getState() == CitizenState.DECEASED)
				count++;
		}
		return count;

	}
	
	public ArrayList<Citizen> sendCitizens(){
		return this.citizens;
	}

	public ArrayList<Unit> getEmergencyUnits() {

		return emergencyUnits;
	}

}
