package model.units;

import javax.swing.JToggleButton;

import exceptions.* ;
import model.disasters.*;
import model.events.EvolutionListener;
import model.events.SOSResponder;
import model.events.UnitArrivedListener;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulatable;

public abstract class Unit implements Simulatable, SOSResponder {
	private String unitID;
	private UnitState state;
	private Address location;
	private Rescuable target;
	private Rescuable perTarget = null;
	public Rescuable getPerTarget() {
		return perTarget;
	}

	private int distanceToTarget;
	private int stepsPerCycle;
	private EvolutionListener ev;
	private UnitArrivedListener arrived;
	private WorldListener worldListener;
	private int evolver = 0;

	public Unit(String unitID, Address location, int stepsPerCycle,
			WorldListener worldListener) {
		this.unitID = unitID;
		this.location = location;
		this.stepsPerCycle = stepsPerCycle;
		this.state = UnitState.IDLE;
		this.worldListener = worldListener;
	}

	public int getEvolver() {
		return evolver;
	}

	public void setEvolver(int evolver) {
		this.evolver = evolver;
	}

	public EvolutionListener getEv() {
		return ev;
	}

	public UnitArrivedListener getArrived() {
		return arrived;
	}

	public String getStatus() { 
		String x = "";
		if(this instanceof Evacuator) {
			x = "Unit Type: Evacuator"+"\n" + "\n" ;
		}
		else {
			if(this instanceof Ambulance) {
				x = "Unit Type: Ambulance"+"\n" + "\n" ;
			}
			else {
				if(this instanceof DiseaseControlUnit) {
					x = "Unit Type: Disease Control Unit"+"\n" + "\n" ;
				}
				else {
					if(this instanceof GasControlUnit) {
						x = "Unit Type: Gas Control Unit"+"\n" + "\n" ;
					}
					else {
						x = "Unit Type: Fire Truck"+"\n" + "\n" ;
					}
				}
			}
		}
		x = x + "Unit ID: "+this.unitID+"\n" + "\n" 
				+"State: "+ this.state+"\n" + "\n" 
				+"Location: (" +this.location.getX()+","+this.location.getY()+")"+"\n" + "\n" 
				+"Steps Per Cycle: "+ this.stepsPerCycle+"\n" + "\n" 
				+"Distance to Target: "+this.distanceToTarget+"\n" + "\n" 
				+"Evolution Phase: "+(this.evolver+1)+"\n" + "\n" ;
		if(this instanceof Evacuator) {
			x = x + "	Passengers:"+"\n" + "\n" ;
			if(((Evacuator)this).getPassengers().size() == 0) {
					x = x +"		EMPTY" +"\n "+"\n"; 
				}
			for(int i = 0 ; i < ((Evacuator)this).getPassengers().size() ; i++) {
					x = x + ((Evacuator)this).getPassengers().get(i).getStatus();
			}
		}
		return x;
	}
	
	public void setWorldListener(WorldListener listener) {
		this.worldListener = listener;
	}

	public WorldListener getWorldListener() {
		return worldListener;
	}

	public UnitState getState() {
		return state;
	}

	public void setState(UnitState state) {
		this.state = state;
	}

	public Address getLocation() {
		return location;
	}

	public void setLocation(Address location) {
		this.location = location;
	}

	public String getUnitID() {
		return unitID;
	}

	public Rescuable getTarget() {
		return target;
	}

	public int getStepsPerCycle() {
		return stepsPerCycle;
	}

	public void setDistanceToTarget(int distanceToTarget) {
		this.distanceToTarget = distanceToTarget;
	}

