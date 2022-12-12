package model.people;

import simulation.Address;
import simulation.Rescuable;
import simulation.Simulatable;
import model.disasters.Disaster;
import model.events.DeathListener;
import model.events.IntroubleListener;
import model.events.SOSListener;
import model.events.WorldListener;
import model.units.Unit;

public class Citizen implements Rescuable,Simulatable{
	private CitizenState state;
	private Disaster disaster;
	private String name;
	private String nationalID;
	private int age;
	static int i = 0;
	private int hp;
	private int bloodLoss;
	private int toxicity;
	private Address location;
	private Unit healer;
	public boolean doit = false;
	private IntroubleListener emergency;
	private SOSListener emergencyService;
	private DeathListener deather;
	private WorldListener worldListener;
	public Citizen(Address location,String nationalID, String name, int age
			,WorldListener worldListener) {
		this.name = name;
		this.nationalID = nationalID;
		this.age = age;
		this.location = location;
		this.state=CitizenState.SAFE;
		this.hp=100;
		this.worldListener = worldListener;
	}
	
	public void setHealer(Unit healer) {
		this.healer = healer;
	}

	public void setDeather(DeathListener deather) {
		this.deather = deather;
	}

	public WorldListener getWorldListener() {
		return worldListener;
	}

	public void setWorldListener(WorldListener listener) {
		this.worldListener = listener;
	}

	public CitizenState getState() {
		return state;
	}
	public void setState(CitizenState state) {
		this.state = state;
	}
	public String getName() {
		return name;
	}
	public int getAge() {
		return age;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
		if(this.hp>=100)
			this.hp=100;
		else if(this.hp<=0 && this.getState() != CitizenState.DECEASED){
			this.hp = 0;
			state=CitizenState.DECEASED;
			deather.changeIcon(this);
		}
	}
	public int getBloodLoss() {
		return bloodLoss;
	}
	public void setBloodLoss(int bloodLoss) {
		this.bloodLoss = bloodLoss;
		if(bloodLoss<=0)
			this.bloodLoss=0;
		else if(bloodLoss>=100)
		{
			this.bloodLoss=100;
			if(this.getState() != CitizenState.DECEASED)
			setHp(0);
		}
	}
	public int getToxicity() {
		return toxicity;
	}
	public void setToxicity(int toxicity) {
		this.toxicity = toxicity;
		if(toxicity>=100)
		{
			this.toxicity=100;
			if(this.getState() != CitizenState.DECEASED)
			setHp(0);
		}
		else if(this.toxicity<=0)
			this.toxicity=0;
	}
	public Address getLocation() {
		return location;
	}
	public void setLocation(Address location) {
		this.location = location;
	}
	public Disaster getDisaster() {
		return disaster;
	}
	public String getNationalID() {
		return nationalID;
	}
	public void setEmergency(IntroubleListener emergency) {
		this.emergency = emergency;
	}
	public void setEmergencyService(SOSListener emergency) {
		this.emergencyService = emergency;
	}
	@Override
	public void cycleStep() {
		if(bloodLoss>0 && bloodLoss<30)
			if(this.getState() != CitizenState.DECEASED)
			setHp(hp-5);
		else if(bloodLoss>=30 && bloodLoss<70)
			if(this.getState() != CitizenState.DECEASED)
			setHp(hp-10);
		else if(bloodLoss >=70)
			if(this.getState() != CitizenState.DECEASED)
			setHp(hp-15);
		if (toxicity >0 && toxicity < 30)
			if(this.getState() != CitizenState.DECEASED)
			setHp(hp-5);
		else if(toxicity>=30 &&toxicity<70)
			if(this.getState() != CitizenState.DECEASED)
			setHp(hp-10);
		else if(toxicity>=70)
			if(this.getState() != CitizenState.DECEASED)
			setHp(hp-15);
	}
	@Override
	public void struckBy(Disaster d) {
		if(disaster!=null)
			disaster.setActive(false);
		disaster=d;
		state= CitizenState.IN_TROUBLE;
		emergencyService.receiveSOSCall(this);
		if(doit)
		emergency.inTrouble(disaster, this);
	}
	
	public String getStatus() {
		String x = "Name: "+ this.name+"\n" + "\n" 
				+"Age: "+ this.age+"\n" + "\n" 
				+"State: "+ this.state+"\n" + "\n" 
				+"National Id: "+ this.nationalID+"\n" + "\n" 
				+"Location: (" +this.location.getX()+","+this.location.getY()+")"+"\n" + "\n" 
				+"Hp: "+ this.hp+"\n" + "\n" 
				+"Bloodloss: "+this.bloodLoss+"\n" + "\n" 
				+"Toxicity: "+this.toxicity+"\n" + "\n" ;
		if(this.disaster != null) {
			x = x +"Current Disaster: "+this.disaster.getType()+"\n" + "\n" ;
			x = x +"Current Disaster State: "+this.disaster.isActive()+"\n" + "\n" ;
		}
		else {
			x = x +"Current Disaster: Null"+"\n" + "\n" ;
		}
		if(this.healer != null) {
			x = x +"Unit Treating: "+"\n" + "\n" ;
			x = x +this.healer.getStatus();
		}
		else {
			x = x +"Unit Treating: "+"\n" + "\n" ;
			x = x +"No Unit Treating"+"\n" + "\n" ;
		}
		return x;
	}

	public Unit getHealer() {
		return healer;
	}
	
	
}
