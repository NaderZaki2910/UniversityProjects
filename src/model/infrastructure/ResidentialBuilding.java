package model.infrastructure;

import java.util.ArrayList;

import model.disasters.Collapse;
import model.disasters.Disaster;
import model.events.CollapseListener;
import model.events.IntroubleListener;
import model.events.SOSListener;
import model.people.Citizen;
import model.units.Unit;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulatable;

public class ResidentialBuilding implements Rescuable, Simulatable
{

	private Address location;
	private int structuralIntegrity;
	private int fireDamage;
	private int gasLevel;
	private int foundationDamage;
	private ArrayList<Citizen> occupants;
	private ArrayList<Unit> curUnits;
	private Disaster disaster;
	public boolean doit = false;
	private IntroubleListener emergency;
	private SOSListener emergencyService;
	private CollapseListener changer;
	public ResidentialBuilding(Address location) {
		this.location = location;
		this.structuralIntegrity=100;
		occupants= new ArrayList<Citizen>();
		curUnits = new ArrayList<>();
	}
	public int getStructuralIntegrity() {
		return structuralIntegrity;
	}
	public void setStructuralIntegrity(int structuralIntegrity) {
		this.structuralIntegrity = structuralIntegrity;
		if(structuralIntegrity<=0)
		{
			this.structuralIntegrity=0;
			for(int i = 0 ; i< occupants.size(); i++)
				occupants.get(i).setHp(0);
		}
	}
	public int getFireDamage() {
		return fireDamage;
	}
	public void setFireDamage(int fireDamage) {
		this.fireDamage = fireDamage;
		if(fireDamage<=0)
			this.fireDamage=0;
		else if(fireDamage>=100)
			this.fireDamage=100;
	}
	public int getGasLevel() {
		return gasLevel;
	}
	public void setGasLevel(int gasLevel) {
		this.gasLevel = gasLevel;
		if(this.gasLevel<=0)
			this.gasLevel=0;
		else if(this.gasLevel>=100)
		{
			this.gasLevel=100;
			for(int i = 0 ; i < occupants.size(); i++)
			{
				occupants.get(i).setHp(0);
			}
		}
	}
	public int getFoundationDamage() {
		return foundationDamage;
	}
	public void setFoundationDamage(int foundationDamage) {
		this.foundationDamage = foundationDamage;
		if(this.foundationDamage>=100)
		{
			setStructuralIntegrity(0);
			changer.ChangeIcon(this);
		}
			
	}
	public Address getLocation() {
		return location;
	}
	public ArrayList<Citizen> getOccupants() {
		return occupants;
	}
	public Disaster getDisaster() {
		return disaster;
	}
	public void setChanger(CollapseListener changer) {
		this.changer = changer;
	}
	public void setEmergencyService(SOSListener emergency) {
		this.emergencyService = emergency;
	}
	public void setEmergency(IntroubleListener emergency) {
		this.emergency = emergency;
	}
	@Override
	public void cycleStep() {
	
		if(foundationDamage>0)
		{
			
			int damage= (int)((Math.random()*6)+5);
			setStructuralIntegrity(structuralIntegrity-damage);
			
		}
		if(fireDamage>0 &&fireDamage<30)
			setStructuralIntegrity(structuralIntegrity-3);
		else if(fireDamage>=30 &&fireDamage<70)
			setStructuralIntegrity(structuralIntegrity-5);
		else if(fireDamage>=70)
			setStructuralIntegrity(structuralIntegrity-7);
		
	}
	public String getStatus() {
		String x = "Location: "+ this.location.getX() + ","+ this.location.getY() + "\n"
				+ "Structural Integrity: " + this.structuralIntegrity + "\n" 
				+ "Fire Damage: " + this.fireDamage + "\n"  + "\n" 
				+ "Gas Level: " + this.gasLevel + "\n"  + "\n" 
				+ "Foundation Damage: " + this.foundationDamage + "\n" + "\n" 
				+ "No. of Occupants: " + this.occupants.size() + "\n" + "\n" ;
				if(this.disaster != null) {
					x = x + "Disaster Affecting: "+ this.disaster.getType()+"\n" + "\n" ;
					if(this.disaster.isActive())
						x = x + "Current Disaster State: Active"+"\n"+"	Occupants: "+"\n" + "\n" ;
					else
						x = x + "Current Disaster State: Not Active"+"\n" + "\n" +"	Occupants: "+"\n" + "\n" ;
				}
				else {
					x = x + "Disaster Affecting: Null"+"\n" + "\n" + "	Occupants: "+"\n" + "\n" ;
				}
				
		if(occupants.size() == 0 ) {
			x = x+"EMPTY."+"\n" + "\n" ;
		}
		for(int i = 0 ; i < occupants.size() ; i++) {
			x = x + occupants.get(i).getStatus();
		}
		x = x + "Units Treating There: "+"\n" + "\n" ;
		if(curUnits.size() == 0) {
			x = x + "No Units"+"\n" + "\n" ;
		}
		for(int i = 0 ; i < curUnits.size() ; i++) {
			x = x + curUnits.get(i).getStatus();
		}
		return x;
	}
	@Override
	public void struckBy(Disaster d) {
		if(disaster!=null)
			disaster.setActive(false);
		disaster=d;
		emergencyService.receiveSOSCall(this);
		if(doit)
			emergency.inTrouble(disaster, this);
	}
	public ArrayList<Unit> getCurUnits() {
		return curUnits;
	}
	
}