	@Override
	public void respond(Rescuable r) throws IncompatibleTargetException,CannotTreatException {
		if(target != null)
	    	  perTarget = target;
        if(this.compatible(r)){  /// Incompatible Target Exception
        	if (this.canTreat(r) && this.compatible2(r)){  ////Cannot Treat Exception
		      if (target != null && state == UnitState.TREATING) {
		    	  perTarget = target;
		    	  reactivateDisaster();
			  }
		      if(target != null && this.location == target.getLocation())
		    	  perTarget = target;
		      finishRespond(r);
        	}
        	else{
        		///Check which message to throw in the exception
        		String message="";
        		if (this.canTreat(r)){
        			message=this.targetMessage();
        		}
        		else{
        			message="The Target is Already Safe";
        		}
        		throw new CannotTreatException(this, r, message);
        	}
        }
        else{
        	throw new IncompatibleTargetException(this, r,this.targetMessage());
        }
	}
	
/////////////To check if the chosen unit is the right one for the disaster of the target 
	//////////////// if not a CannotTreatException is thrown
	public boolean compatible2(Rescuable r) {
		if (this instanceof Ambulance && r.getDisaster()instanceof Injury){
	         	 return true;
		}
		if (this instanceof DiseaseControlUnit && r.getDisaster()instanceof Infection){
        	 return true;
	    }
		if (this instanceof Evacuator && r.getDisaster()instanceof Collapse){
        	 return true;
	    }
		if (this instanceof FireTruck && r.getDisaster()instanceof Fire){
        	 return true;
	    }
		if (this instanceof GasControlUnit && r.getDisaster()instanceof GasLeak){
        	 return true;
	    }
		return false;
	
	   
	}

	public void reactivateDisaster() {
		perTarget = target;
		Disaster curr = target.getDisaster();
		curr.setActive(true);
	}

	public void finishRespond(Rescuable r) {
		perTarget = target;
		target = r;
		state = UnitState.RESPONDING;
		Address t = r.getLocation();
		distanceToTarget = Math.abs(t.getX() - location.getX())
				+ Math.abs(t.getY() - location.getY());

	}

	public String returnType() {
		if(this instanceof Evacuator) {
			return "eva1";
		}
		else {
			if(this instanceof FireTruck) {
				return "fir1";
			}
			else {
				if(this instanceof GasControlUnit) {
					return "gas1";
				}
				else {
					if(this instanceof Ambulance) {
						return "inj1";
					}
					else {
						return "inf1";
					}
				}
			}
		}
	}
	
	public abstract void treat();

	public void cycleStep() {
		if (state == UnitState.IDLE)
			return;
		if (distanceToTarget > 0) {
			distanceToTarget = distanceToTarget - stepsPerCycle;
			if (distanceToTarget <= 0) {
				distanceToTarget = 0;
				Address t = target.getLocation();
				worldListener.assignAddress(this, t.getX(), t.getY());
				arrived.UnitArrived(this, this.target, this.perTarget);
			}
		} else {
			state = UnitState.TREATING;
			treat();
		}
	}

	public void jobsDone() {
		perTarget = target;
		state = UnitState.IDLE;
		if(this instanceof Evacuator || this instanceof GasControlUnit || this instanceof DiseaseControlUnit) {
			if(evolver < 1) {
				evolver++;
				ev.evolve(this, evolver);
			}
			ev.evolve(this, evolver);
		}
		else {
			if(evolver < 2) {
				evolver++;
				ev.evolve(this, evolver);
			}
			ev.evolve(this, evolver);
		}
	}
	//////To Check the cannotTreatException in the case if the citizen or building is safe
	public boolean canTreat(Rescuable r){
			if (r.getDisaster()==null || r.getDisaster().isActive()==false){
				return false;
			}
			else
				return true;
	}
	/////To check if there is an IncompatibleTargetException 
	public boolean compatible(Rescuable r){
		if (this instanceof Ambulance || this instanceof DiseaseControlUnit){
				if (r instanceof Citizen)
		         	 return true;
				else 
					 return false;
		}
		else {
			if(r instanceof ResidentialBuilding)
			     return true;
			else 
				 return false;
		}
	}
	public String targetMessage(){
		if (this instanceof Ambulance){
			return "Ambulances can only treat Citizens with Injury Disasters";
		}
		if (this instanceof DiseaseControlUnit){
			return "Disease Control Units can only treat Citizens with Infection Disasters";
		}
		if (this instanceof FireTruck){
			return "Fire Trucks can only treat ResidentialBuildings with Fire Disasters";
		}
		if (this instanceof GasControlUnit){
			return "Gas Control Units can only treat ResidentialBuildings with Gas Leak Disasters";
		}
		else {
			return "Evacuators can only treat ResidentialBuildings with Collapse Disasters";
		}
	}

	public void setArrived(UnitArrivedListener arrived) {
		this.arrived = arrived;
	}

	public void setEv(EvolutionListener ev) {
		this.ev = ev;
	}
}